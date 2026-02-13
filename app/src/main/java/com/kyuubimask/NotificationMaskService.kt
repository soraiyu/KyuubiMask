/*
 * Copyright 2026 KyuubiMask Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kyuubimask

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.kyuubimask.data.PreferencesRepository
import java.util.Objects

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

    private lateinit var prefsRepository: PreferencesRepository

    companion object {
        // Debug broadcast
        const val ACTION_DEBUG_LOG = "com.kyuubimask.DEBUG_LOG"
        const val EXTRA_LOG_MESSAGE = "log_message"
        
        // Tag to identify masked notifications and prevent re-masking
        private const val MASKED_TAG = "kyuubimask_masked"
    }

    override fun onCreate() {
        super.onCreate()
        prefsRepository = PreferencesRepository(applicationContext)
    }

    private fun sendDebugLog(message: String) {
        if (!BuildConfig.DEBUG) return  // Only log in debug builds
        
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
        
        // Skip already masked notifications
        if (sbn.tag == MASKED_TAG) return

        // Check if service is enabled
        if (!prefsRepository.isServiceEnabled) return

        // Check if this app should be masked
        val isMasked = prefsRepository.isAppMasked(packageName)
        
        // Debug log - show all notifications (package name only, not content)
        val status = if (isMasked) "🦊 MASKED" else "⏭️ SKIP"
        sendDebugLog("$status: $packageName")

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
        // Use Objects.hash to avoid collision issues
        val notificationId = generateNotificationId(sbn)
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(MASKED_TAG, notificationId, maskedNotification)
        
        sendDebugLog("✅ Masked notification posted for: $appName")
    }
    
    /**
     * Generate a unique notification ID from StatusBarNotification
     * Uses Objects.hash to prevent collisions
     */
    private fun generateNotificationId(sbn: StatusBarNotification): Int {
        return Objects.hash(sbn.packageName, sbn.id, sbn.tag ?: "")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action needed - privacy first, don't track removals
    }
}
