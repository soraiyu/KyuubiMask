# Copilot Coding Agent Instructions for KyuubiMask

## Project Overview
- This is a minimal, privacy-first Android notification masking app.
- Core purpose: Cancel sensitive notifications and replace them with generic masked ones.
- **Target: F-Droid and Google Play Store release**
- Never store, log, or transmit any notification content.

## Absolute Rules (Never Break These)

### Privacy & Security (Critical)
- **Privacy First**: Never save, log, cache, or send any part of the original notification content (title, text, bigText, extras, etc.).
- **No INTERNET permission**: No analytics, no Firebase, no Room, no network calls.
- **Debug builds**: DebugLogRepository or any logging that stores notification content is FORBIDDEN in release builds. Use `BuildConfig.DEBUG` guards, but ensure NO user data leaks in production.
- **Code obfuscation**: Always use ProGuard/R8 for release builds. Keep class/method names generic.
- **No unnecessary complexity**: Add no features that aren't essential.

### Build & Release
- **Release builds must be obfuscated**: Always add/update ProGuard/R8 rules in build.gradle.kts
- **Verify package names**: Before implementing a new MaskStrategy, verify the actual package name on a real device (use `adb shell pm list packages` or check Play Store URL)
- **No debug-only code in release**: Ensure BuildConfig.DEBUG checks don't expose sensitive data

### Notification Handling
- In onNotificationPosted:
  - Cancel the original notification first.
  - Then post exactly one masked notification.
  - Never post the same notification twice.
  - Always use the same notification ID/group logic to prevent duplicates.
- For Discord and Gmail, handle specially if needed, but keep it minimal.

## Coding Style

### Security-First Approach
- Always ask: "Could this be reverse-engineered? Could this leak user data in production?"
- Prefer readable, auditable code over clever solutions
- If adding a new library, ask: "Is this really necessary? Does it have network permissions?"

### Changes
- Keep changes as small and localized as possible.
- Do not refactor unrelated parts.
- Add comments only when necessary.
- If you are unsure, ask me before making changes.

## Testing Priority

### Functional Testing
- Test primarily with LINE, Telegram, and Notification Tester app.
- Gmail self-email and Discord webhook are known to behave specially — handle with care.
- **Verify package names**: Always confirm the actual package name on device before hardcoding

### Security Testing
- Test that release builds have no network permissions
- Verify ProGuard obfuscation is working (use `apktool` or `jadx` to check)
- Ensure no sensitive data in logcat output

## Goal
Make the app work reliably with exactly ONE masked notification per original notification, while maintaining:
1. **Maximum privacy** (no data collection ever)
2. **Security** (obfuscation, no leaks)
3. **Simplicity** (minimal code)
4. **Store-ready** (F-Droid/Play Store compliant)

Always prioritize: Privacy > Security > Simplicity > Features
