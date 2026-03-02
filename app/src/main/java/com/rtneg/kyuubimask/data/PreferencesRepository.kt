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
import com.rtneg.kyuubimask.VibrationPatterns

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
        private const val KEY_VIBE_PATTERN = VibrationPatterns.PREF_KEY_VIBE_PATTERN
        private const val KEY_APP_ENABLED_PREFIX = "app_enabled_"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_USER_SELECTED_PACKAGES = "user_selected_packages"

        /** Broadcast action sent when masking is toggled (e.g. from Quick Settings tile). */
        const val ACTION_MASK_TOGGLED = "com.rtneg.kyuubimask.ACTION_MASK_TOGGLED"

        /** Broadcast extra – Boolean, the new masking-enabled state. */
        const val EXTRA_ENABLED = "enabled"
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
     * Currently selected vibration pattern key.
     */
    var vibrationPattern: String
        get() = preferences.getString(KEY_VIBE_PATTERN, VibrationPatterns.DEFAULT_VIBE_PATTERN)
            ?: VibrationPatterns.DEFAULT_VIBE_PATTERN
        set(value) {
            preferences.edit()
                .putString(KEY_VIBE_PATTERN, value)
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

    /**
     * Whether this is the first time the app has been launched.
     * Defaults to true; set to false after the privacy dialog is shown.
     */
    var isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        set(value) {
            preferences.edit()
                .putBoolean(KEY_IS_FIRST_LAUNCH, value)
                .apply()
        }

    /**
     * Returns the set of user-selected package names to mask.
     * These are additional apps beyond the built-in presets.
     */
    fun getUserSelectedPackages(): Set<String> =
        preferences.getStringSet(KEY_USER_SELECTED_PACKAGES, emptySet())?.toSet() ?: emptySet()

    /**
     * Adds a package to the user-selected set. No-op if already present.
     */
    fun addUserSelectedPackage(packageName: String) {
        val current = getUserSelectedPackages()
        if (packageName in current) return
        preferences.edit().putStringSet(KEY_USER_SELECTED_PACKAGES, current + packageName).apply()
    }

    /**
     * Removes a package from the user-selected set. No-op if not present.
     */
    fun removeUserSelectedPackage(packageName: String) {
        val current = getUserSelectedPackages()
        if (packageName !in current) return
        preferences.edit().putStringSet(KEY_USER_SELECTED_PACKAGES, current - packageName).apply()
    }
}
