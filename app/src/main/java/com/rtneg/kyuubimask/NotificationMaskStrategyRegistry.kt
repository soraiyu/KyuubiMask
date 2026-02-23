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
     * Finds the strategy for the given package name.
     * Searches in registration order and returns the first match.
     * Returns null if no strategy is registered for the package (notification passes through unmasked).
     */
    fun findStrategy(packageName: String): NotificationMaskStrategy? =
        strategies.firstOrNull { it.canHandle(packageName) }
}
