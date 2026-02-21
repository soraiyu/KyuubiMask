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

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.rtneg.kyuubimask.KyuubiMaskApp
import com.rtneg.kyuubimask.NotificationMaskStrategy
import com.rtneg.kyuubimask.R
import com.rtneg.kyuubimask.data.PreferencesRepository
import java.util.Objects

/**
 * マスク処理の共通ロジックを持つ抽象基底クラス
 *
 * 新しいアプリ対応を追加する最小手順:
 * 1. このクラスを継承したクラスを作成（例: DiscordMaskStrategy）
 * 2. canHandle() でパッケージ名を返す
 * 3. NotificationMaskStrategyRegistry の init ブロックに登録する
 *
 * 通知テキストや通知全体をカスタマイズしたい場合は
 * getMaskedText() または buildMaskedNotification() をオーバーライドする。
 */
abstract class AbstractMaskStrategy : NotificationMaskStrategy {

    // PreferencesRepository はストラテジーインスタンスごとに初回呼び出し時のみ生成する
    // applicationContext を使用してサービス Context のリークを防ぐ
    @Volatile
    private var prefsRepository: PreferencesRepository? = null

    private fun getPrefsRepository(context: Context): PreferencesRepository =
        prefsRepository ?: synchronized(this) {
            prefsRepository ?: PreferencesRepository(context.applicationContext).also {
                prefsRepository = it
            }
        }

    /**
     * マスク後の通知本文テキストを返す
     * アプリ固有のサブクラスでオーバーライドしてカスタマイズ可能
     */
    protected open fun getMaskedText(context: Context): String =
        context.getString(R.string.masked_text)

    override fun mask(
        sbn: StatusBarNotification,
        listenerService: NotificationListenerService,
    ): Boolean {
        val context: Context = listenerService

        // Android 13 以上では POST_NOTIFICATIONS 権限を確認
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        val packageName = sbn.packageName

        // 元の通知をキャンセル
        listenerService.cancelNotification(sbn.key)

        // アプリ名を取得（失敗時は "App" にフォールバック）
        val appName = try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            "App"
        }

        // 通知 ID を生成（パッケージ名・ID・タグの組み合わせで一意化）
        val notificationId = Objects.hash(packageName, sbn.id, sbn.tag ?: "")

        // 元の contentIntent を保持し、なければランチャーインテントを使用
        val contentIntent = sbn.notification.contentIntent
            ?: createLaunchIntent(context, packageName, notificationId)

        // ユーザー設定（サウンド・バイブレーション）を初回のみ生成したリポジトリから取得
        val prefsRepository = getPrefsRepository(context)

        // マスク済み通知を構築して投稿
        val maskedNotification =
            buildMaskedNotification(context, sbn, appName, contentIntent, prefsRepository)
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(NotificationMaskStrategy.MASKED_TAG, notificationId, maskedNotification)

        return true
    }

    /**
     * マスク済み通知を構築する
     * 通知全体をカスタマイズしたい場合はサブクラスでオーバーライドする
     */
    protected open fun buildMaskedNotification(
        context: Context,
        sbn: StatusBarNotification,
        appName: String,
        contentIntent: PendingIntent?,
        prefsRepository: PreferencesRepository,
    ): Notification {
        // サウンド・バイブレーション・ライト設定を適用
        var defaults = Notification.DEFAULT_LIGHTS
        if (prefsRepository.notificationSound) defaults = defaults or Notification.DEFAULT_SOUND
        if (prefsRepository.notificationVibrate) defaults = defaults or Notification.DEFAULT_VIBRATE

        return NotificationCompat.Builder(context, KyuubiMaskApp.CHANNEL_ID)
            .setContentTitle(appName)
            .setContentText(getMaskedText(context))
            .setSmallIcon(R.drawable.ic_mask)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .setDefaults(defaults)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                // 元の並び替えキーを保持
                sbn.notification.sortKey?.let { setSortKey(it) }
            }
            .build()
    }

    /**
     * アプリのランチャーインテントから PendingIntent を作成する
     */
    private fun createLaunchIntent(
        context: Context,
        packageName: String,
        notificationId: Int,
    ): PendingIntent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return null
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP,
        )
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, notificationId, launchIntent, flags)
    }
}
