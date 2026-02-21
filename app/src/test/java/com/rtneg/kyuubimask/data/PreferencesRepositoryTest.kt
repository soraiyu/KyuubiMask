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
    fun `notification preferences persist across repository instances`() {
        repository.notificationSound = false
        repository.notificationVibrate = false
        
        // Create new repository instance
        val newRepository = PreferencesRepository(context)
        
        assertFalse(newRepository.notificationSound)
        assertFalse(newRepository.notificationVibrate)
    }
}
