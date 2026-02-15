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
- **Configuration**: [app/build.gradle.kts](app/build.gradle.kts)

### Metadata
- **Status**: ✅ Complete
- **Location**: [metadata/com.kyuubimask.yml](metadata/com.kyuubimask.yml)
- **Fastlane**: [fastlane/metadata/android/](fastlane/metadata/android/)

### Source Code
- **Status**: ✅ Publicly available
- **Repository**: https://github.com/soraiyu/KyuubiMask
- **Issue Tracker**: https://github.com/soraiyu/KyuubiMask/issues

## F-Droid Submission Checklist

- [x] Apache 2.0 license
- [x] No proprietary dependencies
- [x] No tracking or analytics
- [x] No internet permission
- [x] Source code publicly available
- [x] Metadata file created
- [x] Fastlane structure prepared
- [ ] Screenshots added (optional)
- [ ] Feature graphic added (optional)
- [ ] Submit to F-Droid repository

## How to Submit

1. Fork the [F-Droid Data repository](https://gitlab.com/fdroid/fdroiddata)
2. Add the metadata file from `metadata/com.kyuubimask.yml`
3. Submit a merge request
4. Wait for F-Droid team review

## Additional Resources

- [F-Droid Inclusion Guide](https://f-droid.org/docs/Inclusion_Policy/)
- [F-Droid Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/)
