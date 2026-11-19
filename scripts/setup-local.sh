#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ -e .env.local ]]; then
    echo '.env.local already exists; keeping its settings.'
    exit 0
fi
command -v openssl >/dev/null
umask 077
# noclobber also protects against two concurrent setup processes.
set -o noclobber
cat > .env.local <<EOF
DB_HOST=localhost
DB_PORT=3307
DB_NAME=lostfound
DB_USERNAME=lostfound
DB_PASSWORD=$(openssl rand -hex 24)
MYSQL_ROOT_PASSWORD=$(openssl rand -hex 24)
JWT_SECRET=$(openssl rand -base64 32)
PORT=8080
UPLOAD_DIR=./uploads
CORS_ALLOWED_ORIGINS=http://localhost:5173
APP_ENV=dev
APP_DEBUG=false
SQL_DEBUG=false
EOF
echo 'Created private .env.local with generated credentials. Existing .env was preserved.'
