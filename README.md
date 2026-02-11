# KyuubiMask 🦊

プライバシー重視の通知マスキングツール

## ビルド手順

```bash
cd KyuubiMask

# Debug APK
./gradlew assembleDebug

# Release APK (署名が必要)
./gradlew assembleRelease
```

APKは `app/build/outputs/apk/debug/app-debug.apk` に生成されます。

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
