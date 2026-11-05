# T01 Carry nested declarations through CSS tooling

- Spec coverage: R1–R5 and AC1–AC5.

## Delivers

A single vertical change covers the shared parser, component and global authoring, optimized linking and final bundling. The same parser change affects every entry point, so acceptance tests and documentation ship in this slice.

## Acceptance and validation

- [x] Verify grouped direct declaration parsing, ordering and rejection at stylesheet level with the parser and bundler suites.
- [x] Verify checked global and component output, source maps and the packaged processor with the processor suites.
- [x] Exercise nested declarations in optimized collision checks and URL finalization with the bundler suite.
- [x] Verify browser matching and cascade in both development and optimized builds. Run the original Rose stylesheet through the CLI using a temporary copy.
- [x] Document the durable scope and user-facing change. Validate domain links, then run the project gate.

## Evidence

- Focused Bazel parser, compiler, bundler and asset suites passed. The processor fixtures passed in the full development suite. Both checked diagnostic goldens were regenerated.
- Pinned Chromium passed the CSS example regression in development and in all three optimized naming variants. The regression covers shared and global pseudo-elements, the component root, and selector specificity and declaration order.
- Bundled the unmodified Rose migration stylesheet at `34701f51746f9342bc8afe485dcbaa40197ab34e`. The authored bytes and source-map content are preserved. Pinned Chromium computed `oklab(0.573832 -0.0295402 -0.134073 / 0.5)` for the placeholder before and after bundling.
- Domain link validation and `git diff --check` passed.

- `tools/check.sh` passed all 62 development, 17 optimized browser and 26 instrumented JVM targets, coverage thresholds, site validation and unsigned archives. The local macOS run enabled Bazel test loopback and temporarily passed `--no-sandbox` to the pinned coverage browser. The unmodified baseline reproduces the Chrome helper crash at the runfiles symlink. Both overrides were removed after verification.
