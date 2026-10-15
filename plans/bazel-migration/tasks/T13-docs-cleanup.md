# T13 — Contributor guidance and obsolete infrastructure removal

- Status: `complete`
- Blocked by: `T12`
- Spec coverage: `R9`, `R10`, `R11`, `R13`, `AC5`, `AC6`, `AC8`

## Delivers

Consolidate contributor docs and update current setup/agent guides/changelog. Delete remaining Buildr/Ruby/Node/Yarn/Docusaurus/GWT/API-diff/downstream infrastructure, dependencies and assets, preserving historical changelog and encrypted secrets.

Audit task-created support code, fixtures, tests, logs and documents against the user's replacement AGENTS.md cleanup rule (source/scope in TASKMAP.md). Retain only concrete continuing coverage/use, and remove replaced migration experiments and their build wiring.

## Acceptance criteria

- [x] Current commands and migration contracts match implemented behavior; no obsolete executable/configuration/current-doc references remain, except the documented J2CL pruning annotation and historical descriptions. Git diff verifies secrets untouched and no scratch output.

## Validation

- Repository searches with explicit historical/pruning exclusions; site link check; command-doc consistency; diff/stat and secret-path unchanged checks — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Removed Buildr/Gemfile/Ruby/Node/Yarn/Docusaurus configs and scripts, Travis secret-decryption automation, all real downstream build/statistics code and fixtures, API-diff code/history/viewer assets, and obsolete ignore/attribute entries. Preserved historical changelog/credits and encrypted `etc/secrets` unchanged.
- Removed owned Grim symbol-map annotations and metadata processor wiring with their obsolete downstream checks; removed unused direct legacy-annotation pin and JSON processor dependencies through depgen. Transitive annotation imports remain only where unchanged third-party source compilation or explicit legacy-ignored regression coverage needs them. Owned runtime/processor/tool/example sources contain no legacy nullness imports.
- Consolidated contributor command/policy guidance, updated AGENTS and four technical guides, current Maven/Bazel/JSpecify/IDE setup, affected FAQ/examples and Unreleased changelog. Current setup has one Maven version property; actual note preparation succeeded against temporary copies of the current changelog/setup. No current legacy tooling instructions remain; archive rejection checks and the J2CL pruning marker intentionally name removed payloads, and README credits retain historical origin.
- Added Java root-configuration export plus a disposable synthetic external Bazel consumer because dependency overrides are root-only. The consumer compiled/ran the real packaged fixture (including processor/Sting integration) and built public core/DOM J2CL plus the runnable example in both modes. No dependency repositories changed. The final script contains its Bazel output base inside its temporary parent; the full T14 gate exercises that cleanup lifecycle. Removed both earlier one-off consumer workspaces and their verified task-owned global output bases.
- `bazel test //core:tests //dom:tests //processor:all_tests //tools/release:tests //tools/site:tests //third_party/java:verify_config_sha256 //:java_format_check`: all 14 test targets pass. `tools/site.sh check`: 26,925 local links/fragments, zero broken. Java formatting/buildifier/Bash syntax/diff checks pass; Error Prone switch comparison exactly matches Arez with none missing or added.
- Audited retained verification for ongoing contracts: complete strict processor goldens/negative controls, real archive/bundle/consumer checks, browser behavior and renderer/link/deploy checks remain. Duplicate bootstrap processor/browser smokes and completed manual probes are gone. Removed 135 completed task logs/probe/scratch artifacts and the one-off unsigned prepare bundle. Temporary plan evidence remains only until required workflow closeout; durable conclusions are in existing guides.
