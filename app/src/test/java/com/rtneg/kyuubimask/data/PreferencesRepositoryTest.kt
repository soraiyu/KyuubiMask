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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
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
    fun `default masked apps should contain expected apps`() {
        val maskedApps = repository.maskedApps
        
        // Original apps
        assertTrue(maskedApps.contains("com.whatsapp"))
        assertTrue(maskedApps.contains("org.telegram.messenger"))
        assertTrue(maskedApps.contains("com.google.android.gm"))
        assertTrue(maskedApps.contains("jp.naver.line.android"))
        
        // Newly added messaging apps
        assertTrue(maskedApps.contains("org.thoughtcrime.securesms")) // Signal
        assertTrue(maskedApps.contains("com.discord")) // Discord
        
        // Newly added email apps
        assertTrue(maskedApps.contains("com.fsck.k9")) // K-9 Mail
        
        // Newly added business apps
        assertTrue(maskedApps.contains("com.slack")) // Slack
        assertTrue(maskedApps.contains("com.microsoft.teams")) // Teams
        assertTrue(maskedApps.contains("us.zoom.videomeetings")) // Zoom
        assertTrue(maskedApps.contains("com.notion.id")) // Notion
        assertTrue(maskedApps.contains("com.atlassian.jira.core.ui")) // Jira
        
        // Verify total count
        assertEquals(13, maskedApps.size)
    }

    @Test
    fun `can add app to masked apps`() {
        val testPackage = "com.test.app"
        assertFalse(repository.isAppMasked(testPackage))

        repository.addMaskedApp(testPackage)
        assertTrue(repository.isAppMasked(testPackage))
    }

    @Test
    fun `can remove app from masked apps`() {
        val testPackage = "com.whatsapp"
        assertTrue(repository.isAppMasked(testPackage))

        repository.removeMaskedApp(testPackage)
        assertFalse(repository.isAppMasked(testPackage))
    }

    @Test
    fun `adding same app twice should not duplicate`() {
        val testPackage = "com.test.app"
        repository.addMaskedApp(testPackage)
        val sizeAfterFirst = repository.maskedApps.size

        repository.addMaskedApp(testPackage)
        val sizeAfterSecond = repository.maskedApps.size

        assertEquals(sizeAfterFirst, sizeAfterSecond)
    }

    @Test
    fun `can set custom masked apps list`() {
        val customApps = setOf("com.custom1", "com.custom2")
        repository.maskedApps = customApps

        assertEquals(customApps, repository.maskedApps)
        assertTrue(repository.isAppMasked("com.custom1"))
        assertTrue(repository.isAppMasked("com.custom2"))
        assertFalse(repository.isAppMasked("com.whatsapp"))
    }

    @Test
    fun `preferences persist across repository instances`() {
        repository.isServiceEnabled = false
        repository.addMaskedApp("com.test.persistent")

        // Create new repository instance
        val newRepository = PreferencesRepository(context)

        assertFalse(newRepository.isServiceEnabled)
        assertTrue(newRepository.isAppMasked("com.test.persistent"))
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
    fun `notification preferences persist across repository instances`() {
        repository.notificationSound = false
        repository.notificationVibrate = false
        
        // Create new repository instance
        val newRepository = PreferencesRepository(context)
        
        assertFalse(newRepository.notificationSound)
        assertFalse(newRepository.notificationVibrate)
    }
}
