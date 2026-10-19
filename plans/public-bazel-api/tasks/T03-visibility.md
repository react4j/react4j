# T03 — Enforced downstream boundary

- Status: pending
- Blocked by: T02
- Spec coverage: R3, R4, R5; AC2, AC3

## Delivers

Repository-only target/file defaults and restricted internal Starlark loads. Explicitly public consumer APIs and necessary support dependencies are checked from a separate workspace; examples/tests/implementation labels cannot be consumed downstream.

## Acceptance criteria

- [ ] No broad public package defaults remain; narrower friend visibility is preserved.
- [ ] Every remaining public target/file/load has a current consumer or documented supported role.
- [ ] External consumer succeeds in both modes; target/load/retired-name negative probes fail for the intended reason.
- [ ] Temporary planning probe is removed.

## Validation

Bazel visibility queries including generated targets and files, separate-workspace positive and negative probes, buildifier, hermeticity and affected tests.

## Evidence

Pending.
