/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
    fun `built-in strategies are registered correctly`() {
        val strategiesToTest = mapOf(
            SlackMaskStrategy.SLACK_PACKAGE to SlackMaskStrategy::class,
            DiscordMaskStrategy.DISCORD_PACKAGE to DiscordMaskStrategy::class,
            WhatsAppMaskStrategy.WHATSAPP_PACKAGE to WhatsAppMaskStrategy::class,
            LineMaskStrategy.LINE_PACKAGE to LineMaskStrategy::class
        )

        strategiesToTest.forEach { (pkg, clazz) ->
            val strategy = NotificationMaskStrategyRegistry.findStrategy(pkg)
            assertNotNull(strategy, "Strategy for package '$pkg' should be registered.")
            assertTrue(clazz.isInstance(strategy), "Strategy for package '$pkg' should be of type '${clazz.simpleName}'.")
        }
    }
}
