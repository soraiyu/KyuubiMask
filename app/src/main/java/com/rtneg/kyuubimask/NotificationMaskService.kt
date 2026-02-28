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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rtneg.kyuubimask.data.DebugLogRepository
import com.rtneg.kyuubimask.data.PreferencesRepository

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

    companion object {
        // Foreground service notification channel and ID
        private const val FOREGROUND_CHANNEL_ID = "kyuubimask_service"
        private const val FOREGROUND_NOTIFICATION_ID = 1001
        private const val TAG = "KyuubiMask"
    }

    override fun onCreate() {
        super.onCreate()
        prefsRepository = (applicationContext as KyuubiMaskApp).prefsRepository
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Service created")
            DebugLogRepository.add("Service started")
        }
        
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
        
        // Skip already masked notifications
        if (sbn.tag == NotificationMaskStrategy.MASKED_TAG) return

        // Check if service is enabled
        if (!prefsRepository.isServiceEnabled) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Service disabled – skipping $packageName")
                DebugLogRepository.add("Disabled – skipped: $packageName")
            }
            return
        }

        // Delegate to strategy registry for app-specific masking
        // The registry is the single source of truth for which apps are masked
        val strategy = NotificationMaskStrategyRegistry.findStrategy(packageName)
        if (strategy != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Masking notification from $packageName")
                DebugLogRepository.add("Masking: $packageName")
            }
            strategy.mask(sbn, this)
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No strategy for $packageName – passing through")
                DebugLogRepository.add("Pass-through: $packageName")
            }
        }
    }
}
