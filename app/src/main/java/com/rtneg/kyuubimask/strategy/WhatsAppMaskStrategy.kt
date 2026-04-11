/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask.strategy

/**
 * Masking strategy dedicated to WhatsApp.
 *
 * Since WhatsApp notifications contain contact names and message bodies,
 * their content is masked to protect user privacy.
 * Currently uses the generic masking logic of AbstractMaskStrategy.
 */
class WhatsAppMaskStrategy : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName == WHATSAPP_PACKAGE

    companion object {
        const val WHATSAPP_PACKAGE = "com.whatsapp"
    }
}
