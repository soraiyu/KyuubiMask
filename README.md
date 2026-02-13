# KyuubiMask 🦊

プライバシー重視の通知マスキングツール

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android CI](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml/badge.svg)](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml)

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

## テスト手順

1. APKをインストール
2. アプリを開く
3. 「Grant Notification Access」をタップ
4. 設定画面でKyuubiMaskを有効化
5. マスク対象アプリ（WhatsApp等）から通知を送信
6. 通知内容が「New notification」に置き換わることを確認

## プライバシー

- ❌ インターネット権限なし
- ❌ データ保存なし
- ❌ ログ記録なし
- ✅ 完全オフライン動作

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
