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
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        lifecycleScope.launch {
            val items = loadAppItems()
            recyclerView.adapter = AppListAdapter(items, getString(R.string.label_work_profile)) { item, checked ->
                if (checked) {
                    prefsRepository.addUserSelectedPackage(item.storageKey)
                } else {
                    prefsRepository.removeUserSelectedPackage(item.storageKey)
                }
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

        if (launcherApps != null && !profiles.isNullOrEmpty()) {
            // Enumerate apps across all profiles (personal + work/managed)
            for (profile in profiles) {
                // UserHandle.hashCode() returns the user ID (mHandle field) on all API levels.
                // UserHandle.getIdentifier() is equivalent but only available from API 24.
                val userId = profile.hashCode()

                try {
                    launcherApps.getActivityList(null, profile)
                        .map { it.applicationInfo.packageName }
                        .toSet()
                        .filter { it != packageName && it !in builtInPackages }
                        .forEach { pkg ->
                            val label = try {
                                launcherApps.getActivityList(pkg, profile)
                                    .firstOrNull()
                                    ?.label
                                    ?.toString()
                                    ?: pkg
                            } catch (e: Exception) {
                                pkg
                            }
                            val storageKey = PreferencesRepository.profileAppKey(pkg, userId)
                            items.add(
                                AppItem(
                                    label = label,
                                    packageName = pkg,
                                    userId = userId,
                                    storageKey = storageKey,
                                    isSelected = prefsRepository.isUserSelectedApp(pkg, userId),
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
                        )
                    )
                }
        }

        items.sortWith(compareByDescending<AppItem> { it.isSelected }.thenBy { it.label })
        // Selected apps are shown first so users can quickly review their choices;
        // remaining apps are sorted alphabetically by label.
        items
    }

    private class AppListAdapter(
        items: List<AppItem>,
        private val workProfileLabel: String,
        private val onToggle: (AppItem, Boolean) -> Unit,
    ) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

        private val mutableItems: MutableList<AppItem> = items.toMutableList()
        private val selectedKeys: MutableSet<String> =
            items.filter { it.isSelected }.map { it.storageKey }.toMutableSet()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvLabel: TextView = view.findViewById(R.id.tvAppLabel)
            val tvPackage: TextView = view.findViewById(R.id.tvAppPackage)
            val checkBox: CheckBox = view.findViewById(R.id.checkBoxApp)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_app, parent, false)
            )

        override fun getItemCount(): Int = mutableItems.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mutableItems[position]
            holder.tvLabel.text = item.label
            // For work-profile apps, append the profile badge to the package name line so the
            // user can clearly distinguish them from the same app in their personal profile.
            holder.tvPackage.text = if (item.userId != 0) {
                "${item.packageName} • $workProfileLabel"
            } else {
                item.packageName
            }
            holder.checkBox.contentDescription = item.label
            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.isChecked = item.storageKey in selectedKeys
            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedKeys.add(item.storageKey)
                } else {
                    selectedKeys.remove(item.storageKey)
                }
                onToggle(item, isChecked)
                // Re-sort to keep selected apps at the top
                mutableItems.sortWith(
                    compareByDescending<AppItem> { it.storageKey in selectedKeys }.thenBy { it.label }
                )
                notifyDataSetChanged()
            }
            holder.itemView.setOnClickListener {
                holder.checkBox.toggle()
            }
        }
    }
}
