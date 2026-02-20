#!/usr/bin/env bash
set -euo pipefail

SOCKET_FILE="${TAILSCALE_SOCKET_FILE:-/var/run/tailscale/tailscaled.sock}"

if pgrep -x tailscaled >/dev/null 2>&1; then
  if [ -S "$SOCKET_FILE" ] && command -v tailscale >/dev/null 2>&1; then
    sudo tailscale --socket="$SOCKET_FILE" down >/dev/null 2>&1 || true
  fi
  sudo pkill -x tailscaled
  echo "tailscaled stopped"
else
  echo "tailscaled is not running"
fi
