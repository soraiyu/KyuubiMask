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
package com.rtneg.kyuubimask.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.rtneg.kyuubimask.strategy.DiscordMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class PreferencesRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: PreferencesRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = PreferencesRepository(context)
        // Clear preferences before each test
        context.getSharedPreferences(PreferencesRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @After
    fun tearDown() {
        // Clean up after each test
        context.getSharedPreferences(PreferencesRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun `default service enabled should be true`() {
        assertTrue(repository.isServiceEnabled)
    }

    @Test
    fun `can set and get service enabled state`() {
        repository.isServiceEnabled = false
        assertFalse(repository.isServiceEnabled)

        repository.isServiceEnabled = true
        assertTrue(repository.isServiceEnabled)
    }

    @Test
    fun `preferences persist across repository instances`() {
        repository.isServiceEnabled = false

        // Create new repository instance
        val newRepository = PreferencesRepository(context)

        assertFalse(newRepository.isServiceEnabled)
    }
    
    @Test
    fun `default notification sound should be true`() {
        assertTrue(repository.notificationSound)
    }
    
    @Test
    fun `can set and get notification sound state`() {
        repository.notificationSound = false
        assertFalse(repository.notificationSound)
        
        repository.notificationSound = true
        assertTrue(repository.notificationSound)
    }
    
    @Test
    fun `default notification vibrate should be true`() {
        assertTrue(repository.notificationVibrate)
    }
    
    @Test
    fun `can set and get notification vibrate state`() {
        repository.notificationVibrate = false
        assertFalse(repository.notificationVibrate)
        
        repository.notificationVibrate = true
        assertTrue(repository.notificationVibrate)
    }

    @Test
    fun `default app enabled should be true`() {
        assertTrue(repository.isAppEnabled(SlackMaskStrategy.SLACK_PACKAGE))
        assertTrue(repository.isAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE))
        assertTrue(repository.isAppEnabled(WhatsAppMaskStrategy.WHATSAPP_PACKAGE))
        assertTrue(repository.isAppEnabled(LineMaskStrategy.LINE_PACKAGE))
    }

    @Test
    fun `can set and get app enabled state`() {
        repository.setAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE, false)
        assertFalse(repository.isAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE))

        repository.setAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE, true)
        assertTrue(repository.isAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE))
    }

    @Test
    fun `app enabled state persists across repository instances`() {
        repository.setAppEnabled(WhatsAppMaskStrategy.WHATSAPP_PACKAGE, false)

        val newRepository = PreferencesRepository(context)
        assertFalse(newRepository.isAppEnabled(WhatsAppMaskStrategy.WHATSAPP_PACKAGE))
    }

    @Test
    fun `app enabled state is independent per package`() {
        repository.setAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE, false)

        assertTrue(repository.isAppEnabled(WhatsAppMaskStrategy.WHATSAPP_PACKAGE))
        assertTrue(repository.isAppEnabled(LineMaskStrategy.LINE_PACKAGE))
        assertFalse(repository.isAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE))
    }
    
    @Test
    fun `notification preferences persist across repository instances`() {
        repository.notificationSound = false
        repository.notificationVibrate = false
        
        // Create new repository instance
        val newRepository = PreferencesRepository(context)
        
        assertFalse(newRepository.notificationSound)
        assertFalse(newRepository.notificationVibrate)
    }

    @Test
    fun `default vibration pattern is short`() {
        assertEquals(com.rtneg.kyuubimask.VibrationPatterns.DEFAULT_VIBE_PATTERN, repository.vibrationPattern)
    }

    @Test
    fun `can set and get vibration pattern`() {
        repository.vibrationPattern = "double"
        assertEquals("double", repository.vibrationPattern)

        repository.vibrationPattern = "heart"
        assertEquals("heart", repository.vibrationPattern)
    }

    @Test
    fun `vibration pattern persists across repository instances`() {
        repository.vibrationPattern = "long"

        val newRepository = PreferencesRepository(context)
        assertEquals("long", newRepository.vibrationPattern)
    }
}
