#!/usr/bin/env bash
# setup_new_app.sh — Customize KyuubiMask as a new Android app template
#
# Usage:
#   chmod +x scripts/setup_new_app.sh
#   ./scripts/setup_new_app.sh
#
# Requirements: bash, python3, GNU coreutils (sed, find)
#   Linux / GitHub Actions (ubuntu-latest): works out of the box
#   macOS: requires GNU sed   →  brew install gnu-sed
#          then run:  PATH="$(brew --prefix gnu-sed)/libexec/gnubin:$PATH" ./scripts/setup_new_app.sh
#
# What this script does:
#   1. Replaces applicationId / namespace in app/build.gradle.kts
#   2. Replaces package references in app/src/main/AndroidManifest.xml
#   3. Updates rootProject.name in settings.gradle.kts
#   4. Resets versionCode/versionName to 1 / 0.1.0
#   5. Rewrites CHANGELOG.md with a clean [Unreleased] section
#   6. Clears fastlane changelogs
#   7. Optionally removes F-Droid metadata
#   8. Prints manual steps to finish the rename

set -euo pipefail

OLD_APP_ID="com.rtneg.kyuubimask"
OLD_PROJECT_NAME="KyuubiMask"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$REPO_ROOT"

# ── Colour helpers ────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}ℹ${NC}  $*"; }
success() { echo -e "${GREEN}✅${NC} $*"; }
warn()    { echo -e "${YELLOW}⚠️${NC}  $*"; }
error()   { echo -e "${RED}❌${NC} $*"; exit 1; }

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║   Android App Template Setup                        ║"
echo "║   KyuubiMask → Your New App                         ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# ── Gather inputs ─────────────────────────────────────────────────────────────
read -rp "$(echo -e "${CYAN}?${NC} New app name            (e.g. MyApp):               ")" APP_NAME
read -rp "$(echo -e "${CYAN}?${NC} New application ID      (e.g. com.example.myapp):  ")" NEW_APP_ID
read -rp "$(echo -e "${CYAN}?${NC} GitHub owner (user/org) (e.g. your-username):       ")" GITHUB_OWNER
read -rp "$(echo -e "${CYAN}?${NC} GitHub repository name  (e.g. my-app):              ")" REPO_NAME
read -rp "$(echo -e "${CYAN}?${NC} Remove F-Droid files?   [y/N]:                      ")" REMOVE_FDROID
REMOVE_FDROID="${REMOVE_FDROID:-N}"

echo ""

# ── Validate inputs ───────────────────────────────────────────────────────────
[[ -z "$APP_NAME"      ]] && error "App name cannot be empty."
[[ -z "$NEW_APP_ID"    ]] && error "Application ID cannot be empty."
[[ -z "$GITHUB_OWNER"  ]] && error "GitHub owner cannot be empty."
[[ -z "$REPO_NAME"     ]] && error "Repository name cannot be empty."

if ! echo "$NEW_APP_ID" | grep -qE '^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$'; then
    error "Application ID '$NEW_APP_ID' is not valid. Expected format: com.example.myapp"
fi

info "Applying changes to: $REPO_ROOT"
echo ""

# ── Verify required tools ─────────────────────────────────────────────────────
command -v python3 >/dev/null 2>&1 || error "python3 is required but not found. Install Python 3 and re-run."

# ── 1. app/build.gradle.kts ───────────────────────────────────────────────────
info "Updating app/build.gradle.kts ..."

sed -i \
    -e "s|namespace = \"$OLD_APP_ID\"|namespace = \"$NEW_APP_ID\"|g" \
    -e "s|applicationId = \"$OLD_APP_ID\"|applicationId = \"$NEW_APP_ID\"|g" \
    -e 's|versionCode = [0-9]*|versionCode = 1|' \
    -e 's|versionName = "[^"]*"|versionName = "0.1.0"|' \
    app/build.gradle.kts

success "app/build.gradle.kts updated"

# ── 2. settings.gradle.kts ───────────────────────────────────────────────────
info "Updating settings.gradle.kts ..."

sed -i "s|rootProject.name = \"$OLD_PROJECT_NAME\"|rootProject.name = \"$APP_NAME\"|g" \
    settings.gradle.kts

success "settings.gradle.kts updated"

# ── 3. AndroidManifest.xml ───────────────────────────────────────────────────
info "Updating app/src/main/AndroidManifest.xml ..."

sed -i "s|$OLD_APP_ID|$NEW_APP_ID|g" app/src/main/AndroidManifest.xml

success "AndroidManifest.xml updated"

# ── 4. CHANGELOG.md ───────────────────────────────────────────────────────────
info "Resetting CHANGELOG.md ..."

cat > CHANGELOG.md <<CHANGELOG
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

<!-- Add your upcoming changes here. auto-tag.yml will promote this to a versioned section on PR merge. -->

[Unreleased]: https://github.com/$GITHUB_OWNER/$REPO_NAME/compare/v0.1.0...HEAD
CHANGELOG

success "CHANGELOG.md reset to clean state"

