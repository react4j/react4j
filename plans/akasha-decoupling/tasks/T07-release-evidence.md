# T07 — Regenerate release evidence and run the full gate

- Status: `pending`
- Blocked by: `T05`, `T06`
- Spec coverage: `R10`, `R11`, `AC10`, `AC11`, `D9`

## Delivers

The actual release workflow consumes the pushed temporary branches, every configured downstream application passes, and React4j records the resulting `0.229` size evidence.

## Acceptance criteria

- [ ] The downstream statistics workflow fetches and builds all ten pushed upgrade branches.
- [ ] `statistics.properties` contains regenerated `0.229` values from the migrated outputs.
- [ ] Full React4j tests, packaging, API-diff validation, GWT compilation, and downstream checks pass.
- [ ] Generated core and DOM POMs omit Akasha and active source/generated contracts contain no Akasha reference.
- [ ] All React4j and downstream diffs are intentional; no temporary scratch output or unrelated edit remains.
- [ ] Task map records complete full-gate and remote-branch evidence and enters implementation review.

## Validation

- `bundle exec buildr update_downstream_build_stats` and the complete repository test/package gates — prove release integration and refresh the expected measurements.
- Generated metadata, active-reference, git diff, status, and remote-ref checks — prove dependency absence and delivery integrity.

## Evidence

- `pending`
