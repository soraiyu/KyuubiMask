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
import com.rtneg.kyuubimask.strategy.GmailMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SignalMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.TelegramMaskStrategy
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
        assertTrue(DiscordMaskStrategy().canHandle("com.discord"))
    }

    @Test
    fun `DiscordMaskStrategy canHandle returns false for other packages`() {
        assertFalse(DiscordMaskStrategy().canHandle("com.Slack"))
        assertFalse(DiscordMaskStrategy().canHandle("com.whatsapp"))
        assertFalse(DiscordMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `WhatsAppMaskStrategy canHandle returns true for WhatsApp package`() {
        assertTrue(WhatsAppMaskStrategy().canHandle("com.whatsapp"))
    }

    @Test
    fun `WhatsAppMaskStrategy canHandle returns false for other packages`() {
        assertFalse(WhatsAppMaskStrategy().canHandle("com.Slack"))
        assertFalse(WhatsAppMaskStrategy().canHandle("com.discord"))
        assertFalse(WhatsAppMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `LineMaskStrategy canHandle returns true for LINE package`() {
        assertTrue(LineMaskStrategy().canHandle("jp.naver.line.android"))
    }

    @Test
    fun `LineMaskStrategy canHandle returns false for other packages`() {
        assertFalse(LineMaskStrategy().canHandle("com.Slack"))
        assertFalse(LineMaskStrategy().canHandle("com.discord"))
        assertFalse(LineMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `TelegramMaskStrategy canHandle returns true for Telegram package`() {
        assertTrue(TelegramMaskStrategy().canHandle("org.telegram.messenger"))
    }

    @Test
    fun `TelegramMaskStrategy canHandle returns false for other packages`() {
        assertFalse(TelegramMaskStrategy().canHandle("com.Slack"))
        assertFalse(TelegramMaskStrategy().canHandle("com.discord"))
        assertFalse(TelegramMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `SignalMaskStrategy canHandle returns true for Signal package`() {
        assertTrue(SignalMaskStrategy().canHandle("org.thoughtcrime.securesms"))
    }

    @Test
    fun `SignalMaskStrategy canHandle returns false for other packages`() {
        assertFalse(SignalMaskStrategy().canHandle("com.Slack"))
        assertFalse(SignalMaskStrategy().canHandle("com.discord"))
        assertFalse(SignalMaskStrategy().canHandle("com.unknown.app"))
    }

    @Test
    fun `GmailMaskStrategy canHandle returns true for Gmail package`() {
        assertTrue(GmailMaskStrategy().canHandle("com.google.android.gm"))
    }

    @Test
    fun `GmailMaskStrategy canHandle returns false for other packages`() {
        assertFalse(GmailMaskStrategy().canHandle("com.Slack"))
        assertFalse(GmailMaskStrategy().canHandle("com.discord"))
        assertFalse(GmailMaskStrategy().canHandle("com.unknown.app"))
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
        val strategy = NotificationMaskStrategyRegistry.findStrategy("com.discord")
        assertNotNull(strategy)
        assertTrue(strategy is DiscordMaskStrategy)
    }

    @Test
    fun `registry returns WhatsAppMaskStrategy for WhatsApp package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("com.whatsapp")
        assertNotNull(strategy)
        assertTrue(strategy is WhatsAppMaskStrategy)
    }

    @Test
    fun `registry returns LineMaskStrategy for LINE package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("jp.naver.line.android")
        assertNotNull(strategy)
        assertTrue(strategy is LineMaskStrategy)
    }

    @Test
    fun `registry returns TelegramMaskStrategy for Telegram package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("org.telegram.messenger")
        assertNotNull(strategy)
        assertTrue(strategy is TelegramMaskStrategy)
    }

    @Test
    fun `registry returns SignalMaskStrategy for Signal package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("org.thoughtcrime.securesms")
        assertNotNull(strategy)
        assertTrue(strategy is SignalMaskStrategy)
    }

    @Test
    fun `registry returns GmailMaskStrategy for Gmail package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("com.google.android.gm")
        assertNotNull(strategy)
        assertTrue(strategy is GmailMaskStrategy)
    }

    @Test
    fun `registry returns null for unknown packages`() {
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.slack")) // lowercase must not match
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.unknown.app"))
    }

    @Test
    fun `MASKED_TAG constant is non-empty`() {
        assertTrue(NotificationMaskStrategy.MASKED_TAG.isNotEmpty())
    }
}
