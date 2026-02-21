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

import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationMaskStrategyTest {

    // --- canHandle tests ---

    @Test
    fun `SlackMaskStrategy canHandle returns true for Slack package`() {
        assertTrue(SlackMaskStrategy().canHandle("com.slack"))
    }

    @Test
    fun `SlackMaskStrategy canHandle returns false for other packages`() {
        assertFalse(SlackMaskStrategy().canHandle("com.whatsapp"))
        assertFalse(SlackMaskStrategy().canHandle("jp.naver.line.android"))
        assertFalse(SlackMaskStrategy().canHandle("com.discord"))
    }

    // --- Registry tests ---

    @Test
    fun `registry returns SlackMaskStrategy for Slack package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("com.slack")
        assertNotNull(strategy)
        assertTrue(strategy is SlackMaskStrategy)
    }

    @Test
    fun `registry returns null for non-Slack packages`() {
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.whatsapp"))
        assertNull(NotificationMaskStrategyRegistry.findStrategy("jp.naver.line.android"))
        assertNull(NotificationMaskStrategyRegistry.findStrategy("com.unknown.app"))
    }

    @Test
    fun `MASKED_TAG constant is non-empty`() {
        assertTrue(NotificationMaskStrategy.MASKED_TAG.isNotEmpty())
    }
}
