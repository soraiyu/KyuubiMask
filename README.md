# KyuubiMask 🦊

**A Privacy-Focused Notification Masking Tool for Android**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android CI](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml/badge.svg)](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml)

[English](#english) | [日本語](#日本語)

---

## English

KyuubiMask protects your privacy during screen sharing and presentations by masking sensitive notification content from messaging, email, and business apps.

> **Note**: This project includes AI-generated code to accelerate development while maintaining code quality and security standards.

### Features

- 🔒 **Privacy First** - Completely offline, zero data collection
- 🦊 **Notification Masking** - Protect sensitive information during screen sharing
- 🚀 **Always-On Optimization** - Reduced memory usage, resistant to being killed by Android OS
- 🎯 **12 Apps Supported** - Messaging, email, and business apps
- 🌍 **F-Droid Ready** - Open source, no tracking
- ⚡ **Lightweight** - Minimal memory footprint and battery impact
- 🔋 **Battery Efficient** - Optimized for continuous background operation

### Supported Apps

**Messaging Apps**
- WhatsApp, Telegram, LINE, Signal, Discord

**Email Apps**
- Gmail, K-9 Mail

**Business Apps**
- Slack, Microsoft Teams, Zoom, Notion, Jira

You can easily select which apps to mask in the settings screen.

### Privacy Statement

Your privacy is our top priority. See full [Privacy Policy](PRIVACY.md) for details.

- ❌ **No Internet Permission** - Works completely offline
- ❌ **No Data Collection** - We don't collect, store, or transmit any data
- ❌ **No Logging** - Notification content is never saved
- ❌ **No Tracking, Analytics, or Ads** - Zero third-party services
- ✅ **Fully Open Source** - Apache 2.0 license, all code is reviewable
- ✅ **Complete Offline Operation** - Data transmission is technically impossible

### Installation

#### F-Droid (Recommended)
Coming soon to F-Droid. Perfect for privacy-conscious users who value open source software.

#### GitHub Releases
Download the latest APK from the releases page.

**📥 APK Download Guide**: See [DOWNLOAD_APK.md](DOWNLOAD_APK.md) for a complete guide with images!

### How to Use

1. Install the APK
2. Open the app
3. Tap "Grant Notification Access"
4. Enable KyuubiMask in the settings screen
5. Send a notification from a target app (e.g., WhatsApp)
6. Verify that the notification content is replaced with "New notification"

### Building from Source

For detailed build and testing instructions, see [BUILD_AND_TEST.md](BUILD_AND_TEST.md).

#### Local Build

```bash
cd KyuubiMask

# Debug APK
./gradlew assembleDebug

# Release APK (requires signing)
./gradlew assembleRelease
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

#### CI/CD Automatic Builds

This repository uses GitHub Actions for automatic builds on:

- **Push**: main, master, develop, copilot/** branches
- **Pull Requests**: PRs to main, master, develop branches
- **Tags**: Release builds for `v*` tags

Built APKs are available in GitHub Actions Artifacts.

### Memory Optimization

Recent Android versions may terminate background apps due to memory pressure. KyuubiMask implements several optimizations to maintain continuous operation:

- 🚀 **Foreground Service** - Less likely to be killed by Android OS
- 💾 **Minimal Memory Usage** - Removed unnecessary features like debug logs and grouping state
- ⚡ **Simple Implementation** - Focus on core notification masking functionality
- 🔋 **Battery Efficiency** - Reduced processing to minimize battery consumption

These optimizations ensure the notification masking feature remains active while using minimal system resources.

### Development & Testing

```bash
# Run tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

### Questions, Support & Contributions

Feel free to reach out with questions, suggestions, or ideas!

- 💬 [Ask questions on GitHub Issues](https://github.com/soraiyu/KyuubiMask/issues)
- 🤝 [See Contribution Guidelines](CONTRIBUTING.md)
- 💡 Ideas and discussions are always welcome

All questions are welcome. The community is here to support you.

### License

This project is licensed under the [Apache License 2.0](LICENSE).

---

## 日本語

KyuubiMaskは、画面共有やプレゼン中にメッセージング、メール、ビジネスアプリからの機密情報を含む通知をマスクして、プライバシーを保護します。

> **注意**: このプロジェクトには、コード品質とセキュリティ基準を維持しながら開発を加速するため、AI生成コードが含まれています。

### 特徴

- 🔒 **プライバシー第一** - 完全オフライン、データ収集なし
- 🦊 **通知のマスキング** - 機密情報を画面共有から保護
- 🚀 **常時起動最適化** - メモリ使用量を削減し、終了されにくい設計
- 🎯 **12個のアプリに対応** - メッセージ、メール、ビジネスアプリ
- 🌍 **F-Droid準拠** - オープンソース、トラッキングなし
- ⚡ **軽量** - 最小限のメモリフットプリントとバッテリー影響
- 🔋 **バッテリー効率** - 継続的なバックグラウンド動作に最適化

### 対応アプリ

**メッセージアプリ**
- WhatsApp, Telegram, LINE, Signal, Discord

**メールアプリ**
- Gmail, K-9 Mail

**ビジネスアプリ**
- Slack, Microsoft Teams, Zoom, Notion, Jira

設定画面で簡単に選択できます。

### プライバシーステートメント

お客様のプライバシーが最優先事項です。詳細は [プライバシーポリシー](PRIVACY.md) をご覧ください。

- ❌ **インターネット権限なし** - 完全オフライン動作
- ❌ **データ収集なし** - データの収集、保存、送信は一切行いません
- ❌ **ログ記録なし** - 通知内容は一切保存されません
- ❌ **トラッキング、アナリティクス、広告なし** - サードパーティサービス不使用
- ✅ **完全オープンソース** - Apache 2.0ライセンス、全コードレビュー可能
- ✅ **完全オフライン動作** - データ送信は技術的に不可能

### インストール

#### F-Droid (推奨)
F-Droidでの公開準備中です。完全にオープンソースで、プライバシーを重視するユーザーに最適です。

#### GitHub Releases
リリースページから最新のAPKをダウンロードできます。

**📥 APKダウンロード方法**: [DOWNLOAD_APK.md](DOWNLOAD_APK.md) に画像付き完全ガイドがあります！

### 使い方

1. APKをインストール
2. アプリを開く
3. 「Grant Notification Access」をタップ
4. 設定画面でKyuubiMaskを有効化
5. マスク対象アプリ（WhatsApp等）から通知を送信
6. 通知内容が「New notification」に置き換わることを確認

### ソースからのビルド

詳細なビルドと実機テストのガイドは [BUILD_AND_TEST.md](BUILD_AND_TEST.md) をご覧ください。

#### ローカルビルド

```bash
cd KyuubiMask

# Debug APK
./gradlew assembleDebug

# Release APK (署名が必要)
./gradlew assembleRelease
```

APKは `app/build/outputs/apk/debug/app-debug.apk` に生成されます。

#### CI/CD 自動ビルド

このリポジトリには GitHub Actions が設定されており、以下の場合に自動的にビルドが実行されます：

- **Push時**: main, master, develop, copilot/** ブランチへのpush
- **Pull Request時**: main, master, develop ブランチへのPR
- **タグ作成時**: `v*` タグでリリースビルド

ビルドされたAPKは、GitHub Actions の Artifacts からダウンロードできます。

### メモリ最適化

最近のAndroidの仕様では、バックグラウンドアプリがメモリ不足で終了されることがあります。KyuubiMaskは常時起動を維持するために以下の最適化を実施しています：

- 🚀 **フォアグラウンドサービス化** - Android OSに終了されにくくなります
- 💾 **最小メモリ使用量** - デバッグログやグループ化状態などの不要な機能を削除
- ⚡ **シンプルな実装** - 通知マスキングのコア機能のみに集中
- 🔋 **バッテリー効率** - 不要な処理を削減し、バッテリー消費を最小化

これらの最適化により、通知マスキング機能を維持しながら、メモリ使用量を削減し、アプリの常時起動を実現しています。

### 開発・テスト

```bash
# テストの実行
./gradlew test

# インストルメントテストの実行（エミュレーター/実機が必要）
./gradlew connectedAndroidTest
```

### 質問・相談・コントリビューション

作りたいアプリや機能についてのご相談、質問、提案など、お気軽にお寄せください！

- 💬 [GitHubのIssueで質問する](https://github.com/soraiyu/KyuubiMask/issues)
- 🤝 [コントリビューションガイドを見る](CONTRIBUTING.md)
- 💡 アイデアの共有や意見交換も大歓迎です

どんな相談でも歓迎します。コミュニティでサポートいたします。

### ライセンス

このプロジェクトは [Apache License 2.0](LICENSE) の下でライセンスされています。
