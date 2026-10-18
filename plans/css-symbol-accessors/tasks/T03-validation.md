# T03 — Coverage, documentation and full validation

- Status: complete
- Blocked by: T02
- Spec coverage: R7/AC7 and final verification of AC1–AC6

## Delivers

Permanent downstream documentation and comprehensive evidence for all accepted symbol contracts with unchanged coverage thresholds and a simplified implementation ready for independent review.

## Acceptance criteria

- [x] Constraint matrices cover inference/invalid names, illegal method/type shapes, aliases/retention/cycles, identifier context boundaries and separate compilation.
- [x] Unit coverage of newly hazardous logic is high; configured roots pass unchanged thresholds.
- [x] README/changelog explain actual user behavior and migration boundaries without process narration.
- [x] Simplify pass and tools/check.sh pass; evidence is recorded before the task commit.

## Validation

Run focused tests for added cases, formatter/buildifier, then tools/check.sh. Inspect all diffs and generated goldens. Do not weaken policy to pass coverage. Independent implementation review follows this task; closeout deletes exactly this plan tree after review passes.

## Evidence

Passed the focused CSS units, implementation processor suite and actual Bazel-header/JVM/J2CL integration after the simplify pass. Added resource-owner, wrong-site, empty import, mixed-cycle and ASCII attribute-case constraints; simplified factory validation and property rewriting, and cached provider-name parsing. Updated permanent README/API docs and the downstream changelog.

Full tools/check.sh passed: formatting/buildifier, hermeticity in both modes, archives/Javadocs/examples, all tests and optimized browser tests, JVM/runtime coverage enforcement, isolated Bazel consumers, site checks, release validation and reproducibility. CSS processor: 1140/1150 lines (99.1%), 838/870 branches (96.3%); internal: 232/237 and 129/138; bundler: 246/250 and 148/157. Thresholds unchanged. No qualifying domain artifact directories exist. Reviewed the complete task diff and new goldens.
