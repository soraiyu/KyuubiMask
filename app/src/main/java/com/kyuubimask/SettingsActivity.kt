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
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    
    // Request notification permission for Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            addDebugLog("✅ POST_NOTIFICATIONS permission granted")
            Toast.makeText(this, R.string.post_notification_permission_granted, Toast.LENGTH_SHORT).show()
        } else {
            addDebugLog("❌ POST_NOTIFICATIONS permission denied")
            Toast.makeText(this, R.string.error_post_notification_denied, Toast.LENGTH_LONG).show()
        }
        updateServiceStatus()
    }
    
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
        
        // Check and request notification permission on Android 13+
        checkNotificationPermission()
        
        // Register debug receiver only in debug builds
        if (BuildConfig.DEBUG) {
            val filter = IntentFilter(NotificationMaskService.ACTION_DEBUG_LOG)
            ContextCompat.registerReceiver(
                this,
                debugReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
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
    
    /**
     * Check notification permission for Android 13+
     */
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    addDebugLog("✅ POST_NOTIFICATIONS permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale and request permission
                    Toast.makeText(
                        this,
                        R.string.post_notification_permission_required,
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Request permission directly
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    /**
     * Check if POST_NOTIFICATIONS permission is granted (Android 13+)
     */
    private fun hasPostNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission not required for Android < 13
            true
        }
    }

    private fun setupUI() {
        // Service enable toggle
        binding.switchEnable.isChecked = prefsRepository.isServiceEnabled
        binding.switchEnable.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.isServiceEnabled = isChecked
            updateServiceStatus()
            addDebugLog(if (isChecked) "🟢 Masking enabled" else "🔴 Masking disabled")
        }
        
        // Notification sound toggle
        binding.switchSound.isChecked = prefsRepository.notificationSound
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.notificationSound = isChecked
            addDebugLog(if (isChecked) "🔊 Sound enabled" else "🔇 Sound disabled")
        }
        
        // Notification vibrate toggle
        binding.switchVibrate.isChecked = prefsRepository.notificationVibrate
        binding.switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.notificationVibrate = isChecked
            addDebugLog(if (isChecked) "📳 Vibrate enabled" else "📴 Vibrate disabled")
        }

        // Permission button
        binding.btnPermission.setOnClickListener {
            openNotificationListenerSettings()
        }

        // App selection chips
        val maskedApps = prefsRepository.maskedApps.toMutableSet()

        // Messaging Apps
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

        // LINE
        binding.chipLine.isChecked = prefsRepository.isAppMasked("jp.naver.line.android")
        binding.chipLine.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("jp.naver.line.android", isChecked, maskedApps)
        }

        // Signal
        binding.chipSignal.isChecked = prefsRepository.isAppMasked("org.thoughtcrime.securesms")
        binding.chipSignal.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("org.thoughtcrime.securesms", isChecked, maskedApps)
        }

        // Discord
        binding.chipDiscord.isChecked = prefsRepository.isAppMasked("com.discord")
        binding.chipDiscord.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.discord", isChecked, maskedApps)
        }

        // Email Apps
        // Gmail
        binding.chipGmail.isChecked = prefsRepository.isAppMasked("com.google.android.gm")
        binding.chipGmail.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.google.android.gm", isChecked, maskedApps)
        }

        // K-9 Mail
        binding.chipK9Mail.isChecked = prefsRepository.isAppMasked("com.fsck.k9")
        binding.chipK9Mail.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.fsck.k9", isChecked, maskedApps)
        }

        // Business Apps
        // Slack
        binding.chipSlack.isChecked = prefsRepository.isAppMasked("com.slack")
        binding.chipSlack.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.slack", isChecked, maskedApps)
        }

        // Teams
        binding.chipTeams.isChecked = prefsRepository.isAppMasked("com.microsoft.teams")
        binding.chipTeams.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.microsoft.teams", isChecked, maskedApps)
        }

        // Zoom
        binding.chipZoom.isChecked = prefsRepository.isAppMasked("us.zoom.videomeetings")
        binding.chipZoom.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("us.zoom.videomeetings", isChecked, maskedApps)
        }

        // Notion
        binding.chipNotion.isChecked = prefsRepository.isAppMasked("com.notion.id")
        binding.chipNotion.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.notion.id", isChecked, maskedApps)
        }

        // Jira
        binding.chipJira.isChecked = prefsRepository.isAppMasked("com.atlassian.jira.core.ui")
        binding.chipJira.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.atlassian.jira.core.ui", isChecked, maskedApps)
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
        val hasNotificationListenerPermission = isNotificationServiceEnabled()
        val hasPostNotificationPermission = hasPostNotificationPermission()
        val isEnabled = prefsRepository.isServiceEnabled

        binding.tvStatus.text = when {
            !hasNotificationListenerPermission -> getString(R.string.status_permission_required)
            !hasPostNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> 
                getString(R.string.post_notification_permission_required)
            isEnabled -> getString(R.string.status_active)
            else -> getString(R.string.status_disabled)
        }

        val isFullyOperational = hasNotificationListenerPermission && 
                                 hasPostNotificationPermission && 
                                 isEnabled
        
        binding.tvStatus.setTextColor(
            getColor(
                if (isFullyOperational) R.color.status_active
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
