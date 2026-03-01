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

import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VibrationPatternsTest {

    @Test
    fun `default vibe pattern key is short`() {
        assertEquals("short", VibrationPatterns.DEFAULT_VIBE_PATTERN)
    }

    @Test
    fun `pref key constant is vibe_pattern`() {
        assertEquals("vibe_pattern", VibrationPatterns.PREF_KEY_VIBE_PATTERN)
    }

    @Test
    fun `patterns map contains all four keys`() {
        val keys = VibrationPatterns.patterns.keys
        assertTrue("short" in keys)
        assertTrue("double" in keys)
        assertTrue("heart" in keys)
        assertTrue("long" in keys)
    }

    @Test
    fun `getVibrationTimings returns correct timings for short`() {
        assertContentEquals(longArrayOf(0, 200), VibrationPatterns.getVibrationTimings("short"))
    }

    @Test
    fun `getVibrationTimings returns correct timings for double`() {
        assertContentEquals(longArrayOf(0, 150, 100, 150), VibrationPatterns.getVibrationTimings("double"))
    }

    @Test
    fun `getVibrationTimings returns correct timings for heart`() {
        assertContentEquals(longArrayOf(0, 100, 100, 100, 100, 300), VibrationPatterns.getVibrationTimings("heart"))
    }

    @Test
    fun `getVibrationTimings returns correct timings for long`() {
        assertContentEquals(longArrayOf(0, 500, 200, 500), VibrationPatterns.getVibrationTimings("long"))
    }

    @Test
    fun `getVibrationTimings returns default for unknown key`() {
        val expected = VibrationPatterns.getVibrationTimings(VibrationPatterns.DEFAULT_VIBE_PATTERN)
        assertContentEquals(expected, VibrationPatterns.getVibrationTimings("unknown_pattern"))
    }

    @Test
    fun `all patterns have at least two elements (wait + vibrate)`() {
        VibrationPatterns.patterns.forEach { (key, timings) ->
            assertTrue(timings.size >= 2, "Pattern '$key' must have at least 2 timing values")
        }
    }

    @Test
    fun `all pattern timings start with zero wait`() {
        VibrationPatterns.patterns.forEach { (key, timings) ->
            assertEquals(0L, timings[0], "Pattern '$key' must start with 0ms wait")
        }
    }
}
