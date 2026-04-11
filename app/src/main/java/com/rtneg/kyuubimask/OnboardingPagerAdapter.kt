/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
