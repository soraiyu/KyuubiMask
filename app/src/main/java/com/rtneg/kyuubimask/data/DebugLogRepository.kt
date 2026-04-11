/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
