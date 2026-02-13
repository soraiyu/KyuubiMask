# Changelog

All notable changes to KyuubiMask will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Test infrastructure with JUnit and AndroidX Test
- PreferencesRepository for centralized settings management
- LICENSE file (Apache 2.0) for open source
- CONTRIBUTING.md with contribution guidelines
- Debug logging now gated with BuildConfig.DEBUG

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

[Unreleased]: https://github.com/soraiyu/KyuubiMask/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/soraiyu/KyuubiMask/releases/tag/v1.0.0
