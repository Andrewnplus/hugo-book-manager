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
echo "=== Image invariant: Hugo must NOT be baked in ==="
# hugobook-build-deploy.yml installs Hugo per job via actions-hugo and then
# fails the build if the hugo on PATH is not the version it asked for. A baked
# hugo in /usr/local/bin can shadow that install, so the image must not carry
# one — this is also what makes Hugo bumps safe to automerge (single source of
# truth). A container still running a pre-2026-07 image will fail this check.
stale=0
for i in 1 2 3 4; do
  name="hugobook-runner-$i"
  docker ps --format '{{.Names}}' | grep -qx "$name" || continue
  if docker exec "$name" sh -c 'command -v hugo' >/dev/null 2>&1; then
    baked=$(docker exec "$name" hugo version 2>/dev/null | head -1)
    echo "  ✗ $name has a baked hugo: $baked"
    stale=1
  else
    echo "  ✓ $name has no baked hugo"
  fi
done
if [ "$stale" -eq 1 ]; then
  echo ""
  echo "  ERROR: stale runner image. Rebuild before the next Renovate run, or an"
  echo "         automerged Hugo bump will break every book repo's build:"
  echo "           git pull && docker compose build --no-cache && docker compose up -d"
fi

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
