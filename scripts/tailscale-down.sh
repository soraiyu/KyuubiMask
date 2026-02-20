#!/usr/bin/env bash
set -euo pipefail

SOCKET_FILE="${TAILSCALE_SOCKET_FILE:-/var/run/tailscale/tailscaled.sock}"

if ! command -v tailscale >/dev/null 2>&1; then
  echo "tailscale is not installed." >&2
  exit 1
fi

if [ -S "$SOCKET_FILE" ]; then
  sudo tailscale --socket="$SOCKET_FILE" down "$@"
else
  sudo tailscale down "$@"
fi
