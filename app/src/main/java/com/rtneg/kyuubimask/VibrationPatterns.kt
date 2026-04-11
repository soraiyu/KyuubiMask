/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask

/**
 * Defines the available notification vibration patterns.
 * Each pattern is expressed as a timing array (ms) passed to VibrationEffect.createWaveform.
 * Index 0 = initial wait before vibration; subsequent values alternate between vibrate and pause.
 */
object VibrationPatterns {

    /** SharedPreferences key for the selected vibration pattern */
    const val PREF_KEY_VIBE_PATTERN = "vibe_pattern"

    /** Default vibration pattern key */
    const val DEFAULT_VIBE_PATTERN = "short"

    /** Available patterns keyed by name; insertion order determines display order in the UI. */
    val patterns: Map<String, LongArray> = linkedMapOf(
        "short"  to longArrayOf(0, 200),
        "double" to longArrayOf(0, 150, 100, 150),
        "heart"  to longArrayOf(0, 100, 100, 100, 100, 300),
        "long"   to longArrayOf(0, 500, 200, 500),
    )

    /**
     * Returns the vibration timing array for the given pattern key.
     * Falls back to the default pattern ("short") for unknown keys.
     * A defensive copy is returned to prevent callers from mutating the internal pattern.
     */
    fun getVibrationTimings(key: String): LongArray =
        (patterns[key] ?: patterns[DEFAULT_VIBE_PATTERN]!!).copyOf()
}
