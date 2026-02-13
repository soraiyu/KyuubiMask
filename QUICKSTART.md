# 🚀 クイックスタート: 実機テスト

## 最速で実機テストを始める

### オプション: ビルド済みAPKをダウンロード

自分でビルドせずに、すぐに試したい場合：

📥 **[DOWNLOAD_APK.md](DOWNLOAD_APK.md)** - GitHub Actionsからビルド済みAPKをダウンロード

ダウンロードしたAPKは、ステップ2から始めてください。

---

### ステップ1: ビルド (5分)

```bash
# プロジェクトディレクトリに移動
cd KyuubiMask

# Debug APK をビルド
./gradlew assembleDebug
```

✅ 成功すると: `app/build/outputs/apk/debug/app-debug.apk` が生成されます

### ステップ2: インストール (2分)

**方法A: USB接続でインストール（推奨）**

```bash
# デバイスが認識されているか確認
adb devices

# インストール
adb install app/build/outputs/apk/debug/app-debug.apk
```

**方法B: APKを直接転送**
1. `app-debug.apk` をスマホに送る（メール、クラウドなど）
2. スマホで APK をタップしてインストール

### ステップ3: 初期設定 (3分)

1. **アプリを開く**
   - ホーム画面の KyuubiMask アイコンをタップ

2. **通知アクセス権限を付与**
   - "Grant Notification Access" ボタンをタップ
   - 設定画面で KyuubiMask をオンにする
   - 警告が表示されたら「許可」

3. **ステータス確認**
   - アプリに戻る
   - "🦊 Active & Protecting" と表示されていれば OK！

### ステップ4: テスト (5分)

#### 基本テスト

1. **WhatsApp で通知を受信**
   - 友達にメッセージを送ってもらう
   - 通知バーを確認
   - ✅ 期待: "WhatsApp" / "New notification" と表示

2. **マスキングをオフにする**
   - KyuubiMask でスイッチをオフ
   - もう一度メッセージを受信
   - ✅ 期待: メッセージ内容が表示される

3. **マスキングを再度オン**
   - スイッチをオン
   - ✅ 期待: また "New notification" と表示される

## 完了！ 🎉

基本的な動作が確認できたら、以下もお試しください：

### 追加テスト

- [ ] Telegram のマスキング
- [ ] Gmail のマスキング
- [ ] LINE のマスキング
- [ ] 複数アプリの同時マスキング
- [ ] アプリ個別の有効/無効切り替え

### パフォーマンス確認

- [ ] バッテリー消費（設定 > バッテリーで確認）
- [ ] アプリの応答性
- [ ] 通知の遅延がないか

## トラブルシューティング

### ビルドが失敗する

```bash
# クリーンビルド
./gradlew clean assembleDebug
```

### 通知がマスクされない

1. 通知アクセス権限を確認
2. アプリを再起動
3. デバイスを再起動

### もっと詳しく知りたい

👉 [BUILD_AND_TEST.md](BUILD_AND_TEST.md) に詳細なガイドがあります

## フィードバック

問題や改善提案は [GitHub Issues](https://github.com/soraiyu/KyuubiMask/issues) へ！

---

**所要時間**: 約15分で実機テスト完了 ⚡
