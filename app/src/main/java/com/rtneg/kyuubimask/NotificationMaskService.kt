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
package com.rtneg.kyuubimask

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.rtneg.kyuubimask.data.PreferencesRepository
import java.util.Objects

/**
 * NotificationMaskService - Core notification masking service
 * 
 * PRIVACY FIRST:
 * - No logging of notification content
 * - No data storage
 * - No network access
 * - Original notification content is never persisted
 * 
 * OPTIMIZED FOR MEMORY:
 * - Minimal memory footprint
 * - No in-memory state tracking
 * - No debug logging
 * - Runs as foreground service for better persistence
 */
class NotificationMaskService : NotificationListenerService() {

    private lateinit var prefsRepository: PreferencesRepository
    
    // Track notification keys being processed to prevent re-entrancy
    private val processingKeys = mutableSetOf<String>()

    companion object {
        // Tag to identify masked notifications in the notification system
        private const val MASKED_TAG = "kyuubimask_masked"
        
        // Key for extras Bundle to mark a notification as already masked
        private const val EXTRA_KEY_IS_MASKED = "kyuubimask_is_masked"
        
        // Foreground service notification channel and ID
        private const val FOREGROUND_CHANNEL_ID = "kyuubimask_service"
        private const val FOREGROUND_NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        prefsRepository = PreferencesRepository(applicationContext)
        
        // Start as foreground service to prevent Android from killing it
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop foreground service
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    /**
     * Create notification for foreground service
     * This keeps the service running and prevents Android from killing it
     */
    private fun createForegroundNotification(): Notification {
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                getString(R.string.service_channel_name),
                NotificationManager.IMPORTANCE_LOW // Low importance to avoid disturbing user
            ).apply {
                description = getString(R.string.service_channel_description)
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open settings when notification is tapped
        val notificationIntent = Intent(this, SettingsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        return androidx.core.app.NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle(getString(R.string.service_running_title))
            .setContentText(getString(R.string.service_running_text))
            .setSmallIcon(R.drawable.ic_mask)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Cannot be dismissed by user
            .setShowWhen(false)
            .build()
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
        
        // Skip already masked notifications by checking tag or extras
        if (sbn.tag == MASKED_TAG) return
        
        // Also check notification extras for the masked flag
        // If notification or extras is null (shouldn't happen in practice), we proceed with normal processing
        sbn.notification?.extras?.let { extras ->
            if (extras.getBoolean(EXTRA_KEY_IS_MASKED, false)) return
        }
        
        // Skip if this notification is already being processed
        // This prevents race conditions when apps re-post notifications
        val notificationKey = sbn.key
        if (processingKeys.contains(notificationKey)) return

        // Check if service is enabled
        if (!prefsRepository.isServiceEnabled) return

        // Check if this app should be masked
        if (prefsRepository.isAppMasked(packageName)) {
            maskNotification(sbn)
        }
    }

    /**
     * Masks a notification by canceling original and posting a generic one
     * PRIVACY: No content from original notification is logged or stored
     */
    private fun maskNotification(sbn: StatusBarNotification) {
        val notificationKey = sbn.key
        
        // Mark this notification as being processed
        processingKeys.add(notificationKey)
        
        try {
            // Check POST_NOTIFICATIONS permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }
            
            val packageName = sbn.packageName
            
            // Cancel the original notification
            cancelNotification(sbn.key)

            // Get app name for the masked notification title
            val appName = try {
                val appInfo = packageManager.getApplicationInfo(sbn.packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                "App" // Fallback if app name can't be retrieved
            }

            // Generate unique notification ID
            val notificationId = generateNotificationId(sbn)

            // Create PendingIntent to open the masked app when notification is tapped
            // Uses original notification's contentIntent to enable deep linking (e.g., specific chat/message)
            // PRIVACY: PendingIntent is opaque; notification content remains masked
            // LIMITATION: Deep link may indirectly reveal context (acceptable UX trade-off)
            // SECURITY: Safe because sbn.packageName is verified in onNotificationPosted() before calling maskNotification()
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
                    contentIntent?.let { setContentIntent(it) }
                    
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
                    
                    // Mark this notification as already masked to prevent re-processing
                    // Use addExtras to preserve existing notification data (title, text, etc.)
                    addExtras(android.os.Bundle().apply {
                        putBoolean(EXTRA_KEY_IS_MASKED, true)
                    })
                }
                .build()

            // Post the masked notification with unique ID
            val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.notify(MASKED_TAG, notificationId, maskedNotification)
        } finally {
            // Remove from processing set after a delay to handle race conditions
            // Use a short delay to allow the notification system to process the cancellation
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                processingKeys.remove(notificationKey)
            }, 500) // 500ms delay should be sufficient
        }
    }
    
    /**
     * Generate a unique notification ID from StatusBarNotification
     * Uses Objects.hash to prevent collisions
     */
    private fun generateNotificationId(sbn: StatusBarNotification): Int {
        return Objects.hash(sbn.packageName, sbn.id, sbn.tag ?: "")
    }
}