# ── 5. Fastlane changelogs ───────────────────────────────────────────────────
if [ -d fastlane/metadata/android ]; then
    info "Clearing fastlane changelogs ..."
    find fastlane/metadata/android -name "*.txt" -path "*/changelogs/*" -delete
    success "Fastlane changelogs cleared"
fi

# ── 6. F-Droid metadata ──────────────────────────────────────────────────────
if [[ "$REMOVE_FDROID" =~ ^[Yy]$ ]]; then
    info "Removing F-Droid metadata and workflow ..."
    rm -rf metadata/
    rm -f .github/workflows/update-fdroid.yml
    success "F-Droid files removed"
else
    # Rename the metadata file to the new app ID
    if [ -f "metadata/$OLD_APP_ID.yml" ]; then
        info "Renaming F-Droid metadata file ..."
        NEW_META="metadata/$NEW_APP_ID.yml"
        cp "metadata/$OLD_APP_ID.yml" "$NEW_META"
        rm "metadata/$OLD_APP_ID.yml"

        # Reset the metadata content for the new app
        sed -i \
            -e "s|$OLD_APP_ID|$NEW_APP_ID|g" \
            -e "s|soraiyu/KyuubiMask|$GITHUB_OWNER/$REPO_NAME|g" \
            -e "s|AutoName: $OLD_PROJECT_NAME|AutoName: $APP_NAME|g" \
            "$NEW_META"

        # Clear existing builds so the user starts fresh
        python3 - <<PYEOF
import re

with open('$NEW_META', 'r') as f:
    content = f.read()

# Clear the Builds: section — keep the key but leave body empty
content = re.sub(
    r'^Builds:.*?(?=^\w|\Z)',
    'Builds:\n\n',
    content,
    flags=re.MULTILINE | re.DOTALL
)

# Reset CurrentVersion and CurrentVersionCode
content = re.sub(r'^CurrentVersion:.*', 'CurrentVersion: 0.1.0', content, flags=re.MULTILINE)
content = re.sub(r'^CurrentVersionCode:.*', 'CurrentVersionCode: 1', content, flags=re.MULTILINE)

with open('$NEW_META', 'w') as f:
    f.write(content)

print("F-Droid metadata reset")
PYEOF

        success "F-Droid metadata renamed and reset: $NEW_META"
    fi
fi

# ── Summary ───────────────────────────────────────────────────────────────────
echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║   Automated changes complete!                       ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
success "applicationId:    $OLD_APP_ID  →  $NEW_APP_ID"
success "project name:     $OLD_PROJECT_NAME  →  $APP_NAME"
success "versionName:      reset to 0.1.0"
success "versionCode:      reset to 1"
success "CHANGELOG.md:     reset with [Unreleased] section"
echo ""

warn "The following steps must be completed MANUALLY:"
echo ""
echo "  1. Move Kotlin source files to the new package directory:"
echo ""
echo "     mkdir -p app/src/main/java/$(echo "$NEW_APP_ID" | tr '.' '/')"
echo "     mv app/src/main/java/$(echo "$OLD_APP_ID" | tr '.' '/')/* \\"
echo "        app/src/main/java/$(echo "$NEW_APP_ID" | tr '.' '/')/"
echo "     rmdir -p app/src/main/java/$(echo "$OLD_APP_ID" | tr '.' '/')"
echo ""
echo "  2. Update all 'package' and 'import' statements in .kt files:"
echo ""
echo "     find app/src -name '*.kt' -exec \\"
echo "       sed -i 's|$(echo "$OLD_APP_ID" | sed 's/\./\\./g')|$NEW_APP_ID|g' {} +"
echo ""
echo "     💡 Tip: Use Android Studio > Refactor > Rename for a safer rename."
echo ""
echo "  3. Do the same for test sources:"
echo ""
echo "     find app/src/test -name '*.kt' -exec \\"
echo "       sed -i 's|$(echo "$OLD_APP_ID" | sed 's/\./\\./g')|$NEW_APP_ID|g' {} +"
echo ""
echo "  4. Update app icon, app name strings, and other app-specific resources."
echo ""
echo "  5. Set GitHub Secrets for release signing:"
echo "       ANDROID_KEYSTORE_BASE64"
echo "       ANDROID_KEYSTORE_PASSWORD"
echo "       ANDROID_KEY_ALIAS"
echo "       ANDROID_KEY_PASSWORD"
echo ""
echo "  6. Enable in GitHub Settings → Actions → General:"
echo "       ✓ Read and write permissions"
echo "       ✓ Allow GitHub Actions to create and approve pull requests"
echo ""
echo "  7. Update the Release description in .github/workflows/release.yml"
echo "     to match your app."
echo ""
echo "  8. Optionally update scripts/check_security.py:"
echo "     - Remove the INTERNET permission check if your app needs network access"
echo "     - Remove permissions from DANGEROUS_PERMISSIONS that your app requires"
echo ""
echo "  9. Commit and push:"
echo "       git add ."
echo "       git commit -m 'chore: initialize from KyuubiMask template'"
echo "       git push"
echo ""
echo "  10. Read TEMPLATE_GUIDE.md for full customization details."
echo ""
success "Setup script finished. Happy building! 🚀"
