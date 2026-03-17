# Changelog

All notable changes to KyuubiMask will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.7.4] - 2026-03-17

## [1.7.3] - 2026-03-17

## [1.7.2] - 2026-03-16

## [1.7.1] - 2026-03-16

## [1.7.1] - 2026-03-16

## [1.7.1] - 2026-03-16

### Added
- Per-app language selection: Language card in Settings lets users switch the app language independently of the system locale (English / 日本語 / 中文（简体）/ 中文（繁體）/ System default). Uses `AppCompatDelegate.setApplicationLocales()` for compatibility with all Android versions and syncs with Android 13+'s native per-app language preference.

## [1.7.0] - 2026-03-14

### Added
- Replaced the first-launch privacy dialog with a 5-page swipe onboarding experience that covers the app's functionality, privacy features, and setup
- Debug builds now append a `.debug.v{versionName}` suffix to `applicationId`, allowing debug and release variants to coexist on the same device

### Changed
- Settings screen redesigned: status and enable switch merged into one horizontal card featuring a fox 🦊 stamp; app preset toggles rearranged into a 2×2 tile grid; "Grant Notification Access" button moved above the settings cards

## [1.6.1] - 2026-03-10

### Fixed
- Switch toggles no longer remain visually active (colored) when unchecked; they now correctly gray out to reflect the disabled state

## [1.6.0] - 2026-03-07

### Added
- F-Droid distribution: app metadata, feature graphic, and changelogs added for F-Droid submission
- App page assets: feature graphic for en-US, short/full descriptions for en-US and ja-JP locales

## [1.5.0] - 2026-03-03

### Added
- User-selectable app masking: any installed app can now be chosen as a masking target via "Add custom apps…" in Settings
- `SelectAppsActivity` for browsing and selecting installed apps with a searchable list
- `GenericMaskStrategy` for masking notifications from user-selected apps
- User-selected packages persisted in `PreferencesRepository`

## [1.4.1] - 2026-03-02

### Fixed
- Default English strings (`values/strings.xml`) for Quick Settings tile and main toggle screen were displaying Japanese text on non-Japanese devices

## [1.4.0] - 2026-03-02

### Added
- Japanese localization (`values-ja/strings.xml`) with full translation of all UI strings
- First-launch privacy dialog shown on initial app startup

## [1.3.0] - 2026-03-01

### Added
- Masking ON/OFF toggle via Quick Settings Tile
- Compose UI for masking toggle on the main screen
- Vibration pattern selection for masked notifications (short, double, heart, long)
- Automated APK release signing via GitHub Actions

## [1.2.2] - 2026-02-27

### Fixed
- Release workflow permissions

## [1.2.1] - 2026-02-27

### Added
- Signed release workflow setup
- App version display in settings screen
- Improved release workflow with APK renaming
- CI: consistent debug keystore for reproducible builds

## [1.2.0] - 2026-02-23

### Added
- Notification masking support for **Discord** (`com.discord`)
- Notification masking support for **WhatsApp** (`com.whatsapp`)
- Notification masking support for **LINE** (`jp.naver.line.android`)
- Per-app toggle UI in Settings for Discord, WhatsApp, and LINE

## [1.1.0] - 2026-02-22

### Added
- Test infrastructure with JUnit and AndroidX Test
- PreferencesRepository for centralized settings management
- LICENSE file (Apache 2.0) for open source
- CONTRIBUTING.md with contribution guidelines
- Debug logging now gated with BuildConfig.DEBUG
- BUILD_AND_TEST.md with comprehensive build and testing guide
- QUICKSTART.md for quick device testing
- **GitHub Actions CI/CD workflows**
  - Automatic APK build on push to main branches
  - Automatic APK build on pull requests
  - **Automatic version tagging on PR merge to main**
  - Release build workflow with artifact upload and GitHub Release creation
  - Build status badge in README

### Changed
- Refactored SharedPreferences access to use PreferencesRepository
- Improved notification ID generation to prevent collisions
- Centralized magic numbers as constants

### Fixed
- Potential infinite loop in notification masking

## [1.0.0] - 2026-02-13

### Added
- Initial release
- Notification masking service for WhatsApp, Telegram, Gmail, and LINE
- Privacy-first design: no internet access, no data storage
- Simple settings UI with Material Design 3
- Real-time debug log viewer
- Complete offline operation

[Unreleased]: https://github.com/soraiyu/KyuubiMask/compare/v1.7.4...HEAD
[1.7.4]: https://github.com/soraiyu/KyuubiMask/compare/v1.7.3...v1.7.4
[1.7.3]: https://github.com/soraiyu/KyuubiMask/compare/v1.7.2...v1.7.3
[1.7.2]: https://github.com/soraiyu/KyuubiMask/compare/v1.7.1...v1.7.2
[1.7.1]: https://github.com/soraiyu/KyuubiMask/compare/v1.7.0...v1.7.1
[1.7.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.6.1...v1.7.0
[1.6.1]: https://github.com/soraiyu/KyuubiMask/compare/v1.6.0...v1.6.1
[1.6.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.5.0...v1.6.0
[1.5.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.4.1...v1.5.0
[1.4.1]: https://github.com/soraiyu/KyuubiMask/compare/v1.4.0...v1.4.1
[1.4.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.2.2...v1.3.0
[1.2.2]: https://github.com/soraiyu/KyuubiMask/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/soraiyu/KyuubiMask/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/soraiyu/KyuubiMask/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/soraiyu/KyuubiMask/releases/tag/v1.0.0
