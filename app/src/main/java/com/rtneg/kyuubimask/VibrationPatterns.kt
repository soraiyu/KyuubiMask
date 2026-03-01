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
     */
    fun getVibrationTimings(key: String): LongArray =
        patterns[key] ?: patterns[DEFAULT_VIBE_PATTERN]!!
}
