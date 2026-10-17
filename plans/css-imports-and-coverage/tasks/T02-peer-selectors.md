# T02 — Checked peer selectors and graph semantics

Status: complete. Blocked by: T01. Coverage: R2, R3; AC2, AC3.

Resolve root-only :css(alias) references through parsed selector members. Extend scoped component combinators under the spec contract, resolve global selector references, and separate retained closure from global-only ordering. Cover all legal combinators/conditions, invalid arguments/ownership/function contexts, real peer cycles versus global cycles, dependency diamonds, unused imports, maps and separate libraries. Run compiler/processor/bundler/integration suites and goldens.

Evidence: tools/update_goldens.sh css passed (459 processor cases; ten paired golden changes). bazel test //css:tests --test_output=errors passed all eight targets, including new peer compiler matrices, separate full/header compilation and J2CL header integration. Source-map origin assertions pass. Mutual/self peer references, mixed peer/global paths, global diamonds and true global-cycle failures are checked. Java format and buildifier checks pass; the new unit target is included in the format gate. Bare -- aliases are rejected because the pinned parser cannot consume them as function arguments; --peer is covered. Browser interaction coverage remains in T06.
