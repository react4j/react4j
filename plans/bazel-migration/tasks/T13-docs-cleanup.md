# T13 — Contributor guidance and obsolete infrastructure removal

- Status: `pending`
- Blocked by: `T12`
- Spec coverage: `R9`, `R10`, `R11`, `R13`, `AC5`, `AC6`, `AC8`

## Delivers

Consolidate contributor docs and update current setup/agent guides/changelog. Delete remaining Buildr/Ruby/Node/Yarn/Docusaurus/GWT/API-diff/downstream infrastructure, dependencies and assets, preserving historical changelog and encrypted secrets.

## Acceptance criteria

- [ ] Current commands and migration contracts match implemented behavior; no obsolete executable/configuration/current-doc references remain, except the documented J2CL pruning annotation and historical descriptions. Git diff verifies secrets untouched and no scratch output.

## Validation

- Repository searches with explicit historical/pruning exclusions; site link check; command-doc consistency; diff/stat and secret-path unchanged checks — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
