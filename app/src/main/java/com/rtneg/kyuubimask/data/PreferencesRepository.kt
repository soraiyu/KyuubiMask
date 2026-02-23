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
        private const val KEY_SERVICE_ENABLED = "service_enabled"
        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
        private const val KEY_NOTIFICATION_VIBRATE = "notification_vibrate"
        private const val KEY_APP_ENABLED_PREFIX = "app_enabled_"
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
     * Returns whether masking is enabled for the given package name.
     * Defaults to true (enabled) when not explicitly set.
     */
    fun isAppEnabled(packageName: String): Boolean =
        preferences.getBoolean(KEY_APP_ENABLED_PREFIX + packageName, true)

    /**
     * Sets whether masking is enabled for the given package name.
     */
    fun setAppEnabled(packageName: String, enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_APP_ENABLED_PREFIX + packageName, enabled)
            .apply()
    }
}
