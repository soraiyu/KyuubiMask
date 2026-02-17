# KyuubiMask 🦊

プライバシー重視の通知マスキングツール

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android CI](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml/badge.svg)](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml)

## 特徴

- 🔒 **プライバシー第一** - 完全オフライン、データ収集なし
- 🦊 **通知のマスキング** - 機密情報を画面共有から保護
- 🚀 **常時起動最適化** - メモリ使用量を削減し、終了されにくい設計
- 🎯 **12個のアプリに対応** - メッセージ、メール、ビジネスアプリ
- 🌍 **F-Droid準拠** - オープンソース、トラッキングなし

## 対応アプリ

### メッセージアプリ
- WhatsApp, Telegram, LINE, Signal, Discord

### メールアプリ
- Gmail, K-9 Mail

### ビジネスアプリ
- Slack, Microsoft Teams, Zoom, Notion, Jira

設定画面で簡単に選択できます。

## インストール

### F-Droid (推奨)
F-Droidでの公開準備中です。完全にオープンソースで、プライバシーを重視するユーザーに最適です。

### GitHub Releases
リリースページから最新のAPKをダウンロードできます。

**📥 APKダウンロード方法**: [DOWNLOAD_APK.md](DOWNLOAD_APK.md) に画像付き完全ガイドがあります！

## ビルド手順

詳細なビルドと実機テストのガイドは [BUILD_AND_TEST.md](BUILD_AND_TEST.md) をご覧ください。

### ローカルビルド

```bash
cd KyuubiMask

# Debug APK
./gradlew assembleDebug

# Release APK (署名が必要)
./gradlew assembleRelease
```

APKは `app/build/outputs/apk/debug/app-debug.apk` に生成されます。

### CI/CD 自動ビルド

このリポジトリには GitHub Actions が設定されており、以下の場合に自動的にビルドが実行されます：

- **Push時**: main, master, develop, copilot/** ブランチへのpush
- **Pull Request時**: main, master, develop ブランチへのPR
- **タグ作成時**: `v*` タグでリリースビルド

ビルドされたAPKは、GitHub Actions の Artifacts からダウンロードできます。

**📥 APKダウンロード方法**: [DOWNLOAD_APK.md](DOWNLOAD_APK.md) に画像付き完全ガイドがあります！

## テスト手順

1. APKをインストール
2. アプリを開く
3. 「Grant Notification Access」をタップ
4. 設定画面でKyuubiMaskを有効化
5. マスク対象アプリ（WhatsApp等）から通知を送信
6. 通知内容が「New notification」に置き換わることを確認

## プライバシー

完全なプライバシーポリシーは [PRIVACY.md](PRIVACY.md) をご覧ください。

- ❌ インターネット権限なし
- ❌ データ収集なし
- ❌ ログ記録なし（通知内容は一切保存されません）
- ❌ トラッキング、アナリティクス、広告なし
- ✅ 完全オフライン動作
- ✅ オープンソース（Apache 2.0）

## メモリ最適化

最近のAndroidの仕様では、バックグラウンドアプリがメモリ不足で終了されることがあります。KyuubiMaskは常時起動を維持するために以下の最適化を実施しています：

- 🚀 **フォアグラウンドサービス化** - Android OSに終了されにくくなります
- 💾 **最小メモリ使用量** - デバッグログやグループ化状態などの不要な機能を削除
- ⚡ **シンプルな実装** - 通知マスキングのコア機能のみに集中
- 🔋 **バッテリー効率** - 不要な処理を削減し、バッテリー消費を最小化

これらの最適化により、通知マスキング機能を維持しながら、メモリ使用量を削減し、アプリの常時起動を実現しています。

## 機能

### 通知マスキング
画面共有やプレゼン中に、選択したアプリからの通知内容を「新しい通知」のような一般的なテキストに置き換えます。

## 質問・相談・コントリビューション

作りたいアプリや機能についてのご相談、質問、提案など、お気軽にお寄せください！

- 💬 [GitHubのIssueで質問する](https://github.com/soraiyu/KyuubiMask/issues)
- 🤝 [コントリビューションガイドを見る](CONTRIBUTING.md)
- 💡 アイデアの共有や意見交換も大歓迎です

どんな相談でも歓迎します。コミュニティでサポートいたします。

## ライセンス

このプロジェクトは [Apache License 2.0](LICENSE) の下でライセンスされています。

## 開発・テスト

```bash
# テストの実行
./gradlew test

# インストルメントテストの実行（エミュレーター/実機が必要）
./gradlew connectedAndroidTest
```
