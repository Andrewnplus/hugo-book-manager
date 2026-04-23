# gh-runner

Self-hosted GitHub Actions runners for `nplus-father` org, running on this
host as Docker containers. Used by HugoBook reusable workflow
`nplus-father/workflows/.github/workflows/hugobook-build-deploy.yml`.

## What's here

| File | Purpose |
|---|---|
| `Dockerfile.runner` | Custom image: myoung34 runner + Temurin 25 JDK + Hugo 0.154.5 extended + Go |
| `docker-compose.yml` | 4 runner services, CPU/mem capped to 60% of host |
| `.env.example` | Env template — copy to `.env`, add PAT |
| `.env` | **Gitignored.** Real secrets live here |
| `scripts/health-check.sh` | Verify containers + print registration URL |

## First-time setup

1. **Create a fine-grained PAT** on GitHub — see `.env.example` for exact scope.
2. `cp .env.example .env` and paste the PAT into `ACCESS_TOKEN=`.
3. `docker compose build` — builds the custom runner image (~3 min first time).
4. `docker compose up -d` — starts 4 runners; each self-registers with org.
5. `./scripts/health-check.sh` — confirms containers are up, prints URL to verify in GitHub UI.

## Everyday commands

```bash
docker compose up -d            # start
docker compose down             # stop (de-registers ephemeral runners)
docker compose logs -f runner-1 # tail one runner
docker compose restart          # after changing .env
docker compose build --no-cache # after Dockerfile changes (e.g. new Hugo version)
```

## Scaling

- **More runners**: copy a `runner-N` block in `docker-compose.yml`, bump the name.
- **Less**: `docker compose stop runner-4` (doesn't remove).
- **Bigger jobs**: raise `cpus`/`memory` in the `x-runner-base` anchor.

## Security notes

- Docker socket is **not** mounted — workflows can't touch the host's Docker.
- `EPHEMERAL=true` — each job gets a fresh container; no state leaks between runs.
- `no-new-privileges` — runner can't escalate via setuid binaries.
- **Only use with private repos.** Public-repo forks can run arbitrary code on your runner.
  Enforce in GitHub UI: org Settings → Actions → Runner groups → restrict to private repos.

## Troubleshooting

- **Runner shows offline** — `docker compose ps` says Up? If yes, check logs: `docker compose logs runner-1`. Most common cause: bad `ACCESS_TOKEN` scope.
- **`permission denied` for docker** — your user isn't in `docker` group. Fix: `sudo usermod -aG docker $USER && newgrp docker`.
- **Build fails on Temurin step** — Adoptium apt repo blip. Rerun `docker compose build`.
- **Runners register but jobs don't pick up** — verify labels in workflow match: `runs-on: [self-hosted, linux, hugobook]`.
