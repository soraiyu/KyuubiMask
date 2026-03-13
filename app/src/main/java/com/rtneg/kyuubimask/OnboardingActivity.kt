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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.rtneg.kyuubimask.data.PreferencesRepository
import com.rtneg.kyuubimask.databinding.ActivityOnboardingBinding

/**
 * OnboardingActivity - Shown on first launch instead of the privacy dialog.
 * Displays a 5-page horizontal swipe onboarding with:
 *   - Skip button (top right) to dismiss at any time
 *   - Page indicator dots (TabLayout)
 *   - Next button that becomes "Get Started" on the last page
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var prefsRepository: PreferencesRepository

    private val pages: List<OnboardingPage> by lazy {
        listOf(
            OnboardingPage(
                titleRes = R.string.onboarding_page1_title,
                descRes  = R.string.onboarding_page1_desc,
                imageRes = R.drawable.ic_mask_on
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_page2_title,
                descRes  = R.string.onboarding_page2_desc,
                imageRes = R.drawable.ic_mask
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_page3_title,
                descRes  = R.string.onboarding_page3_desc,
                imageRes = R.drawable.ic_privacy
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_page4_title,
                descRes  = R.string.onboarding_page4_desc,
                imageRes = R.drawable.ic_mask_off
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_page5_title,
                descRes  = R.string.onboarding_page5_desc,
                imageRes = R.drawable.ic_mask_on
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsRepository = (applicationContext as KyuubiMaskApp).prefsRepository

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        val adapter = OnboardingPagerAdapter(pages)
        binding.viewPager.adapter = adapter

        // Connect TabLayout dots to ViewPager2
        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()

        // Update button label when page changes
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonLabel(position)
            }
        })
    }

    private fun setupButtons() {
        // Skip: finish onboarding immediately
        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }

        // Next / Get Started
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < pages.lastIndex) {
                binding.viewPager.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }

        // Set initial label
        updateButtonLabel(0)
    }

    private fun updateButtonLabel(position: Int) {
        val isLast = position == pages.lastIndex
        binding.btnNext.setText(
            if (isLast) R.string.onboarding_start else R.string.onboarding_next
        )
        binding.btnSkip.visibility =
            if (isLast) android.view.View.INVISIBLE else android.view.View.VISIBLE
    }

    /**
     * Marks onboarding as complete and returns to SettingsActivity.
     */
    private fun finishOnboarding() {
        prefsRepository.isFirstLaunch = false
        finish()
    }
}
