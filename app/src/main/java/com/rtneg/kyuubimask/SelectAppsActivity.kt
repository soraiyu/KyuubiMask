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
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rtneg.kyuubimask.data.PreferencesRepository
import com.rtneg.kyuubimask.strategy.DiscordMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.SlackMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SelectAppsActivity – lets the user choose any installed app for notification masking.
 *
 * Built-in apps (Slack, Discord, WhatsApp, LINE) are excluded from this list because they
 * already have dedicated toggles in SettingsActivity. All other launcher-visible apps are
 * shown here so the user can add them to the mask list. This makes KyuubiMask a general-purpose
 * notification masker, not limited to specific proprietary services.
 *
 * Work/managed-profile apps are enumerated separately using LauncherApps so that the user can
 * independently control masking for personal-profile and work-profile instances of the same app.
 * Work-profile apps are stored with a profile key ("packageName:userId") and displayed with a
 * "Work" badge to distinguish them from their personal-profile counterparts.
 */
class SelectAppsActivity : AppCompatActivity() {

    private lateinit var prefsRepository: PreferencesRepository

    // Package names already handled by dedicated built-in toggles
    private val builtInPackages = setOf(
        SlackMaskStrategy.SLACK_PACKAGE,
        DiscordMaskStrategy.DISCORD_PACKAGE,
        WhatsAppMaskStrategy.WHATSAPP_PACKAGE,
        LineMaskStrategy.LINE_PACKAGE,
    )

