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

import com.rtneg.kyuubimask.strategy.DefaultMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NotificationMaskStrategyTest {

    // --- canHandle tests ---

    @Test
    fun `WhatsAppMaskStrategy canHandle returns true for WhatsApp package`() {
        assertTrue(WhatsAppMaskStrategy().canHandle("com.whatsapp"))
    }

    @Test
    fun `WhatsAppMaskStrategy canHandle returns false for other packages`() {
        assertFalse(WhatsAppMaskStrategy().canHandle("jp.naver.line.android"))
        assertFalse(WhatsAppMaskStrategy().canHandle("com.discord"))
    }

    @Test
    fun `LineMaskStrategy canHandle returns true for LINE package`() {
        assertTrue(LineMaskStrategy().canHandle("jp.naver.line.android"))
    }

    @Test
    fun `LineMaskStrategy canHandle returns false for other packages`() {
        assertFalse(LineMaskStrategy().canHandle("com.whatsapp"))
        assertFalse(LineMaskStrategy().canHandle("com.discord"))
    }

    @Test
    fun `DefaultMaskStrategy canHandle returns true for any package`() {
        assertTrue(DefaultMaskStrategy().canHandle("com.whatsapp"))
        assertTrue(DefaultMaskStrategy().canHandle("com.unknown.app"))
        assertTrue(DefaultMaskStrategy().canHandle(""))
    }

    // --- Registry tests ---

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
    fun `registry returns DefaultMaskStrategy for unknown package`() {
        val strategy = NotificationMaskStrategyRegistry.findStrategy("com.unknown.app")
        assertNotNull(strategy)
        assertTrue(strategy is DefaultMaskStrategy)
    }

    @Test
    fun `MASKED_TAG constant is non-empty`() {
        assertTrue(NotificationMaskStrategy.MASKED_TAG.isNotEmpty())
    }
}
