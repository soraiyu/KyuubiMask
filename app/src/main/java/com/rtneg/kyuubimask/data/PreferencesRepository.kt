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
package com.rtneg.kyuubimask.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Repository for managing app preferences
 * Centralizes all SharedPreferences access for better testability and maintainability
 */
class PreferencesRepository(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    companion object {
        const val PREFS_NAME = "kyuubi_prefs"
        private const val KEY_MASKED_APPS = "masked_apps"
        private const val KEY_SERVICE_ENABLED = "service_enabled"
        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
        private const val KEY_NOTIFICATION_VIBRATE = "notification_vibrate"
        
        // Default apps to mask
        // Privacy-first: Static list only, never fetched from network
        val DEFAULT_MASKED_APPS = setOf(
            // Messaging apps
            "com.whatsapp",              // WhatsApp
            "org.telegram.messenger",    // Telegram
            "jp.naver.line.android",     // LINE
            "org.thoughtcrime.securesms", // Signal
            "com.discord",               // Discord
            
            // Email apps
            "com.google.android.gm",     // Gmail
            "com.fsck.k9",               // K-9 Mail (F-Droid recommended)
            
            // Business/Productivity apps
            "com.slack",                 // Slack
            "com.microsoft.teams",       // Microsoft Teams
            "us.zoom.videomeetings",     // Zoom
            "com.notion.id",             // Notion
            "com.atlassian.jira.core.ui" // Jira
        )
    }
    
    /**
     * Whether the notification masking service is enabled
     */
    var isServiceEnabled: Boolean
        get() = preferences.getBoolean(KEY_SERVICE_ENABLED, true)
        set(value) {
            preferences.edit()
                .putBoolean(KEY_SERVICE_ENABLED, value)
                .apply()
        }
    
    /**
     * Whether to play sound for masked notifications
     */
    var notificationSound: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATION_SOUND, true)
        set(value) {
            preferences.edit()
                .putBoolean(KEY_NOTIFICATION_SOUND, value)
                .apply()
        }
    
    /**
     * Whether to vibrate for masked notifications
     */
    var notificationVibrate: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATION_VIBRATE, true)
        set(value) {
            preferences.edit()
                .putBoolean(KEY_NOTIFICATION_VIBRATE, value)
                .apply()
        }
    
    /**
     * Set of package names for apps whose notifications should be masked
     */
    var maskedApps: Set<String>
        get() = preferences.getStringSet(KEY_MASKED_APPS, DEFAULT_MASKED_APPS) 
            ?: DEFAULT_MASKED_APPS
        set(value) {
            preferences.edit()
                .putStringSet(KEY_MASKED_APPS, value)
                .apply()
        }
    
    /**
     * Check if a specific app's notifications should be masked
     */
    fun isAppMasked(packageName: String): Boolean {
        return packageName in maskedApps
    }
    
    /**
     * Add an app to the masked apps list
     */
    fun addMaskedApp(packageName: String) {
        val current = maskedApps.toMutableSet()
        if (current.add(packageName)) {
            maskedApps = current
        }
    }
    
    /**
     * Remove an app from the masked apps list
     */
    fun removeMaskedApp(packageName: String) {
        val current = maskedApps.toMutableSet()
        if (current.remove(packageName)) {
            maskedApps = current
        }
    }
}
