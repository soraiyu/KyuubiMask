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

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kyuubimask.data.PreferencesRepository
import com.kyuubimask.databinding.ActivitySettingsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SettingsActivity - Main UI for configuring KyuubiMask
 * Minimal UI focused on privacy and simplicity
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefsRepository: PreferencesRepository
    private val debugLogs = mutableListOf<String>()
    
    private val debugReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra(NotificationMaskService.EXTRA_LOG_MESSAGE)?.let { message ->
                addDebugLog(message)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsRepository = PreferencesRepository(applicationContext)
        
        setupUI()
        updateServiceStatus()
        
        // Register debug receiver only in debug builds
        if (BuildConfig.DEBUG) {
            val filter = IntentFilter(NotificationMaskService.ACTION_DEBUG_LOG)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(debugReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(debugReceiver, filter)
            }
        }
        
        // Clear log button
        binding.btnClearLog.setOnClickListener {
            debugLogs.clear()
            binding.tvDebugLog.text = "Waiting for notifications..."
        }
        
        addDebugLog("App started. Waiting for notifications...")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) {
            try {
                unregisterReceiver(debugReceiver)
            } catch (e: IllegalArgumentException) {
                // Receiver not registered, ignore
            }
        }
    }
    
    private fun addDebugLog(message: String) {
        if (!BuildConfig.DEBUG) return
        
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "[$timestamp] $message"
        
        debugLogs.add(0, logEntry) // Add to top
        if (debugLogs.size > PreferencesRepository.MAX_DEBUG_LOGS) {
            debugLogs.removeAt(debugLogs.size - 1)
        }
        
        binding.tvDebugLog.text = debugLogs.joinToString("\n")
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }

    private fun setupUI() {
        // Service enable toggle
        binding.switchEnable.isChecked = prefsRepository.isServiceEnabled
        binding.switchEnable.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.isServiceEnabled = isChecked
            updateServiceStatus()
            addDebugLog(if (isChecked) "🟢 Masking enabled" else "🔴 Masking disabled")
        }

        // Permission button
        binding.btnPermission.setOnClickListener {
            openNotificationListenerSettings()
        }

        // App selection chips
        val maskedApps = prefsRepository.maskedApps.toMutableSet()

        // WhatsApp
        binding.chipWhatsapp.isChecked = prefsRepository.isAppMasked("com.whatsapp")
        binding.chipWhatsapp.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.whatsapp", isChecked, maskedApps)
        }

        // Telegram
        binding.chipTelegram.isChecked = prefsRepository.isAppMasked("org.telegram.messenger")
        binding.chipTelegram.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("org.telegram.messenger", isChecked, maskedApps)
        }

        // Gmail
        binding.chipGmail.isChecked = prefsRepository.isAppMasked("com.google.android.gm")
        binding.chipGmail.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.google.android.gm", isChecked, maskedApps)
        }

        // LINE
        binding.chipLine.isChecked = prefsRepository.isAppMasked("jp.naver.line.android")
        binding.chipLine.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("jp.naver.line.android", isChecked, maskedApps)
        }
    }

    /**
     * Updates the masked apps set in SharedPreferences
     */
    private fun updateMaskedApp(packageName: String, add: Boolean, currentSet: MutableSet<String>) {
        if (add) {
            prefsRepository.addMaskedApp(packageName)
        } else {
            prefsRepository.removeMaskedApp(packageName)
        }
    }

    /**
     * Checks if notification listener permission is granted
     */
    private fun isNotificationServiceEnabled(): Boolean {
        val componentName = ComponentName(this, NotificationMaskService::class.java)
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(componentName.flattenToString()) == true
    }

    /**
     * Updates UI based on service status
     */
    private fun updateServiceStatus() {
        val hasPermission = isNotificationServiceEnabled()
        val isEnabled = prefsRepository.isServiceEnabled

        binding.tvStatus.text = when {
            !hasPermission -> getString(R.string.status_permission_required)
            isEnabled -> getString(R.string.status_active)
            else -> getString(R.string.status_disabled)
        }

        binding.tvStatus.setTextColor(
            getColor(
                if (hasPermission && isEnabled) R.color.status_active
                else R.color.status_inactive
            )
        )
    }

    /**
     * Opens system notification listener settings
     */
    private fun openNotificationListenerSettings() {
        try {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_open_settings, Toast.LENGTH_SHORT).show()
        }
    }
}
