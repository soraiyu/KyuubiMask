/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Tile added")
        }
        updateTileState()
    }

    /** Called when the tile is removed from the Quick Settings panel. */
    override fun onTileRemoved() {
        super.onTileRemoved()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Tile removed")
        }
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
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Toggled masking: enabled=$newEnabled")
        }
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
        tile.label = getString(if (enabled) R.string.tile_label_masking_on else R.string.tile_label_masking_off)
        tile.updateTile()
    }
}
