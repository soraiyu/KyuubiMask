# KyuubiMask 🦊

**Daily Privacy Guardian for Your Notifications**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android CI](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml/badge.svg)](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml)

[English](#english) | [日本語](#日本語)

---

## English

KyuubiMask protects your privacy in everyday situations—on trains, at work, around family and friends, or in cafes. It prevents sensitive notification content from being exposed when someone looks over your shoulder, appears on your lock screen, or gets captured in screenshots. Whether you're commuting, working in an office, or relaxing at home, KyuubiMask ensures your private messages stay private.

> **Note**: This project includes AI-generated code to accelerate development while maintaining code quality and security standards.

### Features

- 🔒 **Privacy First** - Completely offline, zero data collection
- 👀 **Shoulder Surfing Protection** - Prevents others from seeing notification content over your shoulder
- 📱 **Lock Screen Privacy** - Hides sensitive information even when your phone is locked
- 📸 **Prevents Screenshot Leaks** - Masked notifications won't expose sensitive data in screenshots
- 🦊 **Smart Notification Masking** - Replace sensitive content with generic text automatically
- 🚀 **Always-On Optimization** - Reduced memory usage, resistant to being killed by Android OS
- 🎯 **Multiple Apps Supported** - Slack, Discord, WhatsApp, LINE, Telegram, Signal, Gmail
- 🌍 **F-Droid Ready** - Open source, no tracking
- ⚡ **Lightweight** - Minimal memory footprint and battery impact
- 🔋 **Battery Efficient** - Optimized for continuous background operation

### Supported Apps

**Business Apps**
- Slack

**Messaging Apps**
- Discord
- WhatsApp
- LINE
- Telegram
- Signal

**Email Apps**
- Gmail

### Privacy Statement

Your privacy is our top priority in every daily situation. See full [Privacy Policy](PRIVACY.md) for details.

KyuubiMask protects your notifications everywhere—in crowded trains, at the office, around family, in cafes, or anywhere your phone might be visible to others. It prevents prying eyes from seeing your private messages, emails, and app notifications in all everyday scenarios.

- ❌ **No Internet Permission** - Works completely offline
- ❌ **No Data Collection** - We don't collect, store, or transmit any data
- ❌ **No Logging** - Notification content is never saved
- ❌ **No Tracking, Analytics, or Ads** - Zero third-party services
- ✅ **Fully Open Source** - Apache 2.0 license, all code is reviewable
- ✅ **Complete Offline Operation** - Data transmission is technically impossible

### Installation

#### F-Droid (Recommended)
Preparing for F-Droid submission. Perfect for privacy-conscious users who value open source software.

#### GitHub Releases
Download the latest APK from the releases page.

**📥 APK Download Guide**: See [DOWNLOAD_APK.md](DOWNLOAD_APK.md) for a complete guide with images!

### How to Use

1. Install the APK
2. Open the app
3. Tap "Grant Notification Access"
4. Enable KyuubiMask in the settings screen
5. Send a notification from a target app (e.g., Slack)
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

#### Tailscale Helpers (dev container)

If you're using this repository in a dev container without systemd, use these helper scripts:

```bash
# Start/reconnect Tailscale
./scripts/tailscale-up.sh

# Disconnect from Tailscale
./scripts/tailscale-down.sh

# Stop local tailscaled daemon
./scripts/tailscaled-stop.sh
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

KyuubiMaskは、電車の中、職場、家族や友人の近く、カフェなど、日常のあらゆる場面であなたのプライバシーを守ります。肩越しに覗かれる、ロック画面にそのまま表示される、スクリーンショットに写り込むといった日常的なリスクから、機密性の高い通知内容を保護します。通勤中でも、オフィスでも、自宅でリラックスしている時でも、KyuubiMaskがあなたのプライベートメッセージをしっかり守ります。

> **注意**: このプロジェクトには、コード品質とセキュリティ基準を維持しながら開発を加速するため、AI生成コードが含まれています。

### 特徴

- 🔒 **プライバシー第一** - 完全オフライン、データ収集なし
- 👀 **肩越し覗き見防止** - 他人に通知内容を見られないように保護
- 📱 **ロック画面プライバシー** - 画面ロック時も機密情報を隠す
- 📸 **スクリーンショット漏洩防止** - マスクされた通知は画面キャプチャでも安全
- 🦊 **スマート通知マスキング** - 機密情報を自動的に一般的なテキストに置き換え
- 🚀 **常時起動最適化** - メモリ使用量を削減し、終了されにくい設計
- 🎯 **複数アプリ対応** - Slack、Discord、WhatsApp、LINE、Telegram、Signal、Gmail
- 🌍 **F-Droid準拠** - オープンソース、トラッキングなし
- ⚡ **軽量** - 最小限のメモリフットプリントとバッテリー影響
- 🔋 **バッテリー効率** - 継続的なバックグラウンド動作に最適化

### 対応アプリ

**ビジネスアプリ**
- Slack

**メッセージアプリ**
- Discord
- WhatsApp
- LINE
- Telegram
- Signal

**メールアプリ**
- Gmail

### プライバシーステートメント

お客様のプライバシーが日常のあらゆる場面で最優先事項です。詳細は [プライバシーポリシー](PRIVACY.md) をご覧ください。

KyuubiMaskは、満員電車、オフィス、家族の近く、カフェなど、スマートフォンが他人の目に触れる可能性があるあらゆる場所で通知を保護します。プライベートメッセージ、メール、アプリ通知を覗き見から守り、日常的なシーンでのプライバシー漏洩を防ぎます。

- ❌ **インターネット権限なし** - 完全オフライン動作
- ❌ **データ収集なし** - データの収集、保存、送信は一切行いません
- ❌ **ログ記録なし** - 通知内容は一切保存されません
- ❌ **トラッキング、アナリティクス、広告なし** - サードパーティサービス不使用
- ✅ **完全オープンソース** - Apache 2.0ライセンス、全コードレビュー可能
- ✅ **完全オフライン動作** - データ送信は技術的に不可能

### インストール

#### F-Droid (推奨)
F-Droid申請準備中です。完全にオープンソースで、プライバシーを重視するユーザーに最適です。

#### GitHub Releases
リリースページから最新のAPKをダウンロードできます。

**📥 APKダウンロード方法**: [DOWNLOAD_APK.md](DOWNLOAD_APK.md) に画像付き完全ガイドがあります！

### 使い方

1. APKをインストール
2. アプリを開く
3. 「Grant Notification Access」をタップ
4. 設定画面でKyuubiMaskを有効化
5. マスク対象アプリ（Slack等）から通知を送信
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

#### Tailscale補助スクリプト（dev container向け）

このリポジトリを systemd なしの dev container で使う場合は、次のスクリプトを利用してください。

```bash
# Tailscale の開始/再接続
./scripts/tailscale-up.sh

# Tailscale から切断
./scripts/tailscale-down.sh

# ローカル tailscaled デーモン停止
./scripts/tailscaled-stop.sh
```

### 質問・相談・コントリビューション

作りたいアプリや機能についてのご相談、質問、提案など、お気軽にお寄せください！

- 💬 [GitHubのIssueで質問する](https://github.com/soraiyu/KyuubiMask/issues)
- 🤝 [コントリビューションガイドを見る](CONTRIBUTING.md)
- 💡 アイデアの共有や意見交換も大歓迎です

どんな相談でも歓迎します。コミュニティでサポートいたします。

### ライセンス

このプロジェクトは [Apache License 2.0](LICENSE) の下でライセンスされています。
