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

### 🔲 Optional enhancements (not required for submission)

- [ ] **Phone screenshots for F-Droid** — place PNG/JPEG screenshots in `fastlane/metadata/android/en-US/images/phoneScreenshots/` (recommended: 1080×1920 px). Japanese screenshots go in `fastlane/metadata/android/ja-JP/images/phoneScreenshots/`. These appear on the F-Droid app page.
- [ ] **Screenshots for README.md** — place images in `docs/screenshots/` (or any directory) and reference them with relative Markdown paths. Shown on the GitHub repository page only.

### 🔲 Action required by you (outside this repository)

- [ ] **Merge this PR** into `main` so `featureGraphic.png` is available on the default branch (Fastlane display assets are read from the default branch)
- [ ] **Submit to F-Droid** — see instructions below

## How to Submit to F-Droid

> **This is a manual step you need to perform outside this repository.**

1. Fork the [F-Droid Data repository](https://gitlab.com/fdroid/fdroiddata) on GitLab.
2. In your fork, create a new file at `metadata/com.rtneg.kyuubimask.yml`.
3. Copy the exact contents of [`metadata/com.rtneg.kyuubimask.yml`](metadata/com.rtneg.kyuubimask.yml) from this repository into it.
4. Commit and submit a **merge request** to the F-Droid Data repository.
5. F-Droid maintainers will review, build the APK from tag `v1.6.0`, and publish it.

> **Tip**: The review process typically takes a few weeks. F-Droid will comment on the MR if anything needs to be changed.

## Additional Resources

- [F-Droid Inclusion Guide](https://f-droid.org/docs/Inclusion_Policy/)
- [F-Droid Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/)
- [F-Droid Anti-Features](https://f-droid.org/docs/Anti-Features/)

