/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
    fun `getVibrationTimings returns a defensive copy`() {
        val timings = VibrationPatterns.getVibrationTimings("short")
        timings[0] = 999L
        // The mutation should not affect subsequent calls
        assertContentEquals(longArrayOf(0, 200), VibrationPatterns.getVibrationTimings("short"))
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

    @Test
    fun `patterns map exposes exactly the expected keys`() {
        val expectedKeys = setOf("short", "double", "heart", "long")
        assertEquals(expectedKeys, VibrationPatterns.patterns.keys)
    }

    @Test
    fun `getVibrationTimings for each key returns non-empty array`() {
        VibrationPatterns.patterns.keys.forEach { key ->
            val timings = VibrationPatterns.getVibrationTimings(key)
            assertTrue(timings.isNotEmpty(), "Timings for '$key' must not be empty")
        }
    }

    @Test
    fun `patterns map preserves insertion order - short is first`() {
        assertEquals("short", VibrationPatterns.patterns.keys.first())
    }

    @Test
    fun `getVibrationTimings for empty key returns default pattern`() {
        val expected = VibrationPatterns.getVibrationTimings(VibrationPatterns.DEFAULT_VIBE_PATTERN)
        assertContentEquals(expected, VibrationPatterns.getVibrationTimings(""))
    }

    @Test
    fun `all non-first pattern timings are positive`() {
        VibrationPatterns.patterns.forEach { (key, timings) ->
            timings.drop(1).forEachIndexed { index, value ->
                assertTrue(value > 0, "Pattern '$key' timing at index ${index + 1} must be positive, got $value")
            }
        }
    }
}
