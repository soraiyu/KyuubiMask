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
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rtneg.kyuubimask.data.DebugLogRepository
import com.rtneg.kyuubimask.data.PreferencesRepository
import com.rtneg.kyuubimask.databinding.ActivitySettingsBinding
import com.rtneg.kyuubimask.strategy.DiscordMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * SettingsActivity - Main UI for configuring KyuubiMask
 * Minimal UI focused on privacy and simplicity
 * Optimized for low memory usage
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefsRepository: PreferencesRepository

    // Debug log polling (DEBUG builds only)
    private val debugHandler = Handler(Looper.getMainLooper())
    private val debugRefreshRunnable = object : Runnable {
        override fun run() {
            refreshDebugLog()
            debugHandler.postDelayed(this, DEBUG_POLL_INTERVAL_MS)
        }
    }

    companion object {
        private const val DEBUG_POLL_INTERVAL_MS = 1000L
    }
    
    // Request notification permission for Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, R.string.post_notification_permission_granted, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.error_post_notification_denied, Toast.LENGTH_LONG).show()
        }
        updateServiceStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsRepository = (applicationContext as KyuubiMaskApp).prefsRepository
        
        setupUI()
        updateServiceStatus()
        
        // Check and request notification permission on Android 13+
        checkNotificationPermission()
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
        if (BuildConfig.DEBUG) {
            refreshDebugLog()
            debugHandler.postDelayed(debugRefreshRunnable, DEBUG_POLL_INTERVAL_MS)
        }
    }

    override fun onPause() {
        super.onPause()
        debugHandler.removeCallbacks(debugRefreshRunnable)
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
        // Version info
        binding.tvVersion.text = getString(R.string.label_version, BuildConfig.VERSION_NAME)

        // Service enable toggle
        binding.switchEnable.isChecked = prefsRepository.isServiceEnabled
        binding.switchEnable.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.isServiceEnabled = isChecked
            updateServiceStatus()
        }
        
        // Notification sound toggle
        binding.switchSound.isChecked = prefsRepository.notificationSound
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.notificationSound = isChecked
        }
        
        // Notification vibrate toggle
        binding.switchVibrate.isChecked = prefsRepository.notificationVibrate
        binding.switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.notificationVibrate = isChecked
        }

        // Apps to Mask toggles
        setupAppSwitch(binding.switchSlack, SlackMaskStrategy.SLACK_PACKAGE)
        setupAppSwitch(binding.switchDiscord, DiscordMaskStrategy.DISCORD_PACKAGE)
        setupAppSwitch(binding.switchWhatsApp, WhatsAppMaskStrategy.WHATSAPP_PACKAGE)
        setupAppSwitch(binding.switchLine, LineMaskStrategy.LINE_PACKAGE)

        // Permission button
        binding.btnPermission.setOnClickListener {
            openNotificationListenerSettings()
        }

        // Debug log panel (DEBUG builds only)
        if (BuildConfig.DEBUG) {
            binding.cardDebugLog.visibility = View.VISIBLE
            binding.btnClearLog.setOnClickListener {
                DebugLogRepository.clear()
                refreshDebugLog()
            }
        }
    }

    /** Refresh the in-app debug log display from the in-memory buffer. */
    private fun refreshDebugLog() {
        val entries = DebugLogRepository.entries()
        binding.tvDebugLog.text = if (entries.isEmpty()) {
            getString(R.string.debug_waiting)
        } else {
            entries.joinToString("\n")
        }
    }

    /**
     * Initialises a per-app masking switch: sets the current saved state and
     * persists any change the user makes.
     */
    private fun setupAppSwitch(switch: SwitchMaterial, packageName: String) {
        switch.isChecked = prefsRepository.isAppEnabled(packageName)
        switch.setOnCheckedChangeListener { _, isChecked ->
            prefsRepository.setAppEnabled(packageName, isChecked)
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
