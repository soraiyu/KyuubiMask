package com.kyuubimask

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat

/**
 * NotificationMaskService - Core notification masking service
 * 
 * PRIVACY FIRST:
 * - No logging of notification content
 * - No data storage
 * - No network access
 * - Original notification content is never persisted
 */
class NotificationMaskService : NotificationListenerService() {

    companion object {
        // Default apps to mask - configurable via SharedPreferences
        val DEFAULT_MASKED_APPS = setOf(
            "com.whatsapp",
            "org.telegram.messenger", 
            "com.google.android.gm",
            "jp.naver.line.android"
        )

        // SharedPreferences key
        const val PREFS_NAME = "kyuubi_prefs"
        const val KEY_MASKED_APPS = "masked_apps"
        const val KEY_SERVICE_ENABLED = "service_enabled"
        
        // Debug broadcast
        const val ACTION_DEBUG_LOG = "com.kyuubimask.DEBUG_LOG"
        const val EXTRA_LOG_MESSAGE = "log_message"
    }

    private fun sendDebugLog(message: String) {
        val intent = Intent(ACTION_DEBUG_LOG).apply {
            putExtra(EXTRA_LOG_MESSAGE, message)
            setPackage(packageName) // Send only to our app
        }
        sendBroadcast(intent)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        sendDebugLog("✅ Service connected!")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        sendDebugLog("❌ Service disconnected")
    }

    /**
     * Called when a new notification is posted
     * If from a masked app, cancel original and post generic masked version
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        val packageName = sbn.packageName
        
        // Skip our own notifications to prevent infinite loop
        if (packageName == this.packageName) return

        // Check if service is enabled
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(KEY_SERVICE_ENABLED, true)
        
        // Get list of apps to mask
        val maskedApps = prefs.getStringSet(KEY_MASKED_APPS, DEFAULT_MASKED_APPS) 
            ?: DEFAULT_MASKED_APPS

        // Debug log - show all notifications (package name only, not content)
        val isMasked = packageName in maskedApps
        val status = if (isMasked && isEnabled) "🦊 MASKED" else "⏭️ SKIP"
        sendDebugLog("$status: $packageName")

        if (!isEnabled) return

        // Check if this app should be masked
        if (isMasked) {
            maskNotification(sbn)
        }
    }

    /**
     * Masks a notification by canceling original and posting a generic one
     * PRIVACY: No content from original notification is logged or stored
     */
    private fun maskNotification(sbn: StatusBarNotification) {
        // Cancel the original notification
        cancelNotification(sbn.key)

        // Get app name for the masked notification title
        val appName = try {
            val appInfo = packageManager.getApplicationInfo(sbn.packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            "App" // Fallback if app name can't be retrieved
        }

        // Build masked notification
        // PRIVACY: Generic text only, no original content
        val maskedNotification = NotificationCompat.Builder(this, KyuubiMaskApp.CHANNEL_ID)
            .setContentTitle(appName)
            .setContentText(getString(R.string.masked_text))
            .setSmallIcon(R.drawable.ic_mask)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .apply {
                // Preserve grouping from original notification
                sbn.notification.group?.let { setGroup(it) }
                sbn.notification.sortKey?.let { setSortKey(it) }
                
                // Inherit defaults for sound/vibration
                setDefaults(Notification.DEFAULT_ALL)
            }
            .build()

        // Post the masked notification with unique ID based on original
        val notificationId = sbn.id + sbn.packageName.hashCode()
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(notificationId, maskedNotification)
        
        sendDebugLog("✅ Masked notification posted for: $appName")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action needed - privacy first, don't track removals
    }
}
