/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask

import com.rtneg.kyuubimask.strategy.DiscordMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy

/**
 * Registry of notification masking strategies (singleton).
 *
 * How to add support for a new app:
 * 1. Create a class extending AbstractMaskStrategy (e.g., DiscordMaskStrategy)
 * 2. Add register(DiscordMaskStrategy()) to this init block
 *
 * Strategies are searched in registration order; the first match is used.
 * Notifications from unregistered apps are passed through without masking.
 */
object NotificationMaskStrategyRegistry {

    private val strategies = java.util.concurrent.CopyOnWriteArrayList<NotificationMaskStrategy>()

    init {
        registerDefaults()
    }

    private fun registerDefaults() {
        // Register strategies for supported apps (just append register() calls here to add more)
        register(SlackMaskStrategy())
        register(DiscordMaskStrategy())
        register(WhatsAppMaskStrategy())
        register(LineMaskStrategy())
    }

    /**
     * Registers a new strategy.
     */
    fun register(strategy: NotificationMaskStrategy) {
        strategies.add(strategy)
    }

    /**
     * Resets the registry to its default state (built-in strategies only).
     * Intended for use in unit tests only.
     */
    @androidx.annotation.VisibleForTesting
    internal fun resetForTest() {
        strategies.clear()
        registerDefaults()
    }

    /**
     * Finds the strategy for the given package name.
     * Searches in registration order and returns the first match.
     * Returns null if no strategy is registered for the package (notification passes through unmasked).
     */
    fun findStrategy(packageName: String): NotificationMaskStrategy? =
        strategies.firstOrNull { it.canHandle(packageName) }
}
