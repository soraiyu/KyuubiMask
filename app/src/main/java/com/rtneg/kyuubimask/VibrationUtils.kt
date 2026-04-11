/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Fires vibration using the given timing array.
 * Uses VibratorManager on API 31+ and Vibrator on API 26–30.
 * Returns true if the vibration was triggered, false if no vibrator is available.
 */
fun Context.vibrateWithEffect(timings: LongArray): Boolean {
    val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Vibrator::class.java)
    }
    if (vibrator == null || !vibrator.hasVibrator()) return false
    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
    return true
}
