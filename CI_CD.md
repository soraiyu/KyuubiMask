# CI/CD 自動ビルドガイド 🔄

KyuubiMask には GitHub Actions による自動ビルドシステムが組み込まれています。

## 概要

コードをpushすると、自動的にAPKがビルドされます。手動でビルドする必要はありません！

## ワークフロー

### 1. Android CI (android-ci.yml)

**トリガー条件**:
- `main`, `master`, `develop`, `copilot/**` ブランチへのpush
- `main`, `master`, `develop` ブランチへのPull Request

**実行内容**:
1. ✅ コードのチェックアウト
2. ☕ JDK 17 のセットアップ
3. 🧪 テストの実行 (`./gradlew test`)
4. 🔨 Debug APK のビルド (`./gradlew assembleDebug`)
5. 📦 APK を Artifacts にアップロード (30日間保存)

**成果物**:
- `app-debug.apk` (GitHub Actions の Artifacts から取得可能)

### 2. Auto Tag on PR Merge (auto-tag.yml)

**トリガー条件**:
- `main` または `master` ブランチへのPull Requestがマージされた時

**実行内容**:
1. ✅ コードのチェックアウト
2. 📝 `app/build.gradle.kts` から `versionName` を抽出
3. 🔍 同じタグが既に存在するか確認
4. 🏷️ タグが存在しない場合、新しいバージョンタグを作成してpush
5. 🚀 タグがpushされると、自動的に Release Build ワークフローが起動

**メリット**:
- PRマージ時に自動でバージョンタグが作成される
- `build.gradle.kts` のバージョンを更新するだけでリリース可能
- 手動でタグを作成する必要がない

### 3. Release Build (release.yml)

**トリガー条件**:
- `v*` 形式のタグを作成した時 (例: `v1.0.1`, `v2.0.0`)
- Auto Tag ワークフローによって自動作成されたタグ
- 手動実行 (GitHub Actions の UI から)

**実行内容**:
1. ✅ コードのチェックアウト
2. ☕ JDK 17 のセットアップ
3. 🧪 テストの実行 (`./gradlew test`)
4. 🔨 Release APK のビルド (`./gradlew assembleRelease`)
5. ✔️ APK が正しくビルドされたか確認
6. 📦 APK を Artifacts にアップロード (90日間保存)
7. 🎉 GitHub Release の作成（タグの場合のみ）

**成果物**:
- `app-release-unsigned.apk` (署名なし)
- GitHub Release ページからダウンロード可能

## 使い方

### ビルドされたAPKを取得する

**📥 詳細なダウンロード手順は [DOWNLOAD_APK.md](DOWNLOAD_APK.md) をご覧ください！**

画像付きの完全ガイドで、初めての方でも迷わずダウンロードできます。

#### 方法1: GitHub Actions の Artifacts から

