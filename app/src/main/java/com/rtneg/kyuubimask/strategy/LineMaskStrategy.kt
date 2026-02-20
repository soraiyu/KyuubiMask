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
 * LINE 専用のマスク戦略
 *
 * LINE はグループトークや複数ユーザーからの通知をまとめる形式を持つため、
 * 専用ストラテジーで将来的な拡張に備えている。
 * 現時点では AbstractMaskStrategy の汎用マスク処理を使用する。
 */
class LineMaskStrategy : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName == LINE_PACKAGE

    companion object {
        const val LINE_PACKAGE = "jp.naver.line.android"
    }
}
