# gh-runner

Self-hosted GitHub Actions runners for `nplus-father` org, running on this
host as Docker containers. Used by HugoBook reusable workflow
`nplus-father/workflows/.github/workflows/hugobook-build-deploy.yml`.

## What's here

| File | Purpose |
|---|---|
| `Dockerfile.runner` | Custom image: myoung34 runner + Temurin 25 JDK + Go + Node 22. **No Hugo** — see below |
| `docker-compose.yml` | 4 runner services, CPU/mem capped to 60% of host |
| `.env.example` | Env template — copy to `.env`, add PAT |
| `.env` | **Gitignored.** Real secrets live here |
| `scripts/health-check.sh` | Verify containers + print registration URL |

## Hugo is deliberately not in the image

`hugobook-build-deploy.yml` runs `peaceiris/actions-hugo` on **every** job
(there is no `if:` on that step), and it installs into the Actions tool cache —
which a binary in `/usr/local/bin` is not part of. So baking Hugo bought no
speedup; it only risked shadowing the freshly installed one and tripping that
workflow's `Verify Hugo version` guard.

Keeping the version in exactly one place — the `hugo-version` default in the
shared workflow — is what lets Renovate **automerge** Hugo bumps without a
coordinated image rebuild. Re-adding Hugo here silently breaks that, so CI
(`.github/workflows/ci.yml`) fails the build if this Dockerfile mentions Hugo,
and `scripts/health-check.sh` flags any running container that still has one.

## ⚠️ This is not the only runner fleet on this host

There are also **10 systemd runners** — `actions.runner.nplus-father.andrew-PC-1..10`,
installed under `/home/andrew/actions-runner-{1..10}`, **not managed by this repo**.
They carry the **same `hugobook` label** and the same org, so with all 4 containers up the
org has **14 interchangeable runners** and a job lands on whichever is free — you cannot
tell from the workflow which fleet ran it.

Consequences to keep in mind:

- The systemd ones are **not ephemeral** and reuse `_work/` (real book repos are cached there);
  these containers are ephemeral and start clean every job. Same label, different guarantees.
- Resource caps only bind the containers (2.4 cpu / 9 GB each). The systemd runners are
  uncapped on a 16-core / 62 GB host, so "60% of host" is only true if the other fleet is idle.
- If you want a job pinned to one fleet, they need **different labels** — today nothing separates them.

## First-time setup

Runs on the deploy host, not the dev machine — the repo lives at a different
path there and compose only ever uses relative paths, so the location does not
matter. `.env` is gitignored, so a fresh clone has none: `docker compose up`
fails with `env file ... not found` until step 2 is done.

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

- **Containers stuck in `Restarting (1)`, log says `Invalid configuration provided for token`**
  — `ACCESS_TOKEN` is empty or invalid. An empty value is the easy one to miss: `cp .env.example .env`
  leaves `ACCESS_TOKEN=` blank, config fails, the container exits 1, `restart: unless-stopped`
  brings it back, forever (~20s/loop) with no other symptom. Check without printing the secret:
  `awk -F= '/^ACCESS_TOKEN=/{print length($2)}' .env` — 0 means blank. Verify the token itself with
  `curl -s -o /dev/null -w '%{http_code}\n' -H "Authorization: Bearer $TOKEN" https://api.github.com/user`
  (200 = good, 401 = bad).
- **Runner shows offline** — `docker compose ps` says Up? If yes, check logs: `docker compose logs runner-1`. Most common cause: bad `ACCESS_TOKEN` scope.
- **`permission denied` for docker** — your user isn't in `docker` group. Fix: `sudo usermod -aG docker $USER && newgrp docker`.
- **Build fails on Temurin step** — Adoptium apt repo blip. Rerun `docker compose build`.
- **Runners register but jobs don't pick up** — verify labels in workflow match: `runs-on: [self-hosted, linux, hugobook]`.