    data class AppItem(
        val label: String,
        val packageName: String,
        /** Android user/profile ID that owns this app instance (0 = personal profile). */
        val userId: Int,
        /**
         * Key used to store this item in SharedPreferences.
         * For the main profile (userId == 0) this is just the package name.
         * For work/managed profiles (userId != 0) this is "packageName:userId".
         */
        val storageKey: String,
        val isSelected: Boolean,
        /** True when this app belongs to a managed/work profile (i.e. not the calling user's profile). */
        val isWorkProfile: Boolean,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_apps)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.label_select_apps_title)
        }

        prefsRepository = (applicationContext as KyuubiMaskApp).prefsRepository

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewApps)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val chipGroupFilters = findViewById<ChipGroup>(R.id.chipGroupFilters)
        val chipWorkOnly = findViewById<Chip>(R.id.chipWorkOnly)

        lifecycleScope.launch {
            val items = loadAppItems()
            val adapter = AppListAdapter(items, getString(R.string.label_work_profile)) { item, checked ->
                if (checked) {
                    prefsRepository.addUserSelectedPackage(item.storageKey)
                } else {
                    prefsRepository.removeUserSelectedPackage(item.storageKey)
                }
            }
            recyclerView.adapter = adapter

            // Show the filter chip group only when work-profile apps are present
            if (items.any { it.isWorkProfile }) {
                chipGroupFilters.visibility = View.VISIBLE
            }

            chipWorkOnly.setOnCheckedChangeListener { _, isChecked ->
                adapter.filterWorkOnly = isChecked
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private suspend fun loadAppItems(): List<AppItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<AppItem>()

        val launcherApps = getSystemService(LauncherApps::class.java)
        val userManager = getSystemService(UserManager::class.java)
        val profiles = userManager?.userProfiles
        val myHandle = Process.myUserHandle()

        if (launcherApps != null && !profiles.isNullOrEmpty()) {
            // Enumerate apps across all profiles (personal + work/managed)
            for (profile in profiles) {
                // UserHandle.hashCode() returns the internal user ID (mHandle field). This is
                // confirmed by the platform source (@Override public int hashCode(){return mHandle;})
                // and has been stable since UserHandle was introduced in API 17.
                // UserHandle.getIdentifier() is not included in the public SDK stubs at compileSdk 35,
                // so hashCode() is the only non-reflective way to retrieve the numeric user ID
                // at this project's minSdk (26).
                val userId = profile.hashCode()
                // A profile is a work/managed profile when it is not the calling user's own profile.
                val isWorkProfile = (profile != myHandle)

                try {
                    // Use the LauncherActivityInfo objects from the initial call directly so
                    // we can read both packageName and label without a second IPC call per app.
                    launcherApps.getActivityList(null, profile)
                        .groupBy { it.applicationInfo.packageName }
                        .filter { (pkg, _) -> pkg != packageName && pkg !in builtInPackages }
                        .forEach { (pkg, activities) ->
                            val label = activities.firstOrNull()?.label?.toString() ?: pkg
                            val storageKey = PreferencesRepository.profileAppKey(pkg, userId)
                            items.add(
                                AppItem(
                                    label = label,
                                    packageName = pkg,
                                    userId = userId,
                                    storageKey = storageKey,
                                    isSelected = prefsRepository.isUserSelectedApp(pkg, userId),
                                    isWorkProfile = isWorkProfile,
                                )
                            )
                        }
                } catch (e: SecurityException) {
                    // Profile not accessible; fall through and enumerate via PackageManager below
                }
            }
        }

        // Fallback: if LauncherApps enumeration yielded nothing (e.g. single-user device or
        // permission not available), enumerate via PackageManager as before.
        if (items.isEmpty()) {
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            packageManager
                .queryIntentActivities(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY)
                .map { it.activityInfo.packageName }
                .toSet()
                .filter { it != packageName && it !in builtInPackages }
                .forEach { pkg ->
                    val label = try {
                        val info = packageManager.getApplicationInfo(pkg, 0)
                        packageManager.getApplicationLabel(info).toString()
                    } catch (e: PackageManager.NameNotFoundException) {
                        pkg
                    }
                    val storageKey = PreferencesRepository.profileAppKey(pkg, 0)
                    items.add(
                        AppItem(
                            label = label,
                            packageName = pkg,
                            userId = 0,
                            storageKey = storageKey,
                            isSelected = prefsRepository.isUserSelectedApp(pkg, 0),
                            isWorkProfile = false,
                        )
                    )
                }
        }

        // Migrate any legacy plain-package keys (no ":userId" suffix) to explicit per-profile keys
        // so that each profile can be independently toggled in the UI.
        migrateLegacyKeys(items)

        // Recompute isSelected after migration: the migration may have added explicit per-profile
        // keys that replace a previous plain-key match, so refresh from preferences.
        val migratedItems = items.map { item ->
            item.copy(isSelected = prefsRepository.isUserSelectedApp(item.packageName, item.userId))
        }

        // Selected apps are shown first so users can quickly review their choices;
        // remaining apps are sorted alphabetically by label.
        migratedItems.sortedWith(compareByDescending<AppItem> { it.isSelected }.thenBy { it.label })
    }

    /**
     * Migrates legacy plain-package preference keys to the explicit "packageName:userId" format.
     *
     * Before per-profile support, selections were stored as plain package names (e.g.,
     * "com.example.app"). This migration converts each such key to an explicit per-profile key for
     * every profile in which the package was found, then removes the plain key.  After migration,
     * each profile is independently controllable because no plain key remains to trigger the
     * backward-compat fallback in [PreferencesRepository.isUserSelectedApp].
     */
    private fun migrateLegacyKeys(items: List<AppItem>) {
        val currentPackages = prefsRepository.getUserSelectedPackages()
        val plainKeys = currentPackages.filter {
            PreferencesRepository.PROFILE_KEY_SEPARATOR !in it
        }
        if (plainKeys.isEmpty()) return

        // Build a map of packageName → discovered userId values from the enumerated items
        val profilesByPackage = items.groupBy({ it.packageName }, { it.userId })

        for (plainKey in plainKeys) {
            val userIds = profilesByPackage[plainKey]
            if (userIds != null) {
                // Add an explicit per-profile key for every discovered profile of this package
                for (uid in userIds) {
                    prefsRepository.addUserSelectedPackage(
                        PreferencesRepository.profileAppKey(plainKey, uid)
                    )
                }
            } else {
                // Package no longer enumerated (e.g. uninstalled): migrate to personal profile
                prefsRepository.addUserSelectedPackage(
                    PreferencesRepository.profileAppKey(plainKey, 0)
                )
            }
            prefsRepository.removeUserSelectedPackage(plainKey)
        }
    }

    private class AppListAdapter(
        items: List<AppItem>,
        private val workProfileLabel: String,
        private val onToggle: (AppItem, Boolean) -> Unit,
    ) : ListAdapter<AppItem, AppListAdapter.ViewHolder>(AppItemDiffCallback()) {

        private val allItems: List<AppItem> = items
        private val selectedKeys: MutableSet<String> =
            items.filter { it.isSelected }.map { it.storageKey }.toMutableSet()

        /** When true, only work-profile apps are shown. */
        var filterWorkOnly: Boolean = false
            set(value) {
                field = value
                refreshDisplayList()
            }

        init {
            // Submit the initial list; DiffUtil will diff subsequent calls automatically.
            refreshDisplayList()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvLabel: TextView = view.findViewById(R.id.tvAppLabel)
            val tvPackage: TextView = view.findViewById(R.id.tvAppPackage)
            val checkBox: CheckBox = view.findViewById(R.id.checkBoxApp)
        }

        private class AppItemDiffCallback : DiffUtil.ItemCallback<AppItem>() {
            override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean =
                oldItem.storageKey == newItem.storageKey

            override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean =
                oldItem == newItem
        }

        private fun computeDisplayList(): List<AppItem> {
            val source = if (filterWorkOnly) allItems.filter { it.isWorkProfile } else allItems
            return source.sortedWith(
                compareByDescending<AppItem> { it.storageKey in selectedKeys }.thenBy { it.label }
            )
        }

        private fun refreshDisplayList() {
            submitList(computeDisplayList())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_app, parent, false)
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            holder.tvLabel.text = item.label
            // For work-profile apps, append the profile badge to the package name line so the
            // user can clearly distinguish them from the same app in their personal profile.
            holder.tvPackage.text = if (item.isWorkProfile) {
                "${item.packageName} • $workProfileLabel"
            } else {
                item.packageName
            }
            holder.checkBox.contentDescription = if (item.isWorkProfile) {
                "${item.label} • $workProfileLabel"
            } else {
                item.label
            }
            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.isChecked = item.storageKey in selectedKeys
            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedKeys.add(item.storageKey)
                } else {
                    selectedKeys.remove(item.storageKey)
                }
                onToggle(item, isChecked)
                // Re-apply current sort/filter (selected-first sort depends on selectedKeys)
                refreshDisplayList()
            }
            holder.itemView.setOnClickListener {
                holder.checkBox.toggle()
            }
        }
    }
}