1. [GitHub Actions ページ](https://github.com/soraiyu/KyuubiMask/actions) にアクセス
2. 最新の成功したワークフローをクリック
3. 下部の「Artifacts」セクションから `app-debug` をダウンロード
4. ZIPファイルを解凍して `app-debug.apk` を取得

**困ったら**: [DOWNLOAD_APK.md の詳細ガイド](DOWNLOAD_APK.md#方法1-github-actions-artifacts-からダウンロード)

#### 方法2: Release ページから（タグビルドの場合）

1. [Releases ページ](https://github.com/soraiyu/KyuubiMask/releases) にアクセス
2. 最新のリリースをクリック
3. Assets から `app-release-unsigned.apk` をダウンロード

**困ったら**: [DOWNLOAD_APK.md の詳細ガイド](DOWNLOAD_APK.md#方法2-github-releases-からダウンロード)

### ビルドステータスの確認

README.md のビルドバッジをクリックすると、最新のビルド状況を確認できます：

[![Android CI](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml/badge.svg)](https://github.com/soraiyu/KyuubiMask/actions/workflows/android-ci.yml)

- ✅ 緑: ビルド成功
- ❌ 赤: ビルド失敗
- 🟡 黄: ビルド実行中

### リリースビルドの作成

新しいバージョンをリリースする場合、2つの方法があります：

#### 方法1: 自動リリース（推奨）

1. `app/build.gradle.kts` で `versionName` を更新：
   ```kotlin
   versionName = "1.0.1"  // 新しいバージョン番号に変更
   ```

2. 変更をコミットしてPRを作成：
   ```bash
   git add app/build.gradle.kts
   git commit -m "Bump version to 1.0.1"
   git push origin feature-branch
   ```

3. PRを `main` ブランチにマージ

これにより、自動的に：
- ✅ バージョンタグ（例: `v1.0.1`）が作成される
- ✅ Release APK がビルドされる
- ✅ GitHub Release が作成される
- ✅ APK がリリースページに添付される

**注意**: 同じバージョンのタグが既に存在する場合、新しいタグは作成されません。

#### 方法2: 手動タグ作成

従来の方法でも引き続きリリース可能です：

```bash
# 1. バージョンタグを作成
git tag v1.0.1

# 2. タグをpush
git push origin v1.0.1
```

これにより、自動的に：
- Release APK がビルドされる
- GitHub Release が作成される
- APK がリリースページに添付される

## Debug APK の署名キー設定（CI と ローカルを一致させる）

GitHub Actions でビルドされた APK をローカルにインストール済みのアプリに上書きインストール（`adb install -r`）しようとすると、署名が違うために `INSTALL_FAILED_UPDATE_INCOMPATIBLE` が発生することがあります。
これを防ぐために、ローカルの `debug.keystore` を CI に共有します。

### 手順

**1. ローカルの debug.keystore を Base64 エンコード**

```bash
# macOS: base64 ~/.android/debug.keystore\nbase64 -w 0 ~/.android/debug.keystore > /tmp/debug_keystore_base64.txt
```

**2. GitHub リポジトリのシークレットに追加**

1. GitHub リポジトリの **Settings > Secrets and variables > Actions** を開く
2. **New repository secret** をクリック
3. 名前: `ANDROID_DEBUG_KEYSTORE_BASE64`
4. 値: `/tmp/debug_keystore_base64.txt` の内容を貼り付け
5. **Add secret** をクリック

> **セキュリティ**: キーストアファイル自体はリポジトリにコミットしないでください。シークレット経由でのみ渡してください。

**3. 動作確認**

次の Actions 実行後、ワークフローログで以下を確認できます:
- `Verify debug keystore (SHA-256)` ステップ: キーストアの SHA-256 フィンガープリント
- `Verify APK signing certificate` ステップ: APK に使われた証明書の SHA-256

両ステップの `SHA-256` が一致していれば、CI の APK はローカルと同じ署名になっています。

---

## トラブルシューティング

### ビルドが失敗する場合

1. **ログを確認**:
   - Actions ページでワークフローをクリック
   - 失敗したステップのログを確認

2. **一般的な問題**:
   - Gradle バージョンの不一致
   - 依存関係の問題
   - テストの失敗

3. **解決方法**:
   ```bash
   # ローカルで同じコマンドを実行して確認
   ./gradlew test
   ./gradlew assembleDebug
   ```

### Artifacts が見つからない

- ビルドが成功していることを確認
- ワークフローが完全に終了するまで待つ（通常5-10分）
- ブランチが正しいことを確認

### Release が作成されない

- タグが `v` で始まっているか確認（例: `v1.0.0`）
- タグを正しくpushしたか確認
- GitHub Token の権限を確認（通常は自動）

### 自動タグが作成されない

- PRが `main` または `master` ブランチにマージされたか確認
- `app/build.gradle.kts` の `versionName` が正しく設定されているか確認
- 同じバージョンのタグが既に存在していないか確認（`git tag -l` で確認）
- GitHub Actions のログを確認して、エラーメッセージを確認

## ワークフローのカスタマイズ

### ビルド対象ブランチの変更

`.github/workflows/android-ci.yml` を編集：

```yaml
on:
  push:
    branches: [ main, your-branch-name ]  # ここを変更
```

### APK保存期間の変更

```yaml
- name: Upload Debug APK
  uses: actions/upload-artifact@v4
  with:
    retention-days: 30  # ここを変更（1-90日）
```

### テスト失敗時の挙動

デフォルトでは、テストが失敗してもビルドは続行されます：

```yaml
- name: Run tests
  run: ./gradlew test --stacktrace
  continue-on-error: true  # これを false にすると、テスト失敗時にビルドも失敗
```

## CI/CD のメリット

✅ **自動化**: 手動ビルド不要  
✅ **品質保証**: 常にクリーンな環境でビルド  
✅ **履歴管理**: すべてのビルドが記録される  
✅ **配布簡単**: Artifacts から直接ダウンロード  
✅ **テスト実行**: 自動的にテストが実行される  

## 次のステップ

1. コードをpushして自動ビルドを試す
2. Artifacts からAPKをダウンロード
3. 実機でテスト
4. 問題なければタグを作成してリリース

---

**Happy Building! 🦊**
