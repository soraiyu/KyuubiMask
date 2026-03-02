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
- **Status**: ✅ Complete and up-to-date (v1.4.0 / versionCode 7)
- **Location**: [metadata/com.rtneg.kyuubimask.yml](metadata/com.rtneg.kyuubimask.yml)
- **Fastlane**: [fastlane/metadata/android/](fastlane/metadata/android/)

### Source Code
- **Status**: ✅ Publicly available
- **Repository**: https://github.com/soraiyu/KyuubiMask
- **Issue Tracker**: https://github.com/soraiyu/KyuubiMask/issues

## Potential Anti-Features

F-Droid reviewers may flag the following:

- **NonFreeNet**: The app is designed to work with Slack, Discord, WhatsApp, and LINE — all proprietary (non-free) network services. F-Droid may require an `AntiFeatures: NonFreeNet` declaration in the metadata, even though the app itself is fully free software. If requested, add the following to `metadata/com.rtneg.kyuubimask.yml`:
  ```yaml
  AntiFeatures:
    - NonFreeNet
  ```

## F-Droid Submission Checklist

- [x] Apache 2.0 license
- [x] No proprietary dependencies
- [x] No tracking or analytics
- [x] No internet permission
- [x] Source code publicly available
- [x] Metadata file created and version numbers correct
- [x] Fastlane structure prepared
- [x] English default strings in values/strings.xml
- [ ] **Screenshots for F-Droid** — place PNG/JPEG files in `fastlane/metadata/android/en-US/images/phoneScreenshots/` (and optionally `ja-JP/images/phoneScreenshots/` for Japanese). Recommended size: 1080×1920. This is what F-Droid displays on the app page.
- [ ] **Screenshots for README.md** — place images in `docs/screenshots/` (or any directory) and reference them from `README.md` with relative paths. These appear on the GitHub repository page only and are independent from F-Droid.
- [ ] Feature graphic — place as `fastlane/metadata/android/en-US/images/featureGraphic.png` (1024×500 px). Optional.
- [ ] Submit to F-Droid repository

## How to Submit

1. Fork the [F-Droid Data repository](https://gitlab.com/fdroid/fdroiddata).
2. In your fork, create a new file at `metadata/com.rtneg.kyuubimask.yml`.
3. Copy the contents of this project's `metadata/com.rtneg.kyuubimask.yml` file into the new file.
4. Submit a merge request with your changes to the F-Droid Data repository.

## Additional Resources

- [F-Droid Inclusion Guide](https://f-droid.org/docs/Inclusion_Policy/)
- [F-Droid Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/)
- [F-Droid Anti-Features](https://f-droid.org/docs/Anti-Features/)

