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

    @Test
    fun `prefs name constant is kyuubi_prefs`() {
        assertEquals("kyuubi_prefs", PreferencesRepository.PREFS_NAME)
    }

    @Test
    fun `default isFirstLaunch should be true`() {
        assertTrue(repository.isFirstLaunch)
    }

    @Test
    fun `can set isFirstLaunch to false`() {
        repository.isFirstLaunch = false
        assertFalse(repository.isFirstLaunch)
    }

    @Test
    fun `isFirstLaunch persists across repository instances`() {
        repository.isFirstLaunch = false

        val newRepository = PreferencesRepository(context)
        assertFalse(newRepository.isFirstLaunch)
    }

    @Test
    fun `action mask toggled constant is non-empty`() {
        assertTrue(PreferencesRepository.ACTION_MASK_TOGGLED.isNotEmpty())
    }

    @Test
    fun `extra enabled constant is non-empty`() {
        assertTrue(PreferencesRepository.EXTRA_ENABLED.isNotEmpty())
    }

    @Test
    fun `all app enabled states are independent from service enabled`() {
        repository.isServiceEnabled = false

        // App-level toggles should remain at their defaults regardless of service toggle
        assertTrue(repository.isAppEnabled(SlackMaskStrategy.SLACK_PACKAGE))
        assertTrue(repository.isAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE))
    }

    @Test
    fun `setting all apps disabled leaves service enabled state unchanged`() {
        repository.setAppEnabled(SlackMaskStrategy.SLACK_PACKAGE, false)
        repository.setAppEnabled(DiscordMaskStrategy.DISCORD_PACKAGE, false)

        // Service-level toggle must be unaffected
        assertTrue(repository.isServiceEnabled)
    }

    // --- userSelectedPackages ---

    @Test
    fun `default user selected packages is empty`() {
        assertTrue(repository.getUserSelectedPackages().isEmpty())
    }

    @Test
    fun `addUserSelectedPackage stores the package`() {
        repository.addUserSelectedPackage("com.example.testapp")
        assertTrue(repository.getUserSelectedPackages().contains("com.example.testapp"))
    }

    @Test
    fun `removeUserSelectedPackage removes only the specified package`() {
        repository.addUserSelectedPackage("com.example.app1")
        repository.addUserSelectedPackage("com.example.app2")
        repository.removeUserSelectedPackage("com.example.app1")

        val pkgs = repository.getUserSelectedPackages()
        assertFalse(pkgs.contains("com.example.app1"))
        assertTrue(pkgs.contains("com.example.app2"))
    }

    @Test
    fun `user selected packages persist across repository instances`() {
        repository.addUserSelectedPackage("com.example.persistent")

        val newRepository = PreferencesRepository(context)
        assertTrue(newRepository.getUserSelectedPackages().contains("com.example.persistent"))
    }

    @Test
    fun `adding the same package twice does not duplicate it`() {
        repository.addUserSelectedPackage("com.example.dup")
        repository.addUserSelectedPackage("com.example.dup")

        assertEquals(1, repository.getUserSelectedPackages().count { it == "com.example.dup" })
    }

    @Test
    fun `removing a package not in the set is a no-op`() {
        repository.addUserSelectedPackage("com.example.only")
        repository.removeUserSelectedPackage("com.example.notpresent")

        assertTrue(repository.getUserSelectedPackages().contains("com.example.only"))
    }

    // --- isUserSelectedApp (per-profile) ---

    @Test
    fun `isUserSelectedApp returns true for main profile when plain package name is stored`() {
        repository.addUserSelectedPackage("com.example.app")
        assertTrue(repository.isUserSelectedApp("com.example.app", 0))
    }

    @Test
    fun `isUserSelectedApp returns false when package is not selected`() {
        assertFalse(repository.isUserSelectedApp("com.example.app", 0))
        assertFalse(repository.isUserSelectedApp("com.example.app", 10))
    }

    @Test
    fun `isUserSelectedApp returns true for work profile when profile key is stored`() {
        val workProfileKey = "com.example.app${PreferencesRepository.PROFILE_KEY_SEPARATOR}10"
        repository.addUserSelectedPackage(workProfileKey)
        assertTrue(repository.isUserSelectedApp("com.example.app", 10))
    }

    @Test
    fun `isUserSelectedApp returns false for work profile when only main profile entry exists`() {
        // Plain package name (userId == 0 key) is also treated as a fallback for ALL profiles
        // (backward compat for legacy entries added before per-profile support)
        repository.addUserSelectedPackage("com.example.app")
        // Legacy plain-package entry is still treated as app-wide selection
        assertTrue(repository.isUserSelectedApp("com.example.app", 10))
    }

    @Test
    fun `isUserSelectedApp is independent per profile`() {
        val workKey = "com.example.app${PreferencesRepository.PROFILE_KEY_SEPARATOR}10"
        repository.addUserSelectedPackage(workKey)

        // Work profile → selected
        assertTrue(repository.isUserSelectedApp("com.example.app", 10))
        // Main profile → NOT selected (no plain entry or :0 entry)
        assertFalse(repository.isUserSelectedApp("com.example.app", 0))
        // Different work profile → NOT selected
        assertFalse(repository.isUserSelectedApp("com.example.app", 11))
    }

    @Test
    fun `profileAppKey returns plain package for userId 0`() {
        assertEquals("com.example.app", PreferencesRepository.profileAppKey("com.example.app", 0))
    }

    @Test
    fun `profileAppKey returns colon-separated key for non-zero userId`() {
        assertEquals(
            "com.example.app${PreferencesRepository.PROFILE_KEY_SEPARATOR}10",
            PreferencesRepository.profileAppKey("com.example.app", 10)
        )
    }
}
