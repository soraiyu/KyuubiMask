package com.kyuubimask

import android.app.Notification
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
    }

    /**
     * Called when a new notification is posted
     * If from a masked app, cancel original and post generic masked version
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Check if service is enabled
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_SERVICE_ENABLED, true)) return

        // Get list of apps to mask
        val maskedApps = prefs.getStringSet(KEY_MASKED_APPS, DEFAULT_MASKED_APPS) 
            ?: DEFAULT_MASKED_APPS

        val packageName = sbn.packageName

        // Skip our own notifications to prevent infinite loop
        if (packageName == this.packageName) return

        // Check if this app should be masked
        if (packageName in maskedApps) {
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
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action needed - privacy first, don't track removals
    }
}
