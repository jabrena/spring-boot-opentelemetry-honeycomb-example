#!/usr/bin/env bash
# Test gateway /api/a and /api/c endpoints – assert both return "hello world"
# and verify traces reach the OTEL collector (via Jaeger).
# Usage: ./test-gateway.sh [--up]   (use --up to start docker compose first)

set -e

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8084}"
JAEGER_URL="${JAEGER_URL:-http://localhost:16686}"
MAX_WAIT=60
INTERVAL=3
TRACE_WAIT=5

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
echo "=== Test traces in OTEL collector (via Jaeger) ==="
echo "Waiting ${TRACE_WAIT}s for traces to propagate..."
sleep "$TRACE_WAIT"

# Query Jaeger for traces from our services (collector forwards to Jaeger)
SERVICES=("gateway" "module-a" "module-c")
for svc in "${SERVICES[@]}"; do
  TRACES=$(curl -sf "${JAEGER_URL}/api/traces?service=${svc}&limit=1&lookback=5m" 2>/dev/null || echo "{}")
  if command -v jq &>/dev/null; then
    COUNT=$(echo "$TRACES" | jq -r '.data | length // 0')
  else
    # Fallback: check if response contains trace data (has "traceID" or "spans")
    COUNT=0
    if [[ "$TRACES" == *"traceID"* ]] || [[ "$TRACES" == *"spans"* ]]; then
      COUNT=1
    fi
  fi
  if [[ "$COUNT" -gt 0 ]] || [[ "$TRACES" == *"traceID"* ]]; then
    echo "PASS: traces found for service ${svc}"
  else
    echo "FAIL: no traces found for service ${svc} (OTEL collector -> Jaeger pipeline)"
    echo "  Response: ${TRACES:0:200}..."
    exit 1
  fi
done

echo ""
echo "All tests passed."
