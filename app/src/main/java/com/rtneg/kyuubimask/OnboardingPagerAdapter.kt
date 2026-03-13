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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.rtneg.kyuubimask.databinding.ItemOnboardingPageBinding

/**
 * Data class representing a single onboarding slide.
 */
data class OnboardingPage(
    val titleRes: Int,
    val descRes: Int,
    @DrawableRes val imageRes: Int
)

/**
 * Adapter for the onboarding ViewPager2.
 * Each page shows an icon, title, and description.
 */
class OnboardingPagerAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingPagerAdapter.PageViewHolder>() {

    inner class PageViewHolder(
        private val binding: ItemOnboardingPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage) {
            binding.tvPageTitle.setText(page.titleRes)
            binding.tvPageDesc.setText(page.descRes)
            binding.ivPageImage.setImageResource(page.imageRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemOnboardingPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size
}
