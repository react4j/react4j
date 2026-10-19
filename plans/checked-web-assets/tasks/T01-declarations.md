# T01 — Checked asset declarations

- Status: `pending`
- Blocked by: None
- Spec coverage: R1,R2; AC1

## Delivers

Deliver @Assets/@Asset declarations and lazy generated accessors, an independent versioned metadata contract, Bazel web_assets catalogues and Java/J2CL propagation without file-byte compilation inputs. Validate package-relative path resolution, generated inputs and duplicate logical paths. Preserve metadata in J2CL/source archives. Include strict processor tests/goldens and Bazel catalogue validation tests.

## Acceptance criteria

- [ ] Delivered behavior and its spec criteria pass focused validation.
- [ ] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [ ] Evidence is recorded and the implementation/task state committed together.

## Validation

Processor suites, asset metadata/path unit tests, package-local build/formatter checks and a small J2CL asset declaration build.

## Evidence

- pending
