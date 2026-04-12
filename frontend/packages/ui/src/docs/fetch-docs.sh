#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
API_KEY="${2:?Usage: $0 <base-url> <api-key>}"

for group in authenticated admin public; do
  curl -sf -H "X-API-Key: $API_KEY" "$BASE_URL/v3/api-docs/$group" -o "openapi-$group.json"
  echo "Downloaded openapi-$group.json"
done
