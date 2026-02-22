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
 * 通知マスク戦略のインターフェース
 *
 * アプリごとに通知形式（extras、MessagingStyle 等）が異なるため、
 * Strategy パターンを用いてアプリ固有の処理を分離する。
 * 新しいアプリへの対応は新クラスを作成して Registry に登録するだけでよい。
 */
interface NotificationMaskStrategy {

    companion object {
        // マスク済み通知を識別するタグ（二重マスク防止に使用）
        const val MASKED_TAG = "kyuubimask_masked"
    }

    /**
     * このストラテジーが指定パッケージを処理できるか判定する
     */
    fun canHandle(packageName: String): Boolean

    /**
     * 元の通知をキャンセルし、マスク済み通知を投稿する
     * @return マスク処理が成功した場合 true
     */
    fun mask(sbn: StatusBarNotification, listenerService: NotificationListenerService): Boolean
}
