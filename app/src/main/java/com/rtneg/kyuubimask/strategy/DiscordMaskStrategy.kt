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
