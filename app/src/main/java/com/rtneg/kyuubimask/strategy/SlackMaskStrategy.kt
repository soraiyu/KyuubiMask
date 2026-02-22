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
 * Slack 専用のマスク戦略
 *
 * Slack はチャンネル名・送信者名・メッセージ本文が通知に含まれるため、
 * プライバシー保護の観点から通知内容をマスクする。
 * 現時点では AbstractMaskStrategy の汎用マスク処理を使用する。
 *
 * 新しいアプリを追加する場合の参考実装:
 * 1. AbstractMaskStrategy を継承したクラスを作成（このファイルをコピーして編集）
 * 2. canHandle() で対象パッケージ名を返す
 * 3. 必要に応じて getMaskedText() や buildMaskedNotification() をオーバーライド
 * 4. NotificationMaskStrategyRegistry の init ブロックに register() を追加
 */
class SlackMaskStrategy : AbstractMaskStrategy() {

    override fun canHandle(packageName: String): Boolean =
        packageName == SLACK_PACKAGE

    companion object {
        const val SLACK_PACKAGE = "com.Slack"
    }
}
