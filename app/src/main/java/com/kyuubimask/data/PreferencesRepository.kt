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
package com.kyuubimask.data

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
        
        // Default apps to mask
        val DEFAULT_MASKED_APPS = setOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.google.android.gm",
            "jp.naver.line.android"
        )
        
        // Constants
        const val MAX_DEBUG_LOGS = 50
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
