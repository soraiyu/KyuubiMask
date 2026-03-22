# 🔧 Android App Template Guide

このリポジトリは **汎用的な Android アプリ開発テンプレート** として利用できます。  
GitHub でフォークするだけで、以下の仕組みがすぐに使えます。

> **TL;DR**: フォーク後、`scripts/setup_new_app.sh` を実行してアプリ ID・名前を置換し、GitHub Secrets を設定すれば完成です。

---

## 目次

1. [テンプレートとして使えるコンポーネント一覧](#1-コンポーネント一覧)
2. [CI — ビルド & テスト (android-ci.yml)](#2-ci--ビルド--テスト)
3. [Release — リリースビルド (release.yml)](#3-release--リリースビルド)
4. [Auto Versioning — 自動バージョン管理 (auto-tag.yml)](#4-auto-versioning--自動バージョン管理)
5. [Auto Label — PR 自動ラベル (auto-label.yml)](#5-auto-label--pr-自動ラベル)
6. [Security Check — セキュリティ検証 (security-check.yml)](#6-security-check--セキュリティ検証)
7. [F-Droid 対応 (update-fdroid.yml)](#7-f-droid-対応)
8. [フォーク後の設定手順](#8-フォーク後の設定手順)
9. [必要な GitHub Secrets 一覧](#9-必要な-github-secrets-一覧)
10. [各ファイルのカスタマイズ箇所](#10-各ファイルのカスタマイズ箇所)
11. [実現可否・注意事項](#11-実現可否注意事項)

---

## 1. コンポーネント一覧

| コンポーネント | ファイル | 汎用性 | 説明 |
|---|---|---|---|
| CI ビルド & テスト | `.github/workflows/android-ci.yml` | ⭐⭐⭐ 高 | push/PR で Debug APK ビルド＋ユニットテスト |
| リリースビルド | `.github/workflows/release.yml` | ⭐⭐⭐ 高 | タグ push で署名済みリリース APK ＋ GitHub Release 作成 |
| 自動バージョン管理 | `.github/workflows/auto-tag.yml` | ⭐⭐⭐ 高 | PR マージ時に自動バージョンアップ＋タグ作成（2フェーズ） |
| PR 自動ラベル | `.github/workflows/auto-label.yml` | ⭐⭐⭐ 高 | アプリ変更のない PR に `skip-release` ラベルを自動付与 |
| セキュリティ検証 | `.github/workflows/security-check.yml` | ⭐⭐ 中 | 危険なパーミッション・ハードコードシークレットを検出 |
| F-Droid メタデータ | `.github/workflows/update-fdroid.yml` | ⭐ 低 | F-Droid 公開アプリ専用（不要なら削除可） |
| ビルド設定 | `app/build.gradle.kts` | ⭐⭐⭐ 高 | 環境変数からの署名設定、ProGuard、Compose 対応 |
| CHANGELOG | `CHANGELOG.md` | ⭐⭐⭐ 高 | Keep a Changelog 形式の自動更新テンプレート |

---

## 2. CI — ビルド & テスト

**ファイル**: `.github/workflows/android-ci.yml`

### 何をするか

- `main` / `master` / `develop` ブランチへの push または PR 時に自動実行
- ユニットテストを実行してレポートをアーティファクトとして保存
- Debug APK をビルドしてアーティファクトとして 30 日間保存
- オプション: カスタム `debug.keystore` を Secret から適用し、署名の一貫性を確保

### なぜ有用か

- **フィードバックの高速化**: PR を出した時点でビルドが通るか即座に確認できる
- **テスト自動化**: ローカルでのテスト忘れを防ぐ
- **APK 共有**: ビルド済み APK を GitHub Actions からダウンロードしてチームで共有できる

### カスタマイズが必要な箇所

```yaml
# トリガーするブランチを変更する場合
on:
  push:
    branches: [ main, master, develop ]  # ← 必要なブランチに変更
```

> ⚠️ **注意**: `actions/checkout@v6` など Actions のバージョンは定期的に更新してください。

### オプション: Debug keystore の統一

CI とローカルで同じ `debug.keystore` を使うと、APK の上書きインストール時に署名不一致エラーが防げます。

1. ローカルの `~/.android/debug.keystore` を Base64 エンコード:
   ```bash
   base64 -i ~/.android/debug.keystore | tr -d '\n'
   ```
2. GitHub リポジトリの **Settings → Secrets and variables → Actions** に追加:
   - Secret 名: `ANDROID_DEBUG_KEYSTORE_BASE64`
   - 値: 上記の Base64 文字列

---

## 3. Release — リリースビルド

**ファイル**: `.github/workflows/release.yml`

### 何をするか

- `v*` タグ push（または手動 `workflow_dispatch`）で起動
- ユニットテストを実行
- 環境変数から読み込んだ署名情報で **リリース APK** をビルド
- GitHub Release を自動作成し、署名済み APK を添付

### なぜ有用か

- **署名キーをコードに含めない**: キーストアは Secret に保存、ワークフロー内でデコード＆利用後に即削除
- **再現性**: 同じタグから何度でも同じ APK を生成できる
- **手動トリガー対応**: タグを打たずにリリースビルドを試したい場合に `workflow_dispatch` が使える

### カスタマイズが必要な箇所

Release の説明文（`body:` セクション）をアプリに合わせて書き換えてください:

```yaml
    - name: Create Release
      uses: softprops/action-gh-release@v2
      with:
        body: |
          ## YourApp ${{ steps.get_version.outputs.VERSION }}
          
          アプリの説明文をここに書きます。
          
          See [CHANGELOG.md](CHANGELOG.md) for details.
```

---

## 4. Auto Versioning — 自動バージョン管理

**ファイル**: `.github/workflows/auto-tag.yml`

### 何をするか（2フェーズ方式）

```
PR マージ (main/master)
  └─ Phase 1: prepare-release
      ├─ app/build.gradle.kts の versionName/versionCode を自動バンプ
      ├─ CHANGELOG.md の [Unreleased] を [X.Y.Z] に昇格
      ├─ fastlane changelogs を生成
      └─ release/vX.Y.Z ブランチ＆PR を作成

release PR マージ
  └─ Phase 2: create-tag
      └─ vX.Y.Z タグをリモートに push → release.yml がトリガー
```

### なぜ有用か

- **手動バージョン管理が不要**: コードを書くことに集中できる
- **CHANGELOG の自動管理**: `[Unreleased]` セクションに書いた内容が自動的にバージョン付きセクションに昇格
- **バージョン競合を防ぐ**: 複数 PR が同時にマージされても、自動バンプで重複タグを回避
- **レビューポイント確保**: リリース PR を人間がマージするので、予期しないリリースを防げる

### ラベルの仕組み

| ラベル | 効果 |
|---|---|
| `skip-release` | auto-tag の Phase 1 をスキップ（ドキュメントのみの PR など） |
| `release` | Phase 2 のトリガー条件（自動付与される） |
| `fdroid` | F-Droid PR（Phase 1・2 どちらもスキップ） |

### カスタマイズが必要な箇所

ほぼカスタマイズ不要ですが、**fastlane** を使わない場合は以下のステップを削除:

```yaml
    - name: Create fastlane changelogs   # ← 不要なら削除
```

また、CHANGELOG.md の参照先リポジトリは `${{ github.repository }}` で自動解決されるため、変更不要です。

### 必要な GitHub 設定

- **Settings → Actions → General → Workflow permissions**: "Read and write permissions" を有効化
- **Settings → Actions → General**: "Allow GitHub Actions to create and approve pull requests" を有効化

---

## 5. Auto Label — PR 自動ラベル

**ファイル**: `.github/workflows/auto-label.yml`

### 何をするか

- PR のファイル差分を確認
- アプリに影響する変更（`app/`, `build.gradle.kts`, `gradlew` 等）があれば `skip-release` ラベルを**削除**
- ドキュメントのみの変更なら `skip-release` ラベルを**付与**

### なぜ有用か

- ドキュメント修正などリリース不要な PR で自動バージョンアップが走るのを防ぐ
- 「どの PR がリリースを引き起こすか」が一目でわかる

### カスタマイズが必要な箇所

アプリ変更とみなすパターンを調整:

```bash
# auto-label.yml 内の APP_PATTERN を編集
APP_PATTERN="^(app/|build\.gradle\.kts$|settings\.gradle\.kts$|gradle/|...)"
```

---

## 6. Security Check — セキュリティ検証

**ファイル**: `.github/workflows/security-check.yml`  
**スクリプト**: `scripts/check_security.py`

### 何をするか（6項目チェック）

| チェック項目 | 汎用性 | 説明 |
|---|---|---|
| ① INTERNET 権限なし | ⚠️ 要確認 | オフラインアプリ専用。ネット通信が必要なアプリは削除 |
| ② `allowBackup=false` | ⭐ 推奨 | データのバックアップを無効化（セキュリティ向上） |
| ③ ProGuard/R8 有効 | ⭐⭐⭐ 必須 | リリースビルドでコードを難読化・最適化 |
| ④ 危険なパーミッションなし | ⚠️ 要確認 | カメラ・位置情報等が必要なアプリは除外リストを修正 |
| ⑤ `Log.*` を `BuildConfig.DEBUG` でガード | ⭐⭐⭐ 必須 | リリースビルドにデバッグログを残さない |
| ⑥ ハードコードシークレットなし | ⭐⭐⭐ 必須 | API キー・パスワードのコード埋め込みを検出 |

### ほとんどのアプリで変更が必要な箇所

`scripts/check_security.py` を編集:

```python
# ① INTERNET 権限チェックを削除（ネット通信アプリの場合）
# 該当ブロックをコメントアウトまたは削除:
# ── 1. No INTERNET permission ────────────────────────────────────────────────

# ④ 許可するパーミッションを追加（例: カメラアプリ）
DANGEROUS_PERMISSIONS = [
    # "android.permission.CAMERA",  # ← カメラアプリなら削除
    "android.permission.ACCESS_FINE_LOCATION",
    ...
]
```

---

## 7. F-Droid 対応

**ファイル**: `.github/workflows/update-fdroid.yml`  
**メタデータ**: `metadata/<applicationId>.yml`

### 何をするか

- `v*` タグ push でタグのコミットハッシュを解決
- `metadata/<applicationId>.yml` に新しいビルドエントリを追加
- `fdroid/vX.Y.Z` ブランチ＆PR を作成

### F-Droid に公開しない場合

このファイルは **削除可能** です。F-Droid での公開を予定しない場合:

```bash
rm .github/workflows/update-fdroid.yml
rm -rf metadata/
```

### F-Droid に公開する場合の手順

1. `metadata/com.rtneg.kyuubimask.yml` をコピーしてアプリ ID でリネーム:
   ```bash
   cp metadata/com.rtneg.kyuubimask.yml metadata/<your.app.id>.yml
   ```
2. ファイル内の情報を更新（SourceCode URL、AuthorName 等）
3. `Builds:` セクションを空にして最初のビルドから始める

---

## 8. フォーク後の設定手順

### ステップ 1: リポジトリのフォーク

1. GitHub でこのリポジトリを **Fork**
2. ローカルにクローン:
   ```bash
   git clone https://github.com/<your-username>/<your-repo-name>.git
   cd <your-repo-name>
   ```

### ステップ 2: セットアップスクリプトの実行

```bash
chmod +x scripts/setup_new_app.sh
./scripts/setup_new_app.sh
```

スクリプトが以下を自動で行います:
- `applicationId` / `namespace` の置換
- アプリ名の置換
- バージョンのリセット（0.1.0 / 1）
- CHANGELOG.md のリセット
- 不要なファイルの案内

### ステップ 3: Kotlin ソースの整理

```bash
# 旧パッケージのディレクトリを新パッケージに移動（手動）
mkdir -p app/src/main/java/<your/package/path>
mv app/src/main/java/com/rtneg/kyuubimask/* app/src/main/java/<your/package/path>/

# 全 .kt ファイルの import 文を置換
find app/src -name "*.kt" -exec \
  sed -i 's/com\.rtneg\.kyuubimask/<your.package.name>/g' {} +
```

> 💡 **推奨**: Android Studio の **Refactor → Rename** でパッケージを一括リネームすると安全です。

### ステップ 4: GitHub Secrets の設定

[必要な GitHub Secrets 一覧](#9-必要な-github-secrets-一覧) を参照して設定してください。

### ステップ 5: GitHub リポジトリ設定

**Settings → Actions → General** で以下を有効化:
- [x] "Read and write permissions"
- [x] "Allow GitHub Actions to create and approve pull requests"

### ステップ 6: 動作確認

```bash
# ローカルでビルドが通ることを確認
./gradlew assembleDebug

# ユニットテストの実行
./gradlew testDebugUnitTest
```

---

## 9. 必要な GitHub Secrets 一覧

**Settings → Secrets and variables → Actions** で設定:

### リリースビルド用（必須）

| Secret 名 | 説明 | 取得方法 |
|---|---|---|
| `ANDROID_KEYSTORE_BASE64` | 署名キーストアを Base64 エンコードしたもの | `base64 -i release.jks \| tr -d '\n'` |
| `ANDROID_KEYSTORE_PASSWORD` | キーストアのパスワード | キーストア作成時に設定 |
| `ANDROID_KEY_ALIAS` | キーのエイリアス名 | キーストア作成時に設定 |
| `ANDROID_KEY_PASSWORD` | キーのパスワード | キーストア作成時に設定 |

**キーストアの作成（初回のみ）**:
```bash
keytool -genkeypair -v \
  -keystore my-release-key.jks \
  -alias my-key-alias \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### CI ビルド用（任意）

| Secret 名 | 説明 | 用途 |
|---|---|---|
| `ANDROID_DEBUG_KEYSTORE_BASE64` | デバッグ用キーストアの Base64 | CI とローカルの署名を統一する場合 |

---

## 10. 各ファイルのカスタマイズ箇所

### `app/build.gradle.kts`

```kotlin
android {
    namespace = "com.yourcompany.yourapp"       // ← アプリのパッケージ名に変更
    
    defaultConfig {
        applicationId = "com.yourcompany.yourapp" // ← 上と同じ値に変更
        minSdk = 26                               // ← 対応する最低 Android バージョン
        targetSdk = 35                            // ← 最新安定版を維持推奨
        versionCode = 1                           // ← 初期値は 1 のまま（自動管理）
        versionName = "0.1.0"                     // ← 初期バージョン
    }
    
    buildTypes {
        debug {
            // applicationIdSuffix は任意（デバッグ版とリリース版を共存させる場合）
            applicationIdSuffix = ".debug"
        }
    }
}
```

### `settings.gradle.kts`

```kotlin
rootProject.name = "YourAppName"  // ← プロジェクト名に変更
```

### `CHANGELOG.md`

`auto-tag.yml` が自動更新するため、初期値だけ設定してあとは自動管理:

```markdown
# Changelog

## [Unreleased]

<!-- ここに次のリリースの変更内容を書く -->

[Unreleased]: https://github.com/your-username/your-repo/compare/v0.1.0...HEAD
```

### `release.yml` の Release 本文

アプリの説明に合わせて書き換え:

```yaml
        body: |
          ## YourApp ${{ steps.get_version.outputs.VERSION }}

          あなたのアプリの説明を書きます。

          See [CHANGELOG.md](CHANGELOG.md) for details.
```

---

## 11. 実現可否・注意事項

### ✅ そのまま使えるもの

| 機能 | 理由 |
|---|---|
| CI ビルド＆テスト | `github.repository` など環境変数で自動解決。変更不要 |
| 自動バージョン管理 | `app/build.gradle.kts` の `versionName = "x.y.z"` 形式（semver）を守れば動作 |
| PR 自動ラベル | ファイルパターンの正規表現だけ調整すれば汎用的に動く |
| 署名設定パターン | Secret から環境変数経由でキーストアを受け取る設計は汎用的 |

### ⚠️ カスタマイズが必要なもの

| 機能 | 必要な変更 |
|---|---|
| セキュリティチェック | INTERNET 権限チェック、危険なパーミッションリストはアプリに合わせて調整 |
| リリース本文 | アプリ説明文を書き換え |
| F-Droid メタデータ | 不要なら削除、必要なら applicationId でリネーム |
| Kotlin ソース | パッケージ名変更は手動（Android Studio の Refactor 機能推奨） |

### ❌ このテンプレートの制約

| 制約 | 説明 |
|---|---|
| semver (x.y.z) 必須 | auto-tag.yml は `versionName` が x.y.z 形式でないと動作しない |
| `app/` モジュール名前固定 | ワークフローが `app/build.gradle.kts` を参照しているため、モジュール名を変更した場合は workflows も変更が必要 |
| GitHub Actions 依存 | GitLab CI 等では使えない（ただし概念は移植可能） |
| Branch Protection との組み合わせ | Branch Protection で "Require PR" を設定している場合、Actions から直接 main への push ができないため、ブランチ保護ルールで `github-actions[bot]` をバイパスリストに追加する必要がある |

### 💡 フォーク利用時の推奨フロー

```
1. フォーク
2. setup_new_app.sh でアプリ ID・名前を置換
3. Kotlin ソースを新しいパッケージに移動（Android Studio で Refactor）
4. GitHub Secrets を設定（署名キーストア）
5. GitHub Actions 設定（PR 作成権限）
6. main に push → CI が動作することを確認
7. CHANGELOG.md に最初の変更内容を書いて PR → 自動バージョン管理開始
```

---

**参考ドキュメント**:
- [CI_CD.md](CI_CD.md) — このリポジトリのワークフロー詳細
- [BUILD_AND_TEST.md](BUILD_AND_TEST.md) — ローカルビルド・テスト手順
- [CONTRIBUTING.md](CONTRIBUTING.md) — コントリビューションガイド
