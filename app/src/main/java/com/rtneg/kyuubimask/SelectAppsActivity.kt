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
import android.content.pm.PackageManager
import android.os.Bundle
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
            recyclerView.adapter = AppListAdapter(items) { item, checked ->
                if (checked) {
                    prefsRepository.addUserSelectedPackage(item.packageName)
                } else {
                    prefsRepository.removeUserSelectedPackage(item.packageName)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private suspend fun loadAppItems(): List<AppItem> = withContext(Dispatchers.IO) {
        val userSelected = prefsRepository.getUserSelectedPackages()
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return@withContext packageManager
            .queryIntentActivities(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY)
            .map { it.activityInfo.packageName }
            .toSet()
            .filter { it != packageName && it !in builtInPackages }
            .map { pkg ->
                val label = try {
                    val info = packageManager.getApplicationInfo(pkg, 0)
                    packageManager.getApplicationLabel(info).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    pkg
                }
                AppItem(
                    label = label,
                    packageName = pkg,
                    isSelected = pkg in userSelected,
                )
            }
            .sortedWith(compareByDescending<AppItem> { it.isSelected }.thenBy { it.label })
            // Selected apps are shown first so users can quickly review their choices;
            // remaining apps are sorted alphabetically by label.
    }

    private class AppListAdapter(
        private val items: List<AppItem>,
        private val onToggle: (AppItem, Boolean) -> Unit,
    ) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

        private val selectedPackages: MutableSet<String> =
            items.filter { it.isSelected }.map { it.packageName }.toMutableSet()

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

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvLabel.text = item.label
            holder.tvPackage.text = item.packageName
            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.isChecked = item.packageName in selectedPackages
            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedPackages.add(item.packageName)
                } else {
                    selectedPackages.remove(item.packageName)
                }
                onToggle(item, isChecked)
            }
            holder.itemView.setOnClickListener {
                holder.checkBox.toggle()
            }
        }
    }
}
