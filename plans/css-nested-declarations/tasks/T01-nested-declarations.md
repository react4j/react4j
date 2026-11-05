# T01 Carry nested declarations through CSS tooling

- Spec coverage: R1–R5 and AC1–AC5.

## Delivers

A single vertical change covers the shared parser, component and global authoring, optimized linking and final bundling. The same parser change affects every entry point, so acceptance tests and documentation ship in this slice.

## Acceptance and validation

- [ ] Verify grouped direct declaration parsing, ordering and rejection at stylesheet level with the parser and bundler suites.
- [ ] Verify checked global and component output, source maps and the packaged processor with the processor suites.
- [ ] Exercise nested declarations in optimized collision checks and URL finalization with the bundler suite.
- [ ] Verify browser matching and cascade in both development and optimized builds. Run the original Rose stylesheet through the CLI using a temporary copy.
- [ ] Document the durable scope and user-facing change. Validate domain links, then run the project gate.

## Evidence

Pending.
