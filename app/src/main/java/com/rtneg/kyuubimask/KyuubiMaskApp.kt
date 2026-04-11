/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
