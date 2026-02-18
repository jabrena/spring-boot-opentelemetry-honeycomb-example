#!/usr/bin/env bash
# Test gateway /api/a and /api/c endpoints – assert both return "hello world"
# Usage: ./test-gateway.sh [--up]   (use --up to start docker compose first)

set -e

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8084}"
MAX_WAIT=60
INTERVAL=3

if [[ "$1" == "--up" ]]; then
  echo "Starting Docker Compose..."
  docker compose up -d
  echo "Waiting for services..."
fi

echo "Waiting for gateway at $GATEWAY_URL (max ${MAX_WAIT}s)..."
for ((i=1; i<=MAX_WAIT/INTERVAL; i++)); do
  if curl -sf "$GATEWAY_URL/api/a" >/dev/null 2>&1; then
    echo "Gateway ready."
    break
  fi
  echo "  Attempt $i..."
  sleep $INTERVAL
done

echo ""
echo "=== Test /api/a (module-a -> module-b) ==="
RESP_A=$(curl -s "$GATEWAY_URL/api/a")
echo "Response: $RESP_A"
if [[ "$RESP_A" != "hello world" ]]; then
  echo "FAIL: /api/a expected 'hello world', got '$RESP_A'"
  exit 1
fi
echo "PASS: /api/a returns hello world"

echo ""
echo "=== Test /api/c (module-c -> module-d) ==="
RESP_C=$(curl -s "$GATEWAY_URL/api/c")
echo "Response: $RESP_C"
if [[ "$RESP_C" != "hello world" ]]; then
  echo "FAIL: /api/c expected 'hello world', got '$RESP_C'"
  exit 1
fi
echo "PASS: /api/c returns hello world"

echo ""
echo "All tests passed."
