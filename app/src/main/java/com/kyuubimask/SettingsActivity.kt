package com.kyuubimask

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kyuubimask.databinding.ActivitySettingsBinding

/**
 * SettingsActivity - Main UI for configuring KyuubiMask
 * Minimal UI focused on privacy and simplicity
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        updateServiceStatus()
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }

    private fun setupUI() {
        val prefs = getSharedPreferences(NotificationMaskService.PREFS_NAME, MODE_PRIVATE)

        // Service enable toggle
        binding.switchEnable.isChecked = prefs.getBoolean(
            NotificationMaskService.KEY_SERVICE_ENABLED, true
        )
        binding.switchEnable.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(NotificationMaskService.KEY_SERVICE_ENABLED, isChecked).apply()
            updateServiceStatus()
        }

        // Permission button
        binding.btnPermission.setOnClickListener {
            openNotificationListenerSettings()
        }

        // App selection chips
        val maskedApps = prefs.getStringSet(
            NotificationMaskService.KEY_MASKED_APPS,
            NotificationMaskService.DEFAULT_MASKED_APPS
        )?.toMutableSet() ?: NotificationMaskService.DEFAULT_MASKED_APPS.toMutableSet()

        // WhatsApp
        binding.chipWhatsapp.isChecked = "com.whatsapp" in maskedApps
        binding.chipWhatsapp.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.whatsapp", isChecked, maskedApps)
        }

        // Telegram
        binding.chipTelegram.isChecked = "org.telegram.messenger" in maskedApps
        binding.chipTelegram.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("org.telegram.messenger", isChecked, maskedApps)
        }

        // Gmail
        binding.chipGmail.isChecked = "com.google.android.gm" in maskedApps
        binding.chipGmail.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("com.google.android.gm", isChecked, maskedApps)
        }

        // LINE
        binding.chipLine.isChecked = "jp.naver.line.android" in maskedApps
        binding.chipLine.setOnCheckedChangeListener { _, isChecked ->
            updateMaskedApp("jp.naver.line.android", isChecked, maskedApps)
        }
    }

    /**
     * Updates the masked apps set in SharedPreferences
     */
    private fun updateMaskedApp(packageName: String, add: Boolean, currentSet: MutableSet<String>) {
        if (add) {
            currentSet.add(packageName)
        } else {
            currentSet.remove(packageName)
        }

        getSharedPreferences(NotificationMaskService.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putStringSet(NotificationMaskService.KEY_MASKED_APPS, currentSet)
            .apply()
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
        val isEnabled = getSharedPreferences(NotificationMaskService.PREFS_NAME, MODE_PRIVATE)
            .getBoolean(NotificationMaskService.KEY_SERVICE_ENABLED, true)

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
