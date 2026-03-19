#!/usr/bin/env python3
"""
Security checks for KyuubiMask.

Verifies that the app's privacy and security invariants are maintained:
  1. No INTERNET permission in any source manifest
  2. allowBackup="false" in main manifest
  3. ProGuard/R8 minification enabled inside the release { } buildType
  4. No dangerous permissions in any source manifest
  5. All Log.* calls guarded by BuildConfig.DEBUG
  6. No hardcoded secrets or API keys in source code
"""

import glob
import re
import sys

EXIT_CODE = 0


def fail(message: str) -> None:
    global EXIT_CODE
    print(f"❌ {message}")
    EXIT_CODE = 1


def ok(message: str) -> None:
    print(f"✅ {message}")


# Kotlin production source files (all variant source sets, excluding test dirs)
kt_files = sorted(
    f
    for f in glob.glob("app/src/**/java/**/*.kt", recursive=True)
    if not re.search(r"/(?:android)?[Tt]est/", f)
)


# ── 1 & 4. Collect all source manifests ──────────────────────────────────────

manifests = glob.glob("app/src/**/AndroidManifest.xml", recursive=True)
if not manifests:
    fail("No AndroidManifest.xml found under app/src/")
    sys.exit(1)

# ── 1. No INTERNET permission ─────────────────────────────────────────────────

found_internet = False
for m in manifests:
    with open(m) as f:
        if "android.permission.INTERNET" in f.read():
            fail(f"INTERNET permission found in {m}")
            found_internet = True
if not found_internet:
    ok("No INTERNET permission found (checked all source manifests)")

# ── 2. allowBackup=false ──────────────────────────────────────────────────────

main_manifest = "app/src/main/AndroidManifest.xml"
with open(main_manifest) as f:
    main_manifest_content = f.read()

if 'android:allowBackup="false"' in main_manifest_content:
    ok("allowBackup is false")
else:
    fail("allowBackup is not false or is missing in AndroidManifest.xml")

# ── 3. ProGuard/R8 enabled inside release { } block ──────────────────────────

with open("app/build.gradle.kts") as f:
    gradle_content = f.read()


def release_block_has_minify(text: str) -> bool:
    """Return True if isMinifyEnabled = true appears inside the release { } buildType block.

    Handles both same-line brace style (``release {``) and
    next-line brace style (``release\\n{``).
    """
    in_release = False
    depth = 0
    pending_release = False  # saw "release" keyword, waiting for the opening brace
    for line in text.splitlines():
        stripped = line.strip()
        if in_release:
            depth += stripped.count("{") - stripped.count("}")
            if re.match(r"isMinifyEnabled\s*=\s*true", stripped):
                return True
            if depth <= 0:
                in_release = False
        elif pending_release:
            if stripped.startswith("{"):
                in_release = True
                depth = stripped.count("{") - stripped.count("}")
            pending_release = False
        elif re.match(r"release\s*\{", stripped):
            in_release = True
            depth = stripped.count("{") - stripped.count("}")
        elif re.match(r"release\s*$", stripped):
            pending_release = True
    return False


if release_block_has_minify(gradle_content):
    ok("ProGuard/R8 minification is enabled for release builds")
else:
    fail("ProGuard/R8 minification is not enabled in the release buildType in build.gradle.kts")

# ── 4. No dangerous permissions ───────────────────────────────────────────────

DANGEROUS_PERMISSIONS = [
    "android.permission.READ_CONTACTS",
    "android.permission.WRITE_CONTACTS",
    "android.permission.ACCESS_FINE_LOCATION",
    "android.permission.ACCESS_COARSE_LOCATION",
    "android.permission.CAMERA",
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE",
    "android.permission.RECORD_AUDIO",
    "android.permission.READ_PHONE_STATE",
    "android.permission.READ_SMS",
    "android.permission.RECEIVE_SMS",
    "android.permission.SEND_SMS",
    "android.permission.READ_CALL_LOG",
    "android.permission.PROCESS_OUTGOING_CALLS",
]

found_dangerous = False
for m in manifests:
    with open(m) as f:
        content = f.read()
    for perm in DANGEROUS_PERMISSIONS:
        if perm in content:
            fail(f"Dangerous permission found in {m}: {perm}")
            found_dangerous = True
if not found_dangerous:
    ok("No dangerous permissions found")

# ── 5. All Log.* calls guarded by BuildConfig.DEBUG ──────────────────────────

# LOOKBACK_WINDOW: number of lines before the Log call to search for a guard.
# Combining with the current line handles both:
#   • Multi-line guard:  if (BuildConfig.DEBUG) {\n    Log.d(…)
#   • Single-line guard: if (BuildConfig.DEBUG) Log.d(…)
LOOKBACK_WINDOW = 5

log_pattern = re.compile(r"\bLog\.(d|v|i|w|e)\s*\(")
found_unguarded = False

for filepath in kt_files:
    with open(filepath, encoding="utf-8") as f:
        lines = f.readlines()
    for i, line in enumerate(lines):
        if log_pattern.search(line):
            # Include the current line so single-line guards are detected
            window_start = max(0, i - LOOKBACK_WINDOW)
            window = "".join(lines[window_start : i + 1])
            if "BuildConfig.DEBUG" not in window:
                print(f"  UNGUARDED: {filepath}:{i + 1}: {line.rstrip()}")
                found_unguarded = True

if not found_unguarded:
    ok("All Log calls are guarded by BuildConfig.DEBUG")
else:
    fail("Unguarded Log calls found — wrap them in: if (BuildConfig.DEBUG) { ... }")

# ── 6. No hardcoded secrets or API keys ──────────────────────────────────────

# Note: patterns use Python re (not grep ERE), so \s and other escapes work correctly.
SECRET_PATTERNS = [
    re.compile(r"AIza[0-9A-Za-z_-]{35}"),
    re.compile(r"AAAA[A-Za-z0-9_-]{7}:[A-Za-z0-9_-]{140}"),
    re.compile(r'api[_-]?key\s*=\s*[\'"][^\'"]{10,}', re.IGNORECASE),
    re.compile(r'secret\s*=\s*[\'"][^\'"]{10,}', re.IGNORECASE),
    re.compile(r'password\s*=\s*[\'"][^\'"]{6,}', re.IGNORECASE),
]

found_secret = False
for filepath in kt_files:
    with open(filepath, encoding="utf-8") as f:
        content = f.read()
    for pattern in SECRET_PATTERNS:
        if pattern.search(content):
            fail(
                f"Potential hardcoded secret in {filepath} "
                f"(pattern: {pattern.pattern[:40]})"
            )
            found_secret = True

if not found_secret:
    ok("No hardcoded secret patterns found")

# ── Summary ───────────────────────────────────────────────────────────────────

print()
if EXIT_CODE == 0:
    print("✅ All security checks passed!")
    print()
    print("Verified:")
    print("  - No INTERNET permission (all source manifests)")
    print("  - allowBackup=false")
    print("  - ProGuard/R8 minification enabled for release buildType")
    print("  - No dangerous permissions")
    print("  - All Log calls guarded by BuildConfig.DEBUG")
    print("  - No hardcoded secrets or API keys")
else:
    print("❌ One or more security checks failed!")

sys.exit(EXIT_CODE)
