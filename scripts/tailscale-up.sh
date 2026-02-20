#!/usr/bin/env bash
set -euo pipefail

STATE_DIR="${TAILSCALE_STATE_DIR:-/var/lib/tailscale}"
RUN_DIR="${TAILSCALE_RUN_DIR:-/var/run/tailscale}"
STATE_FILE="${TAILSCALE_STATE_FILE:-$STATE_DIR/tailscaled.state}"
SOCKET_FILE="${TAILSCALE_SOCKET_FILE:-$RUN_DIR/tailscaled.sock}"
LOG_FILE="${TAILSCALE_LOG_FILE:-/tmp/tailscaled.log}"

if ! command -v tailscaled >/dev/null 2>&1 || ! command -v tailscale >/dev/null 2>&1; then
  echo "tailscale is not installed. Run: curl -fsSL https://tailscale.com/install.sh | sh" >&2
  exit 1
fi

sudo mkdir -p "$STATE_DIR" "$RUN_DIR"

if ! pgrep -x tailscaled >/dev/null 2>&1; then
  sudo nohup tailscaled \
    --state="$STATE_FILE" \
    --socket="$SOCKET_FILE" \
    --tun=userspace-networking \
    >"$LOG_FILE" 2>&1 &
fi

for _ in $(seq 1 20); do
  if pgrep -x tailscaled >/dev/null 2>&1 && [ -S "$SOCKET_FILE" ]; then
    break
  fi
  sleep 0.5
done

if ! pgrep -x tailscaled >/dev/null 2>&1 || [ ! -S "$SOCKET_FILE" ]; then
  echo "tailscaled did not become ready. Check: $LOG_FILE" >&2
  exit 1
fi

sudo tailscale --socket="$SOCKET_FILE" up "$@"