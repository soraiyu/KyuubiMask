# Copilot Coding Agent Instructions for KyuubiMask

## Project Overview
- This is a minimal, privacy-first Android notification masking app.
- Core purpose: Cancel sensitive notifications and replace them with generic masked ones.
- Never store, log, or transmit any notification content.

## Absolute Rules (Never Break These)
- **Privacy First**: Never save, log, cache, or send any part of the original notification content (title, text, bigText, extras, etc.).
- No INTERNET permission, no analytics, no Firebase, no Room, no SharedPreferences for notification data.
- Do not add any unnecessary complexity or features.
- Keep the code as simple and minimal as possible.
- Prefer fewer lines of code over clever solutions.

## Notification Handling Rules
- In onNotificationPosted:
  - Cancel the original notification first.
  - Then post exactly one masked notification.
  - Never post the same notification twice.
- Always use the same notification ID/group logic to prevent duplicates.
- For Discord and Gmail, handle specially if needed, but keep it minimal.

## Coding Style
- Keep changes as small and localized as possible.
- Do not refactor unrelated parts.
- Add comments only when necessary.
- If you are unsure, ask me before making changes.

## Testing Priority
- Test primarily with LINE, Telegram, and Notification Tester app.
- Gmail self-email and Discord webhook are known to behave specially — handle with care.

## Goal
Make the app work reliably with exactly ONE masked notification per original notification, while maintaining maximum privacy and simplicity.

Always prioritize privacy, simplicity, and correctness over new features.