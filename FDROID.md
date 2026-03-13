# F-Droid Readiness

This document outlines KyuubiMask's readiness for F-Droid distribution.

## Compliance Status ✅

### License
- **License**: Apache 2.0
- **Status**: ✅ F-Droid compatible
- **File**: [LICENSE](LICENSE)

### Dependencies
- **Status**: ✅ All dependencies are FOSS
- **Details**: Uses only AndroidX and Material Components libraries
- **No proprietary SDKs**: No Google Play Services, Firebase, or other proprietary dependencies

### Privacy
- **Status**: ✅ Fully compliant
- **No internet permission**: App works completely offline
- **No tracking**: No analytics, ads, or data collection
- **Documentation**: [PRIVACY.md](PRIVACY.md)

### Build Configuration
- **Status**: ✅ Reproducible builds supported
- **Build tool**: Gradle with Kotlin DSL
- **Signing**: Release signing is applied only when environment variables are set; F-Droid will build and sign without them
- **Configuration**: [app/build.gradle.kts](app/build.gradle.kts)

### Metadata
- **Status**: ✅ Complete and up-to-date (v1.6.0 / versionCode 10)
- **Location**: [metadata/com.rtneg.kyuubimask.yml](metadata/com.rtneg.kyuubimask.yml)
- **Fastlane**: [fastlane/metadata/android/](fastlane/metadata/android/)

### Source Code
- **Status**: ✅ Publicly available
- **Repository**: https://github.com/soraiyu/KyuubiMask
- **Issue Tracker**: https://github.com/soraiyu/KyuubiMask/issues

### Git Tag
- **Status**: ✅ `v1.6.0` tag exists and points to the correct commit on `main`
- F-Droid builds from `commit: v1.6.0` as specified in the metadata YAML

## Potential Anti-Features

- **NonFreeNet**: Previously, KyuubiMask was hardcoded to work only with Slack, Discord, WhatsApp, and LINE. Since v1.5.0, users can select **any installed app** as a masking target via the "Add custom apps…" button. KyuubiMask is now a general-purpose notification masker and the `NonFreeNet` anti-feature no longer applies. The built-in presets for Slack, Discord, WhatsApp, and LINE are offered purely as convenience defaults; they impose no dependency on those services.

## F-Droid Submission Checklist

### ✅ All required items are complete

- [x] Apache 2.0 license
- [x] No proprietary dependencies
- [x] No tracking or analytics
- [x] No internet permission
- [x] Source code publicly available on GitHub
- [x] `v1.6.0` git tag exists on `main`
- [x] Build metadata file (`metadata/com.rtneg.kyuubimask.yml`) — version numbers and build instructions correct
- [x] Fastlane metadata structure prepared (`fastlane/metadata/android/`)
- [x] English default strings in `values/strings.xml`
- [x] Fastlane changelogs added for v1.5.0 (9.txt) and v1.6.0 (10.txt) in `fastlane/metadata/android/<locale>/changelogs/`
- [x] Feature graphic — `fastlane/metadata/android/en-US/images/featureGraphic.png` (valid PNG, 1024×500 px)
- [x] All store assets are on the `main` branch (Fastlane display assets are read from the default branch)

### 🔲 Optional enhancements (not required for submission)

- [ ] **Phone screenshots for F-Droid** — place PNG/JPEG screenshots in `fastlane/metadata/android/en-US/images/phoneScreenshots/` (recommended: 1080×1920 px). Japanese screenshots go in `fastlane/metadata/android/ja-JP/images/phoneScreenshots/`. These appear on the F-Droid app page.
- [ ] **Screenshots for README.md** — place images in `docs/screenshots/` (or any directory) and reference them with relative Markdown paths. Shown on the GitHub repository page only.

### 🔲 Action required: Submit to F-Droid

The only remaining step is to submit the app to F-Droid. See instructions below.

## How to Submit to F-Droid

> **This is a manual step you need to perform outside this repository.**

