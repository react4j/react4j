# T03 — Enforced downstream boundary

- Status: complete
- Blocked by: T02
- Spec coverage: R3, R4, R5; AC2, AC3

## Delivers

Repository-only target/file defaults and restricted internal Starlark loads. Explicitly public consumer APIs and necessary support dependencies are checked from a separate workspace; examples/tests/implementation labels cannot be consumed downstream.

## Acceptance criteria

- [x] No broad public package defaults remain; narrower friend visibility is preserved.
- [x] Every remaining public target/file/load has a current consumer or documented supported role.
- [x] External consumer succeeds in both modes; target/load/retired-name negative probes fail for the intended reason.
- [x] Temporary planning probe is removed.

## Validation

Bazel visibility queries including generated targets and files, separate-workspace positive and negative probes, buildifier, hermeticity and affected tests.

## Evidence

- Complete external-visibility query shrinks 300 public rules/380 sources/386 generated files to 13 supported rules, two Starlark sources and their 16 generated outputs. No public package default remains; existing friend restrictions are preserved.
- Native external application and real Chromium tests pass in fastbuild and opt. Ten private dependency/file probes, three absent root labels, eight internal load probes and four configured-check failure probes reject for their exact expected diagnostics.
- Full repository 45-suite run and both hermeticity modes pass. Buildifier and shell syntax pass.
- Public browser testing requires rules_webtesting in the React4j module mapping; promote the existing pinned dependency from dev-only.
- Dependency regeneration passes without drift. A two-line repository export wrapper gives depgen-generated configuration exports internal visibility without editing generated sections.
