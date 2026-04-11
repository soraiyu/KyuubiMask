/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask.strategy

/**
 * Masking strategy dedicated to Slack.
 *
 * Since Slack notifications contain channel names, sender names, and message bodies,
 * their content is masked to protect user privacy.
 * Currently uses the generic masking logic of AbstractMaskStrategy.
 *
 * Reference implementation for adding support for new apps:
 * 1. Create a class extending AbstractMaskStrategy (copy and edit this file)
 * 2. Return the target package name in canHandle()
 * 3. Override getMaskedText() or buildMaskedNotification() as needed
 * 4. Add register() to the init block of NotificationMaskStrategyRegistry
 */
class SlackMaskStrategy : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName == SLACK_PACKAGE

    companion object {
        const val SLACK_PACKAGE = "com.Slack"
    }
}
