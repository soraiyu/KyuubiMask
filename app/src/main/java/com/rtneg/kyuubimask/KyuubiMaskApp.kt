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

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.rtneg.kyuubimask.data.PreferencesRepository

/**
 * KyuubiMask Application class
 * Handles notification channel creation on app startup
 */
class KyuubiMaskApp : Application() {

    /** Singleton repository shared across all components in this process. */
    val prefsRepository: PreferencesRepository by lazy {
        PreferencesRepository(applicationContext)
    }

    companion object {
        const val CHANNEL_ID = "masked_notifications"
        const val CHANNEL_NAME = "Masked Notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Creates the notification channel for masked notifications
     * Required for Android 8.0+ (API 26+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for masked notifications"
                // Privacy: Don't show content on lock screen
                lockscreenVisibility = android.app.Notification.VISIBILITY_PRIVATE
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
