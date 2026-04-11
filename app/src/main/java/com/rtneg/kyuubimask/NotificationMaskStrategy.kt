/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
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
