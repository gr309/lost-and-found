#!/bin/sh
set -eu

# Render commonly exposes a managed database as DATABASE_URL. Older deployments
# may use DB_URL, and may already include the jdbc: prefix.
if [ -z "${DB_URL:-}" ] && [ -n "${DATABASE_URL:-}" ]; then
  DB_URL="${DATABASE_URL}"
  export DB_URL
fi

# An empty Render variable must not override Spring's fallback URL.
if [ -z "${DB_URL:-}" ]; then
  unset DB_URL
fi

# Render's Postgres reference is postgresql://..., while older deployments may
# already have jdbc:postgresql://... in DB_URL. Spring Boot needs the JDBC form.
case "${DB_URL:-}" in
  jdbc:*) ;;
  mysql://*|postgresql://*) DB_URL="jdbc:${DB_URL}"; export DB_URL ;;
esac

exec java -XX:MaxRAMPercentage=75.0 -jar app.jar
