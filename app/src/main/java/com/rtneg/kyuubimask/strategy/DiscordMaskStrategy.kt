/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask.strategy

/**
 * Masking strategy dedicated to Discord.
 *
 * Since Discord notifications contain server names, channel names, and message bodies,
 * their content is masked to protect user privacy.
 * Currently uses the generic masking logic of AbstractMaskStrategy.
 */
class DiscordMaskStrategy : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName.equals(DISCORD_PACKAGE, ignoreCase = true)

    companion object {
        const val DISCORD_PACKAGE = "com.discord"
    }
}
