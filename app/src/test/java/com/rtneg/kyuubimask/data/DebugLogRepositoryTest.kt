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
package com.rtneg.kyuubimask.data

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DebugLogRepositoryTest {

    @Before
    fun setUp() = DebugLogRepository.clear()

    @After
    fun tearDown() = DebugLogRepository.clear()

    @Test
    fun `add populates entries with message`() {
        DebugLogRepository.add("test event")
        val entries = DebugLogRepository.entries()
        assertEquals(1, entries.size)
        assertTrue(entries[0].contains("test event"))
    }

    @Test
    fun `entry includes timestamp prefix`() {
        DebugLogRepository.add("with time")
        val entry = DebugLogRepository.entries()[0]
        // Expect format [HH:mm:ss] message
        assertTrue(entry.matches(Regex("""\[\d{2}:\d{2}:\d{2}] with time""")))
    }

    @Test
    fun `clear removes all entries`() {
        DebugLogRepository.add("a")
        DebugLogRepository.add("b")
        DebugLogRepository.clear()
        assertTrue(DebugLogRepository.entries().isEmpty())
    }

    @Test
    fun `max entries limit is enforced`() {
        repeat(60) { DebugLogRepository.add("entry $it") }
        assertEquals(50, DebugLogRepository.entries().size)
    }

    @Test
    fun `oldest entries are dropped when full`() {
        repeat(50) { DebugLogRepository.add("old $it") }
        DebugLogRepository.add("newest")
        val entries = DebugLogRepository.entries()
        assertEquals(50, entries.size)
        assertTrue(entries.last().contains("newest"))
        assertTrue(entries.none { it.contains("old 0") })
    }

    @Test
    fun `entries preserves insertion order`() {
        DebugLogRepository.add("first")
        DebugLogRepository.add("last")
        val entries = DebugLogRepository.entries()
        assertTrue(entries[0].contains("first"))
        assertTrue(entries[1].contains("last"))
    }

    @Test
    fun `add multiple messages accumulates entries`() {
        DebugLogRepository.add("msg1")
        DebugLogRepository.add("msg2")
        DebugLogRepository.add("msg3")
        assertEquals(3, DebugLogRepository.entries().size)
    }

    @Test
    fun `entries returns a snapshot not the live buffer`() {
        DebugLogRepository.add("before")
        val snapshot = DebugLogRepository.entries()
        DebugLogRepository.add("after")
        // The snapshot captured before the second add must still have only one entry
        assertEquals(1, snapshot.size)
    }

    @Test
    fun `add empty string is recorded`() {
        DebugLogRepository.add("")
        assertEquals(1, DebugLogRepository.entries().size)
    }

    @Test
    fun `adding exactly max entries fills buffer without any eviction`() {
        repeat(50) { DebugLogRepository.add("entry $it") }
        val entries = DebugLogRepository.entries()
        assertEquals(50, entries.size)
        // The very first entry must still be present (no eviction yet)
        assertTrue(entries[0].contains("entry 0"))
    }

    @Test
    fun `entry at capacity boundary is evicted when one more is added`() {
        repeat(50) { DebugLogRepository.add("old $it") }
        DebugLogRepository.add("overflow")
        val entries = DebugLogRepository.entries()
        assertEquals(50, entries.size)
        // The overflow entry must be the newest (last)
        assertTrue(entries.last().contains("overflow"))
        // The oldest entry must have been evicted
        assertTrue(entries.none { it.contains("old 0") })
        // Second-oldest entry must still be present
        assertTrue(entries.any { it.contains("old 1") })
    }

    @Test
    fun `entries count never exceeds max entries regardless of how many are added`() {
        repeat(200) { DebugLogRepository.add("msg $it") }
        assertEquals(50, DebugLogRepository.entries().size)
    }
}
