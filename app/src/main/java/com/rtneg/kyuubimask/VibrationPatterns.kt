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
 * 通知バイブレーションパターンの定義。
 * 各パターンは VibrationEffect.createWaveform に渡すタイミング配列 (ms) で表現される。
 * インデックス 0 = 振動前の待機時間、以降は振動・停止を交互に繰り返す。
 */
object VibrationPatterns {

    /** SharedPreferences キー */
    const val PREF_KEY_VIBE_PATTERN = "vibe_pattern"

    /** デフォルトパターン */
    const val DEFAULT_VIBE_PATTERN = "short"

    /** パターンキー一覧（UI での表示順を保持するため LinkedHashMap） */
    val patterns: Map<String, LongArray> = linkedMapOf(
        "short"  to longArrayOf(0, 200),
        "double" to longArrayOf(0, 150, 100, 150),
        "heart"  to longArrayOf(0, 100, 100, 100, 100, 300),
        "long"   to longArrayOf(0, 500, 200, 500),
    )

    /**
     * キーに対応するバイブレーションタイミングを返す。
     * 未知のキーの場合はデフォルト (short) を返す。
     */
    fun getVibrationTimings(key: String): LongArray =
        patterns[key] ?: patterns[DEFAULT_VIBE_PATTERN]!!
}
