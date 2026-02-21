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
}
