#!/usr/bin/env bash
# Health check for hugobook runners.
# Usage: ./scripts/health-check.sh
set -euo pipefail

cd "$(dirname "$0")/.."

echo "=== Containers ==="
docker compose ps

echo ""
echo "=== Recent runner logs (last 20 lines each) ==="
for i in 1 2 3 4; do
  name="hugobook-runner-$i"
  if docker ps --format '{{.Names}}' | grep -qx "$name"; then
    echo "--- $name ---"
    docker logs --tail 20 "$name" 2>&1 | sed 's/^/  /'
  else
    echo "--- $name: NOT RUNNING ---"
  fi
done

echo ""
echo "=== Resource usage ==="
docker stats --no-stream --format \
  "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}" \
  hugobook-runner-1 hugobook-runner-2 hugobook-runner-3 hugobook-runner-4 2>/dev/null \
  || echo "(some containers not running)"

echo ""
echo "=== Next step ==="
echo "Check runner registration in GitHub UI:"
echo "  https://github.com/organizations/nplus-father/settings/actions/runners"
echo "All 4 should show 'Idle' with labels: self-hosted, linux, hugobook"
