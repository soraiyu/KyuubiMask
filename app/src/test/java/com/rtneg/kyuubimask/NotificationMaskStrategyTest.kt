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
}
