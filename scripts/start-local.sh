#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
./scripts/setup-local.sh
mkdir -p uploads/items
docker compose --env-file .env.local up -d --build
printf 'Local services started. Check health at http://localhost:%s/health/\n' "$(sed -n 's/^PORT=//p' .env.local)"
echo 'View startup progress: docker compose --env-file .env.local logs -f api'
