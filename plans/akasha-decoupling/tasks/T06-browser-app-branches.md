# T06 — Migrate broad-browser upgrade branches

- Status: `done`
- Blocked by: `T04`
- Spec coverage: `R8`, `R9`, `AC9`, `D7`, `D8`

## Delivers

Drumloop, Flux Challenge, Heart Rate Monitor, and Web Speech Demo compile against React4j `0.229` while retaining Akasha only as an explicit application dependency.

## Acceptance criteria

- [x] React4j event boundaries use unchecked casts where the application needs Akasha-specific types.
- [x] ReactDOM container calls compile through the neutral Object boundary.
- [x] Flux Challenge explicitly inherits the Akasha GWT module.
- [x] Existing broad Akasha APIs and direct dependency declarations remain intact.
- [x] Each application compiles before push.
- [x] One focused commit is pushed to each approved `master-React4jUpgrade-0.229` branch without changing master or creating a PR.

## Validation

- Each application's existing release GWT build — proves API migration and explicit dependency ownership.
- Local and remote branch/reference inspection — proves exact temporary branch delivery and unchanged permanent refs.

## Evidence

- Production GWT builds passed with React4j `0.229`, Akasha `0.30`, BrainCheck `1.35.0`, Zemeckis `0.18`, and GWT `2.13.1`:
  - Drumloop: `EXCLUDE_GWT_DEV_MODULE=true GWT=react4j-drumloop bundle exec buildr clean package`.
  - Flux Challenge: `EXCLUDE_GWT_DEV_MODULE=true GWT=react4j-sithtracker bundle exec buildr clean package`.
  - Heart Rate Monitor: `EXCLUDE_GWT_DEV_MODULE=true GWT=react4j-heart-rate-monitor bundle exec buildr clean package`.
  - Web Speech Demo: `EXCLUDE_GWT_DEV_MODULE=true GWT=react4j-webspeechdemo bundle exec buildr clean package`.
- The first Drumloop link identified that released `realityforge-buildr` `1.5.24` still selected GWT `2.10`; all four repositories now use the same pinned Buildr revision as React4j, which selected GWT `2.13.1` and completed every link.
- Published one-commit temporary branches:
  - Drumloop `master-React4jUpgrade-0.229`: `5c493541fd34c1931cb8beaba44360133d474d5f`.
  - Flux Challenge `master-React4jUpgrade-0.229`: `ecc84d1d0cad9539a0aa27c9bb8abb25968005a5`.
  - Heart Rate Monitor `master-React4jUpgrade-0.229`: `e741542578bdd223d29ef5f2ccd91d7c96ea6e7e`.
  - Web Speech Demo `master-React4jUpgrade-0.229`: `e6f66b0b39eb4a47a2ae92da7b5d8ef9bb602a45`.
- After fetching both refs in every repository, `origin/master` matched the recorded pre-task base and `git rev-list --count origin/master..origin/master-React4jUpgrade-0.229` returned `1`.
- `rg -n "akasha" build.yaml src/main/java` confirmed each application retains a direct Akasha dependency and Akasha source usage; Flux Challenge's production compile additionally proved the new explicit `<inherits name='akasha.Akasha'/>` declaration.
