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

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.rtneg.kyuubimask.strategy.DiscordMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationMaskStrategyRegistryTest {

    // A minimal fake strategy used to test register()
    private class FakeStrategy(private val pkg: String) : NotificationMaskStrategy {
        override fun canHandle(packageName: String) = packageName == pkg
        override fun mask(sbn: StatusBarNotification, listenerService: NotificationListenerService) = false
    }

    @Before
    fun setUp() {
        NotificationMaskStrategyRegistry.resetForTest()
    }

    @After
    fun tearDown() {
        NotificationMaskStrategyRegistry.resetForTest()
    }

    @Test
    fun `register adds a custom strategy that can be found`() {
        val customPkg = "com.example.customapp.registrytest"
        NotificationMaskStrategyRegistry.register(FakeStrategy(customPkg))
        val strategy = NotificationMaskStrategyRegistry.findStrategy(customPkg)
        assertNotNull(strategy)
    }

    @Test
    fun `findStrategy returns first registered strategy for overlapping canHandle`() {
        // Register two strategies that both handle the same package
        val pkg = "com.example.overlap.registrytest"
        val first = FakeStrategy(pkg)
        val second = FakeStrategy(pkg)
        NotificationMaskStrategyRegistry.register(first)
        NotificationMaskStrategyRegistry.register(second)
        // Should return the first registered match
        val found = NotificationMaskStrategyRegistry.findStrategy(pkg)
        assertTrue(found === first)
    }

    @Test
    fun `findStrategy returns null for completely unknown package`() {
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.totally.unknown.package.xyz123"))
    }

    @Test
    fun `built-in Slack strategy is registered`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(SlackMaskStrategy.SLACK_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is SlackMaskStrategy)
    }

    @Test
    fun `built-in Discord strategy is registered`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(DiscordMaskStrategy.DISCORD_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is DiscordMaskStrategy)
    }

    @Test
    fun `built-in WhatsApp strategy is registered`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(WhatsAppMaskStrategy.WHATSAPP_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is WhatsAppMaskStrategy)
    }

    @Test
    fun `built-in LINE strategy is registered`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(LineMaskStrategy.LINE_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is LineMaskStrategy)
    }
}
