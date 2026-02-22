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

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Interface for notification masking strategies.
 *
 * Since each app has a different notification format (extras, MessagingStyle, etc.),
 * app-specific processing is separated using the Strategy pattern.
 * To add support for a new app, create a new class and register it in the Registry.
 */
interface NotificationMaskStrategy {

    companion object {
        // Tag to identify masked notifications (used to prevent double-masking)
        const val MASKED_TAG = "kyuubimask_masked"
    }

    /**
     * Determines whether this strategy can handle the specified package.
     */
    fun canHandle(packageName: String): Boolean

    /**
     * Cancels the original notification and posts a masked version.
     * @return true if masking succeeded
     */
    fun mask(sbn: StatusBarNotification, listenerService: NotificationListenerService): Boolean
}