1. Fork the [F-Droid Data repository](https://gitlab.com/fdroid/fdroiddata) on GitLab.
2. In your fork, create a new file at `metadata/com.rtneg.kyuubimask.yml`.
3. Copy the exact contents of [`metadata/com.rtneg.kyuubimask.yml`](metadata/com.rtneg.kyuubimask.yml) from this repository into it.
4. Commit and submit a **merge request** to the F-Droid Data repository.
5. Use the merge request description below as the body of your MR.
6. F-Droid maintainers will review, build the APK from tag `v1.6.0`, and publish it.

> **Tip**: The review process typically takes a few weeks. F-Droid will comment on the MR if anything needs to be changed.

## F-Droid Merge Request Template

> **日本語の解説は下の「各チェック項目の解説」セクションを参照してください。**
>
> This is the merge request description to paste when opening your MR in the [F-Droid Data repository](https://gitlab.com/fdroid/fdroiddata). Copy everything from `## Required` through `/label ~"New App"` and paste it into the MR description field.

---

```
## Required

* [x] The app complies with the [inclusion criteria](https://f-droid.org/docs/Inclusion_Policy)
* [x] The original app author has been notified (and does not oppose the inclusion)
  — I am the app author (soraiyu) and am submitting this myself.
* [ ] All related [fdroiddata](https://gitlab.com/fdroid/fdroiddata/issues) and [RFP issues](https://gitlab.com/fdroid/rfp/issues) have been referenced in this merge request
  — No RFP issue has been filed yet. Will open one and update this MR.
* [x] Builds with `fdroid build` and all pipelines pass

## Strongly Recommended

* [x] The upstream app source code repo contains the app metadata _(summary/description/images/changelog/etc)_ in a [Fastlane](https://gitlab.com/snippets/1895688) or [Triple-T](https://gitlab.com/snippets/1901490) folder structure
  — Fastlane metadata at `fastlane/metadata/android/` (en-US, ja-JP) with title, descriptions, changelogs, and feature graphic.
* [x] Releases are tagged
  — Tags are auto-created from `versionName` via GitHub Actions. Current tag: `v1.6.0`.

## Suggested

* [ ] External repos are added as git submodules instead of srclibs
  — No external repos; not applicable.
* [ ] Enable [Reproducible Builds](https://f-droid.org/docs/Reproducible_Builds)
  No, I don't want this.
* [ ] Multiple apks for native code
  — No native code; not applicable.

---------------------

/label ~"New App"
```

---

## 各チェック項目の解説

> MRテンプレートの各チェック項目について、日本語で解説します。

### Required（必須項目）

**✅ The app complies with the inclusion criteria（収録基準への準拠）**

KyuubiMask は F-Droid の[収録基準](https://f-droid.org/docs/Inclusion_Policy/)を満たしています。
- Apache 2.0 ライセンス（FOSS）で公開
- インターネット権限なし（完全オフライン動作）
- プロプライエタリ SDK・ライブラリの不使用（AndroidX のみ）
- トラッキング・広告・データ収集なし

**✅ The original app author has been notified（アプリ作者への通知）**

このマージリクエストはアプリの作者（soraiyu）が自ら提出します。第三者が提出する場合は作者の同意（GitHub イシューやメールのリンク）を貼り付ける必要がありますが、自己提出なので不要です。

**❌ All related issues have been referenced（関連イシューの参照）**

RFP（Request For Packaging）イシューをまだ作成していないため、現時点ではチェックできません。提出前に [F-Droid RFP](https://gitlab.com/fdroid/rfp/issues) でイシューを作成し、MR に `Closes rfp#<番号>` と記載することを推奨します。イシューを開いていない場合でもMRを提出できますが、イシューがあった方がスムーズです。

**✅ Builds with `fdroid build` and all pipelines pass（fdroid ビルドの成功）**

`metadata/com.rtneg.kyuubimask.yml` のビルド設定（`commit: v1.6.0`, `subdir: app`, `gradle: [yes]`）が正しく記述されており、F-Droid の CI でビルドが通ります。署名設定は環境変数が存在しない場合は適用されないため、F-Droid のビルド環境でも問題ありません。

### Strongly Recommended（強く推奨）

**✅ Fastlane folder structure（Fastlane フォルダ構造）**

`fastlane/metadata/android/` に以下のアセットが揃っています。F-Droid はこのフォルダから説明文・変更ログ・画像を自動取得します。

- `en-US/title.txt` — 英語タイトル
- `en-US/short_description.txt` — 英語の短い説明（80 文字以内）
- `en-US/full_description.txt` — 英語の詳細説明
- `en-US/changelogs/10.txt` — v1.6.0 の変更ログ
- `en-US/images/featureGraphic.png` — フィーチャーグラフィック（1024×500 px）
- `ja-JP/` — 日本語版の各ファイル

**✅ Releases are tagged（リリースタグ）**

GitHub Actions の自動タグワークフロー（`.github/workflows/auto-tag.yml`）が PR マージ時に `app/build.gradle.kts` の `versionName` から自動的に `v<versionName>` タグを作成します。F-Droid の `UpdateCheckMode: Tags` と `AutoUpdateMode: Version` により、新しいタグが作成されると F-Droid が自動的に最新バージョンを検出します。

### Suggested（推奨）

**External repos as git submodules（外部リポジトリのサブモジュール化）**

KyuubiMask は外部リポジトリへの依存がないため、該当しません。

**Reproducible Builds（再現可能ビルド）**

現時点では有効にしていません（`No, I don't want this.` と明記）。F-Droid は F-Droid 自身の鍵で APK に署名します。注意：一度 F-Droid 鍵で公開した後は、再現可能ビルドを有効にして独自署名に切り替えることができません。将来的に再現可能ビルドを使いたい場合は、**最初から**有効にする必要があります。

**Multiple APKs for native code（ネイティブコード用複数 APK）**

KyuubiMask はネイティブコード（C/C++）を使用していないため、該当しません。

## Additional Resources

- [F-Droid Inclusion Guide](https://f-droid.org/docs/Inclusion_Policy/)
- [F-Droid Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/)
- [F-Droid Anti-Features](https://f-droid.org/docs/Anti-Features/)

