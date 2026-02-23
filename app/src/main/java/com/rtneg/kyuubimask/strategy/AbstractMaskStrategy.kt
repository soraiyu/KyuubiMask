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
package com.rtneg.kyuubimask.strategy

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.rtneg.kyuubimask.BuildConfig
import com.rtneg.kyuubimask.KyuubiMaskApp
import com.rtneg.kyuubimask.NotificationMaskStrategy
import com.rtneg.kyuubimask.R
import com.rtneg.kyuubimask.data.DebugLogRepository
import com.rtneg.kyuubimask.data.PreferencesRepository
import java.util.Objects

/**
 * Abstract base class containing common masking logic.
 *
 * Minimum steps to add support for a new app:
 * 1. Create a class extending this class (e.g., DiscordMaskStrategy)
 * 2. Return the package name in canHandle()
 * 3. Register it in the init block of NotificationMaskStrategyRegistry
 *
 * Override getMaskedText() or buildMaskedNotification() to
 * customize the notification text or the entire masked notification.
 */
abstract class AbstractMaskStrategy : NotificationMaskStrategy {

    // PreferencesRepository is created lazily per strategy instance on first use.
    // Uses applicationContext to avoid leaking the service Context.
    @Volatile
    private var prefsRepository: PreferencesRepository? = null

    private fun getPrefsRepository(context: Context): PreferencesRepository =
        prefsRepository ?: synchronized(this) {
            prefsRepository ?: PreferencesRepository(context.applicationContext).also {
                prefsRepository = it
            }
        }

    /**
     * Returns the masked notification body text.
     * Can be overridden in app-specific subclasses for customization.
     */
    protected open fun getMaskedText(context: Context): String =
        context.getString(R.string.masked_text)

    override fun mask(
        sbn: StatusBarNotification,
        listenerService: NotificationListenerService,
    ): Boolean {
        val context: Context = listenerService
        val packageName = sbn.packageName

        // Retrieve user preferences (sound, vibration, per-app toggle) from the lazily-created repository
        val prefsRepository = getPrefsRepository(context)

        // Check per-app enabled preference — if disabled, leave the original notification untouched
        if (!prefsRepository.isAppEnabled(packageName)) {
            return false
        }

        // Cancel the original notification first, before the permission check.
        // This ensures the original content is always hidden even when POST_NOTIFICATIONS
        // permission has not been granted (Android 13+).
        listenerService.cancelNotification(sbn.key)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Cancelled original notification from $packageName")
            DebugLogRepository.add("Cancelled: $packageName")
        }

        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted – masked notification not posted")
                    DebugLogRepository.add("ERROR: POST_NOTIFICATIONS not granted")
                }
                return false
            }
        }

        // Get the app name (falls back to "App" on failure)
        val appName = try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            "App"
        }

        // Generate notification ID (unique combination of package name, ID, and tag)
        val notificationId = Objects.hash(packageName, sbn.id, sbn.tag ?: "")

        // Preserve the original contentIntent; fall back to a launch intent if absent
        val contentIntent = sbn.notification.contentIntent
            ?: createLaunchIntent(context, packageName, notificationId)

        // Build and post the masked notification
        val maskedNotification =
            buildMaskedNotification(context, sbn, appName, contentIntent, prefsRepository)
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(NotificationMaskStrategy.MASKED_TAG, notificationId, maskedNotification)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Posted masked notification for $appName ($packageName)")
            DebugLogRepository.add("✅ Masked: $appName ($packageName)")
        }

        return true
    }

    /**
     * Builds the masked notification.
     * Override in a subclass to customize the entire notification.
     */
    protected open fun buildMaskedNotification(
        context: Context,
        sbn: StatusBarNotification,
        appName: String,
        contentIntent: PendingIntent?,
        prefsRepository: PreferencesRepository,
    ): Notification {
        // Apply sound, vibration, and light settings
        var defaults = Notification.DEFAULT_LIGHTS
        if (prefsRepository.notificationSound) defaults = defaults or Notification.DEFAULT_SOUND
        if (prefsRepository.notificationVibrate) defaults = defaults or Notification.DEFAULT_VIBRATE

        return NotificationCompat.Builder(context, KyuubiMaskApp.CHANNEL_ID)
            .setContentTitle(appName)
            .setContentText(getMaskedText(context))
            .setSmallIcon(R.drawable.ic_mask)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .setDefaults(defaults)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                // Preserve the original sort key
                sbn.notification.sortKey?.let { setSortKey(it) }
            }
            .build()
    }

    /**
     * Creates a PendingIntent from the app's launcher intent.
     */
    private fun createLaunchIntent(
        context: Context,
        packageName: String,
        notificationId: Int,
    ): PendingIntent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return null
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP,
        )
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, notificationId, launchIntent, flags)
    }

    companion object {
        private const val TAG = "KyuubiMask"
    }
}
