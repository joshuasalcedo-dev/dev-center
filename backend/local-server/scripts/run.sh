#!/usr/bin/env bash
set -euo pipefail

APP="./local-server"
UPDATES_DIR="updates"

while true; do
    "$APP"
    EXIT_CODE=$?

    if [ "$EXIT_CODE" -ne 80 ]; then
        echo "local-server exited with code $EXIT_CODE."
        exit "$EXIT_CODE"
    fi

    echo "Update detected, applying..."

    UPDATE=$(find "$UPDATES_DIR" -maxdepth 1 -name 'local-server-*' -print -quit 2>/dev/null)

    if [ -z "$UPDATE" ]; then
        echo "No update binary found in $UPDATES_DIR."
        exit 1
    fi

    mv "$UPDATE" "$APP"
    chmod +x "$APP"
    echo "Update applied, restarting..."
done
