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

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * In-memory ring buffer for debug events. Debug builds only.
 *
 * PRIVACY: Only package names and event types are stored.
 * Notification content is never recorded here.
 *
 * Thread-safe: all mutations are synchronized.
 * Capacity: MAX_ENTRIES lines; oldest entry is dropped when full.
 * Lifetime: process lifetime (cleared on app restart).
 */
object DebugLogRepository {

    private const val MAX_ENTRIES = 50

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val buffer = ArrayDeque<String>(MAX_ENTRIES)

    /** Add a log entry prefixed with the current wall-clock time. */
    @Synchronized
    fun add(message: String) {
        if (buffer.size >= MAX_ENTRIES) buffer.removeFirst()
        val time = LocalTime.now().format(formatter)
        buffer.addLast("[$time] $message")
    }

    /** Return all entries in insertion order (oldest first). */
    @Synchronized
    fun entries(): List<String> = buffer.toList()

    /** Remove all entries. */
    @Synchronized
    fun clear() = buffer.clear()
}
