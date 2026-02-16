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

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
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
 * - Notification grouping state stored in memory only (cleared on service restart)
 */
class NotificationMaskService : NotificationListenerService() {

    private lateinit var prefsRepository: PreferencesRepository
    
    // PRIVACY: Memory-only storage, cleared when service stops
    // Only stores package names and counts, never notification content
    private val notificationCounts = mutableMapOf<String, Int>()

    companion object {
        // Debug broadcast
        const val ACTION_DEBUG_LOG = "com.kyuubimask.DEBUG_LOG"
        const val EXTRA_LOG_MESSAGE = "log_message"
        
        // Tag to identify masked notifications and prevent re-masking
        private const val MASKED_TAG = "kyuubimask_masked"
        
        // Group prefix for notification grouping
        private const val GROUP_PREFIX = "kyuubimask_group_"
    }

    override fun onCreate() {
        super.onCreate()
        prefsRepository = PreferencesRepository(applicationContext)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // PRIVACY: Clear all in-memory state when service stops
        notificationCounts.clear()
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
     * 
     * Notifications are grouped by app package name for better organization.
     * Group state is stored in memory only and cleared when service restarts.
     */
    private fun maskNotification(sbn: StatusBarNotification) {
        // Check POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                sendDebugLog("❌ POST_NOTIFICATIONS permission not granted")
                return
            }
        }
        
        val packageName = sbn.packageName
        
        // PRIVACY: Increment count in memory only (never persisted)
        val currentCount = notificationCounts.getOrDefault(packageName, 0) + 1
        notificationCounts[packageName] = currentCount
        
        // Cancel the original notification
        cancelNotification(sbn.key)

        // Get app name for the masked notification title
        val appName = try {
            val appInfo = packageManager.getApplicationInfo(sbn.packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            "App" // Fallback if app name can't be retrieved
        }

        // Generate unique notification ID once for both PendingIntent and notification
        val notificationId = generateNotificationId(sbn)

        // Create PendingIntent to open the masked app when notification is tapped
        // PRIVACY NOTE: Uses original notification's contentIntent to enable deep linking.
        // - PendingIntent is an opaque object - our service cannot access its internal data/URIs
        // - The notification content itself remains masked (title, text, etc. are still hidden)
        // - Trade-off: Enables better UX (deep linking) while maintaining content privacy
        // - Falls back to launch intent if original notification has no contentIntent
        val contentIntent = sbn.notification.contentIntent ?: run {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                // Set flags to bring app to front or reuse existing instance
                launchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or 
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                
                // Create PendingIntent with appropriate flags for the Android version
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                
                // Use notification ID as request code to ensure uniqueness per notification
                PendingIntent.getActivity(this, notificationId, launchIntent, flags)
            } else {
                null
            }
        }

        // Build masked notification with grouping
        // PRIVACY: Generic text only, no original content
        val maskedNotification = NotificationCompat.Builder(this, KyuubiMaskApp.CHANNEL_ID)
            .setContentTitle(appName)
            .setContentText(getString(R.string.masked_text))
            .setSmallIcon(R.drawable.ic_mask)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                // Group notifications by app package name
                // This makes notifications from the same app appear together
                setGroup("$GROUP_PREFIX$packageName")
                setGroupSummary(false)
                
                // Preserve original sort key if available
                sbn.notification.sortKey?.let { setSortKey(it) }
                
                // Apply sound and vibration settings based on user preferences
                var defaults = Notification.DEFAULT_LIGHTS // Always use default lights
                if (prefsRepository.notificationSound) {
                    defaults = defaults or Notification.DEFAULT_SOUND
                }
                if (prefsRepository.notificationVibrate) {
                    defaults = defaults or Notification.DEFAULT_VIBRATE
                }
                setDefaults(defaults)
            }
            .build()

        // Post the masked notification with unique ID
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(MASKED_TAG, notificationId, maskedNotification)
        
        sendDebugLog("✅ Masked notification posted for: $appName (count: $currentCount)")
    }
    
    /**
     * Generate a unique notification ID from StatusBarNotification
     * Uses Objects.hash to prevent collisions
     */
    private fun generateNotificationId(sbn: StatusBarNotification): Int {
        return Objects.hash(sbn.packageName, sbn.id, sbn.tag ?: "")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn ?: return
        
        // Skip our own notifications
        if (sbn.packageName == this.packageName) return
        if (sbn.tag == MASKED_TAG) return
        
        // PRIVACY: Decrement count in memory only (never persisted)
        // Only track counts for masked apps to maintain privacy
        if (prefsRepository.isAppMasked(sbn.packageName)) {
            val currentCount = notificationCounts.getOrDefault(sbn.packageName, 0)
            if (currentCount > 0) {
                notificationCounts[sbn.packageName] = currentCount - 1
            }
            // Clean up if count reaches zero
            if (notificationCounts[sbn.packageName] == 0) {
                notificationCounts.remove(sbn.packageName)
            }
        }
    }
}
