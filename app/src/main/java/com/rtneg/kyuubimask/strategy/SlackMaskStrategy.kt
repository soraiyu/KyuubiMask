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
