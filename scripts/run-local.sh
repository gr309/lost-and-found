#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
./scripts/setup-local.sh
set -a
source .env.local
set +a
# Avoid a stale exported remote JDBC URL overriding the local host/port.
unset DB_URL SPRING_DATASOURCE_URL SPRING_DATASOURCE_USERNAME SPRING_DATASOURCE_PASSWORD
exec ./mvnw spring-boot:run
