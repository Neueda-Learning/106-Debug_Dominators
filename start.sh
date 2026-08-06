#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/Backend/payment-processing-system"
FRONTEND_DIR="$ROOT_DIR/Frontend"
BACKEND_PORT="${BACKEND_PORT:-8082}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"
FRONTEND_HOST="${FRONTEND_HOST:-127.0.0.1}"
API_BASE_URL="${API_BASE_URL:-http://localhost:${BACKEND_PORT}}"

if [[ ! -d "$BACKEND_DIR" ]]; then
  echo "Backend directory not found: $BACKEND_DIR" >&2
  exit 1
fi

if [[ ! -f "$FRONTEND_DIR/package.json" ]]; then
  echo "Frontend package.json not found: $FRONTEND_DIR/package.json" >&2
  exit 1
fi

if command -v lsof >/dev/null 2>&1; then
  PORT_PID="$(lsof -ti:${BACKEND_PORT} || true)"
  if [[ -n "$PORT_PID" ]]; then
    echo "Stopping process on :${BACKEND_PORT} (PID $PORT_PID)..."
    kill -9 "$PORT_PID" || true
  fi
fi

start_backend() {
  echo "Starting backend on http://localhost:${BACKEND_PORT} ..."
  if [[ -f "$BACKEND_DIR/mvnw" ]]; then
    (
      cd "$BACKEND_DIR"
      chmod +x ./mvnw
      nohup ./mvnw -f "$BACKEND_DIR/pom.xml" spring-boot:run -Dspring-boot.run.arguments="--server.port=${BACKEND_PORT}" > "$ROOT_DIR/backend.log" 2>&1 &
      echo $! > "$ROOT_DIR/.backend.pid"
    )
  elif [[ -f "$BACKEND_DIR/mvnw.cmd" ]]; then
    (
      cd "$BACKEND_DIR"
      nohup cmd.exe /c mvnw.cmd -f "$BACKEND_DIR/pom.xml" spring-boot:run -Dspring-boot.run.arguments="--server.port=${BACKEND_PORT}" > "$ROOT_DIR/backend.log" 2>&1 &
      echo $! > "$ROOT_DIR/.backend.pid"
    )
  else
    echo "No Maven wrapper found in backend directory." >&2
    exit 1
  fi
}

start_backend

echo "Installing frontend dependencies..."
cd "$FRONTEND_DIR"
npm install

echo "Starting frontend dev server..."
echo "Frontend URL: http://${FRONTEND_HOST}:${FRONTEND_PORT}"
echo "Frontend API base: ${API_BASE_URL}"
VITE_API_BASE_URL="$API_BASE_URL" npm run dev -- --host "$FRONTEND_HOST" --port "$FRONTEND_PORT"
