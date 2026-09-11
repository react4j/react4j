# T06 — Migrate broad-browser upgrade branches

- Status: `pending`
- Blocked by: `T04`
- Spec coverage: `R8`, `R9`, `AC9`, `D7`, `D8`

## Delivers

Drumloop, Flux Challenge, Heart Rate Monitor, and Web Speech Demo compile against React4j `0.229` while retaining Akasha only as an explicit application dependency.

## Acceptance criteria

- [ ] React4j event boundaries use unchecked casts where the application needs Akasha-specific types.
- [ ] ReactDOM container calls compile through the neutral Object boundary.
- [ ] Flux Challenge explicitly inherits the Akasha GWT module.
- [ ] Existing broad Akasha APIs and direct dependency declarations remain intact.
- [ ] Each application compiles before push.
- [ ] One focused commit is pushed to each approved `master-React4jUpgrade-0.229` branch without changing master or creating a PR.

## Validation

- Each application's existing release GWT build — proves API migration and explicit dependency ownership.
- Local and remote branch/reference inspection — proves exact temporary branch delivery and unchanged permanent refs.

## Evidence

- `pending`
