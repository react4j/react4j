# T07 — Expose agent authoring feedback and packaged commands

- Status: `pending`
- Blocked by: `T06`
- Spec coverage: `R2`, `R10`, `AC7`

## Delivers

Rule authors can validate, describe and expand native libraries on the JVM with the same checked behavior and diagnostic facts as application compilation.

## Acceptance criteria

- [ ] Provide packaged JVM authoring entry points for provider-aware validation/description/batch expansion, using the facade rather than a second matcher.
- [ ] Return structured and human diagnostics with matching codes, payload/request offsets, definition/token locations and suggestions; preserve aggregate batch failures.
- [ ] Describe infinite sources and bounds; show ordered rules/conditions, canonical identities, fingerprint, origins and browser-dependent checks in expansion feedback.
- [ ] Preserve evaluated constant annotation locations/spans under current source-map promises. No complete identifier-map promise or scanner is added.
- [ ] Validate distribution includes required classes/data and no new external executable/Maven/browser runtime dependency; malformed descriptors fail clearly.

## Validation

- Real command invocations and packaged processor tests, positive/negative batches, provider snapshots, descriptor roundtrips/invalid versions and javac diagnostics.
- Distribution build and artifact tests; verify new module is included under shading/relocation policy.

## Evidence

- `pending`
