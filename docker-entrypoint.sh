#!/bin/sh
set -eu

# Render's Postgres reference is postgresql://..., while older deployments may
# already have jdbc:postgresql://... in DB_URL. Spring Boot needs the JDBC form.
case "${DB_URL:-}" in
  jdbc:*) ;;
  mysql://*|postgresql://*) DB_URL="jdbc:${DB_URL}"; export DB_URL ;;
esac

exec java -XX:MaxRAMPercentage=75.0 -jar app.jar
