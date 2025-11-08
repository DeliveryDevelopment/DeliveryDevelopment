#!/usr/bin/env bash
set -euo pipefail

# Ensure the target database exists even when reusing an initialized data volume.
DB_HOST="${DB_HOST:-db}"
DB_PORT="${DB_PORT:-5432}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-secret}"
POSTGRES_DB="${POSTGRES_DB:-delivery}"

export PGPASSWORD="${POSTGRES_PASSWORD}"

until pg_isready -h "${DB_HOST}" -p "${DB_PORT}" -U "${POSTGRES_USER}" >/dev/null 2>&1; do
  sleep 2
done

EXISTS=$(psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${POSTGRES_USER}" -d postgres -Atc \
  "SELECT 1 FROM pg_database WHERE datname='${POSTGRES_DB}'")

if [[ "${EXISTS}" != "1" ]]; then
  psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${POSTGRES_USER}" -d postgres -c \
    "CREATE DATABASE \"${POSTGRES_DB}\";"
fi
