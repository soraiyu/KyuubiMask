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

import com.rtneg.kyuubimask.strategy.DefaultMaskStrategy
import com.rtneg.kyuubimask.strategy.LineMaskStrategy
import com.rtneg.kyuubimask.strategy.WhatsAppMaskStrategy

/**
 * 通知マスク戦略のレジストリ（シングルトン）
 *
 * 新しいアプリ対応を追加する方法:
 * 1. AbstractMaskStrategy を継承したクラスを作成（例: DiscordMaskStrategy）
 * 2. この init ブロックに register(DiscordMaskStrategy()) を追加
 *    ※ DefaultMaskStrategy より前に登録すること
 *
 * ストラテジーは登録順に検索され、最初にマッチしたものが使用される。
 */
object NotificationMaskStrategyRegistry {

    private val strategies = mutableListOf<NotificationMaskStrategy>()

    init {
        // アプリ固有のストラテジーを先に登録（追加はここに register() を追記するだけ）
        register(WhatsAppMaskStrategy())
        register(LineMaskStrategy())
        // 例: register(DiscordMaskStrategy())
        // 例: register(XMaskStrategy())

        // フォールバック用のデフォルトストラテジーは必ず最後に登録
        register(DefaultMaskStrategy())
    }

    /**
     * 新しいストラテジーを登録する
     * DefaultMaskStrategy より前に呼び出すこと
     */
    fun register(strategy: NotificationMaskStrategy) {
        strategies.add(strategy)
    }

    /**
     * パッケージ名に対応するストラテジーを検索する
     * 登録順に検索し、最初にマッチしたものを返す
     * DefaultMaskStrategy が登録されている限り null にはならない
     */
    fun findStrategy(packageName: String): NotificationMaskStrategy? =
        strategies.firstOrNull { it.canHandle(packageName) }
}
