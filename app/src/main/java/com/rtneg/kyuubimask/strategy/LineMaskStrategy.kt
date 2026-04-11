/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask.strategy

/**
 * Masking strategy dedicated to LINE.
 *
 * Since LINE notifications contain contact names and message bodies,
 * their content is masked to protect user privacy.
 * Currently uses the generic masking logic of AbstractMaskStrategy.
 */
class LineMaskStrategy : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName == LINE_PACKAGE

    companion object {
        const val LINE_PACKAGE = "jp.naver.line.android"
    }
}
