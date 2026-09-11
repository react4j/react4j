# T05 — Migrate TodoMVC upgrade branches

- Status: `pending`
- Blocked by: `T04`
- Spec coverage: `R7`, `R9`, `AC8`, `D7`, `D8`

## Delivers

All six configured TodoMVC variants compile against React4j `0.229` without any Akasha dependency or source usage, from pushed temporary release branches.

## Acceptance criteria

- [ ] Each variant replaces root lookup, input value access, and hash routing with minimal local JsInterop types.
- [ ] Each variant contains no Akasha dependency, GWT inherit, import, or active source reference.
- [ ] Each variant compiles against the local React4j `0.229` artifacts before push.
- [ ] One focused commit is pushed to each approved `*-React4jUpgrade-0.229` branch.
- [ ] No permanent branch or pull request is changed.

## Validation

- Per-variant Buildr/Maven/J2CL build command used by the downstream harness — proves each migrated application compiles.
- Local and remote branch/reference inspection — proves zero Akasha use and exact branch delivery.

## Evidence

- `pending`
