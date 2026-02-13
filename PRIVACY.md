# プライバシーポリシー / Privacy Policy

[日本語](#日本語) | [English](#english)

---

## 日本語

KyuubiMaskは、ユーザーのプライバシーを最優先に設計されています。

### データ収集

**一切のデータ収集を行いません**

- 通知の内容を記録、保存、送信することはありません
- ユーザーの個人情報を収集しません
- 使用統計やアナリティクスを収集しません

### 使用するパーミッション

#### POST_NOTIFICATIONS
- **目的**: Android 13以降で通知を表示するために必要
- **使用方法**: マスキングされた通知を表示するためにのみ使用

#### BIND_NOTIFICATION_LISTENER_SERVICE
- **目的**: 他のアプリからの通知を読み取るために必要
- **使用方法**: 通知をマスキングするために読み取り、内容を即座に置き換えます
- **重要**: 通知の内容は一切保存されません

### ネットワーク接続

- **インターネット接続権限なし**
- 完全オフラインで動作します
- データ送信は技術的に不可能です
- ネットワーク通信を一切行いません

### データ保存

#### 保存するもの
- マスク対象アプリのリスト（パッケージ名のみ）
- サービス有効/無効の設定

#### 保存しないもの
- 通知の内容
- 通知の送信者情報
- 通知の受信時刻
- その他の個人情報

#### ストレージ
- SharedPreferencesにマスク対象アプリのリストと設定のみ保存
- `allowBackup="false"`により、バックアップも無効化
- 通知の内容はメモリ上でのみ処理され、永続化されません

### サードパーティサービス

- ❌ アナリティクス（Google Analytics等）なし
- ❌ クラッシュレポート（Crashlytics等）なし
- ❌ 広告SDKなし
- ❌ トラッキングSDKなし
- ❌ Google Play Servicesなし

### オープンソース

このアプリは完全にオープンソースです。全てのコードを確認できます：

https://github.com/soraiyu/KyuubiMask

### ライセンス

Apache License 2.0

詳細は[LICENSE](LICENSE)ファイルをご覧ください。

### セキュリティ

プライバシーやセキュリティに関する懸念事項を発見した場合は、
GitHubのIssueでご報告ください：

https://github.com/soraiyu/KyuubiMask/issues

---

## English

KyuubiMask is designed with user privacy as the top priority.

### Data Collection

**We do not collect any data**

- We do not record, store, or transmit notification content
- We do not collect personal information
- We do not collect usage statistics or analytics

### Permissions Used

#### POST_NOTIFICATIONS
- **Purpose**: Required to display notifications on Android 13+
- **Usage**: Only used to display masked notifications

#### BIND_NOTIFICATION_LISTENER_SERVICE
- **Purpose**: Required to read notifications from other apps
- **Usage**: Read notifications to mask them, replacing content immediately
- **Important**: Notification content is never stored

### Network Connection

- **No internet permission**
- Works completely offline
- Data transmission is technically impossible
- No network communication whatsoever

### Data Storage

#### What we store
- List of apps to mask (package names only)
- Service enabled/disabled setting

#### What we DON'T store
- Notification content
- Notification sender information
- Notification timestamps
- Any other personal information

#### Storage Details
- Only app list and settings stored in SharedPreferences
- `allowBackup="false"` disables backup
- Notification content is processed in memory only, never persisted

### Third-Party Services

- ❌ No analytics (Google Analytics, etc.)
- ❌ No crash reporting (Crashlytics, etc.)
- ❌ No advertising SDKs
- ❌ No tracking SDKs
- ❌ No Google Play Services

### Open Source

This app is completely open source. You can review all the code:

https://github.com/soraiyu/KyuubiMask

### License

Apache License 2.0

See [LICENSE](LICENSE) file for details.

### Security

If you discover any privacy or security concerns, please report them
via GitHub Issues:

https://github.com/soraiyu/KyuubiMask/issues

---

最終更新 / Last Updated: 2026-02-13
