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
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.rtneg.kyuubimask.data.PreferencesRepository

/**
 * Quick Settings Tile that toggles notification masking on/off.
 *
 * Add to the device Quick Settings panel to enable one-tap masking control
 * without opening the app.
 */
class MaskToggleTileService : TileService() {

    private lateinit var prefsRepository: PreferencesRepository

    companion object {
        private const val TAG = "MaskToggleTile"
    }

    override fun onCreate() {
        super.onCreate()
        prefsRepository = (applicationContext as KyuubiMaskApp).prefsRepository
    }

    /** Called when the tile is added to the Quick Settings panel. */
    override fun onTileAdded() {
        super.onTileAdded()
        Log.d(TAG, "Tile added")
        updateTileState()
    }

    /** Called when the tile is removed from the Quick Settings panel. */
    override fun onTileRemoved() {
        super.onTileRemoved()
        Log.d(TAG, "Tile removed")
    }

    /**
     * Called when the Quick Settings panel becomes visible and the tile
     * should reflect the current state.
     */
    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    /** Called when the user taps the tile. Toggles masking on/off. */
    override fun onClick() {
        super.onClick()
        val newEnabled = !prefsRepository.isServiceEnabled
        prefsRepository.isServiceEnabled = newEnabled

        // Broadcast the change so the service picks it up immediately.
        // Restrict to this app's package to prevent other apps from receiving
        // sensitive masking-state information.
        val intent = Intent(PreferencesRepository.ACTION_MASK_TOGGLED).apply {
            putExtra(PreferencesRepository.EXTRA_ENABLED, newEnabled)
            setPackage(packageName)
        }
        sendBroadcast(intent)

        updateTileState()
        Log.d(TAG, "Toggled masking: enabled=$newEnabled")
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /** Reads the current preference and updates the tile label/icon/state. */
    private fun updateTileState() {
        val tile = qsTile ?: return
        val enabled = prefsRepository.isServiceEnabled

        tile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(
            this,
            if (enabled) R.drawable.ic_mask_on else R.drawable.ic_mask_off
        )
        tile.label = if (enabled) {
            getString(R.string.tile_label_masking_on)
        } else {
            getString(R.string.tile_label_masking_off)
        }
        tile.updateTile()
    }
}
