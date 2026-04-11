/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask.strategy

/**
 * Generic masking strategy for user-selected apps.
 *
 * Unlike the app-specific strategies (Slack, Discord, …), this strategy accepts
 * any package name supplied at construction time. It is instantiated dynamically
 * when the user selects a custom app to mask via SelectAppsActivity.
 */
class GenericMaskStrategy(private val packageName: String) : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName == this.packageName
}
