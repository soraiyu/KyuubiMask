/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask

import com.rtneg.kyuubimask.strategy.AbstractMaskStrategy
import com.rtneg.kyuubimask.strategy.DiscordMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationMaskStrategyTest {

    // --- canHandle tests ---

    @Test
    fun `SlackMaskStrategy canHandle returns true for Slack package`() {
        assertTrue(SlackMaskStrategy().canHandle("com.Slack"))
    }

    @Test
    fun `SlackMaskStrategy canHandle returns false for other packages`() {
        assertFalse(SlackMaskStrategy().canHandle("com.slack")) // lowercase must not match
        assertFalse(SlackMaskStrategy().canHandle("com.whatsapp"))
        assertFalse(SlackMaskStrategy().canHandle("jp.naver.line.android"))
        assertFalse(SlackMaskStrategy().canHandle("com.discord"))
    }

    @Test
    fun `DiscordMaskStrategy canHandle returns true for Discord package`() {
        assertTrue(DiscordMaskStrategy().canHandle(DiscordMaskStrategy.DISCORD_PACKAGE))
    }

    @Test
    fun `DiscordMaskStrategy canHandle is case-insensitive`() {
        assertTrue(DiscordMaskStrategy().canHandle("COM.DISCORD"))
        assertTrue(DiscordMaskStrategy().canHandle("Com.Discord"))
        assertTrue(DiscordMaskStrategy().canHandle("COM.discord"))
    }

    @Test
    fun `DiscordMaskStrategy canHandle returns false for other packages`() {
        assertFalse(DiscordMaskStrategy().canHandle(SlackMaskStrategy.SLACK_PACKAGE))
        assertFalse(DiscordMaskStrategy().canHandle(WhatsAppMaskStrategy.WHATSAPP_PACKAGE))
        assertFalse(DiscordMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `WhatsAppMaskStrategy canHandle returns true for WhatsApp package`() {
        assertTrue(WhatsAppMaskStrategy().canHandle(WhatsAppMaskStrategy.WHATSAPP_PACKAGE))
    }

    @Test
    fun `WhatsAppMaskStrategy canHandle returns false for other packages`() {
        assertFalse(WhatsAppMaskStrategy().canHandle(SlackMaskStrategy.SLACK_PACKAGE))
        assertFalse(WhatsAppMaskStrategy().canHandle(DiscordMaskStrategy.DISCORD_PACKAGE))
        assertFalse(WhatsAppMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `LineMaskStrategy canHandle returns true for LINE package`() {
        assertTrue(LineMaskStrategy().canHandle(LineMaskStrategy.LINE_PACKAGE))
    }

    @Test
    fun `LineMaskStrategy canHandle returns false for other packages`() {
        assertFalse(LineMaskStrategy().canHandle(SlackMaskStrategy.SLACK_PACKAGE))
        assertFalse(LineMaskStrategy().canHandle(DiscordMaskStrategy.DISCORD_PACKAGE))
        assertFalse(LineMaskStrategy().canHandle("com.unknown.app"))
    }

    // --- Registry tests ---

    @Test
    fun `registry returns SlackMaskStrategy for Slack package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("com.Slack")
        assertNotNull(strategy)
        assertTrue(strategy is SlackMaskStrategy)
    }

    @Test
    fun `registry returns DiscordMaskStrategy for Discord package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(DiscordMaskStrategy.DISCORD_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is DiscordMaskStrategy)
    }

    @Test
    fun `registry returns WhatsAppMaskStrategy for WhatsApp package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(WhatsAppMaskStrategy.WHATSAPP_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is WhatsAppMaskStrategy)
    }

    @Test
    fun `registry returns LineMaskStrategy for LINE package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy(LineMaskStrategy.LINE_PACKAGE)
        assertNotNull(strategy)
        assertTrue(strategy is LineMaskStrategy)
    }

    @Test
    fun `registry returns null for unknown packages`() {
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.slack")) // lowercase must not match
        assertNull(NotificationMaskStrategyRegistry.findStrategy("org.telegram.messenger"))
        assertNull(NotificationMaskStrategyRegistry.findStrategy("org.thoughtcrime.securesms"))
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.google.android.gm"))
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.unknown.app"))
    }

    @Test
    fun `MASKED_TAG constant is non-empty`() {
        assertTrue(NotificationMaskStrategy.MASKED_TAG.isNotEmpty())
    }

    // --- Package name constant value tests ---

    @Test
    fun `package name constants have correct values`() {
        kotlin.test.assertEquals("com.Slack", SlackMaskStrategy.SLACK_PACKAGE)
        kotlin.test.assertEquals("com.discord", DiscordMaskStrategy.DISCORD_PACKAGE)
        kotlin.test.assertEquals("com.whatsapp", WhatsAppMaskStrategy.WHATSAPP_PACKAGE)
        kotlin.test.assertEquals("jp.naver.line.android", LineMaskStrategy.LINE_PACKAGE)
    }

    // --- Edge case tests ---

    @Test
    fun `all strategies return false for empty package name`() {
        val strategies = listOf(
            SlackMaskStrategy(),
            DiscordMaskStrategy(),
            WhatsAppMaskStrategy(),
            LineMaskStrategy()
        )
        strategies.forEach { strategy ->
            assertFalse(strategy.canHandle(""), "${strategy::class.simpleName} should return false for empty string")
        }
    }

    @Test
    fun `registry returns null for empty string`() {
        assertNull(NotificationMaskStrategyRegistry.findStrategy(""))
    }

    // --- appNameFromPackage fallback tests ---

    @Test
    fun `appNameFromPackage returns Slack for com_Slack`() {
        kotlin.test.assertEquals("Slack", AbstractMaskStrategy.appNameFromPackage("com.Slack"))
    }

    @Test
    fun `appNameFromPackage returns Discord for com_discord`() {
        kotlin.test.assertEquals("Discord", AbstractMaskStrategy.appNameFromPackage("com.discord"))
    }

    @Test
    fun `appNameFromPackage returns Line for jp_naver_line_android`() {
        // "android" is in the skip list, so it takes "line" and title-cases it
        kotlin.test.assertEquals("Line", AbstractMaskStrategy.appNameFromPackage("jp.naver.line.android"))
    }

    @Test
    fun `appNameFromPackage skips generic suffix app`() {
        // "app" is in the skip list, so it should use "example" instead
        kotlin.test.assertEquals("Example", AbstractMaskStrategy.appNameFromPackage("com.example.app"))
    }

    @Test
    fun `appNameFromPackage falls back to full package name when all segments are generic`() {
        // All segments are generic or empty — return the raw package name
        kotlin.test.assertEquals("android.app.mobile", AbstractMaskStrategy.appNameFromPackage("android.app.mobile"))
    }

    @Test
    fun `appNameFromPackage handles single-segment package name`() {
        kotlin.test.assertEquals("Slack", AbstractMaskStrategy.appNameFromPackage("slack"))
    }

    @Test
    fun `appNameFromPackage skips multiple trailing skip words`() {
        // "mobile" and "app" are both skip words; "com" is the last non-skip segment
        kotlin.test.assertEquals("Com", AbstractMaskStrategy.appNameFromPackage("com.mobile.app"))
    }

    @Test
    fun `appNameFromPackage handles numbers in segment`() {
        // "app" is skipped; "test123" is not a skip word and gets title-cased
        kotlin.test.assertEquals("Test123", AbstractMaskStrategy.appNameFromPackage("com.test123.app"))
    }

    @Test
    fun `appNameFromPackage falls back to full package name for single skip-word segment`() {
        // "mobile" is the only segment and is in the skip list; falls back to the raw package name
        kotlin.test.assertEquals("mobile", AbstractMaskStrategy.appNameFromPackage("mobile"))
    }

    @Test
    fun `appNameFromPackage falls back to full package name for empty string`() {
        // Empty string produces one empty segment which is filtered out; falls back to the input
        kotlin.test.assertEquals("", AbstractMaskStrategy.appNameFromPackage(""))
    }

    @Test
    fun `appNameFromPackage preserves existing capitalisation in the chosen segment`() {
        // "MyApp" already starts with uppercase; title-casing is idempotent
        kotlin.test.assertEquals("MyApp", AbstractMaskStrategy.appNameFromPackage("com.android.MyApp"))
    }
}
