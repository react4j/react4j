# T08 — Maven-equivalent publication archives

- Status: `pending`
- Blocked by: `T07`
- Spec coverage: `R8`, `R11`, `AC4`

## Delivers

Assemble reproducible core/DOM/processor binary/source/Javadoc/POM archives, shade dependencies and service metadata, and expose public JVM imports/plugins backed by assembled jars. Verify isolated JVM/processor consumption.

## Acceptance criteria

- [ ] Archive checks prove complete generated/authored sources/resources, clean binary/source separation, processor isolation/service loading, correct Java levels and POM inputs. JVM/processor packaged consumer runs and repeatable jars match.

## Validation

- Build release artifact aggregate; inspect archives/POMs/class versions; run isolated packaged JVM/processor consumer and archive reproducibility check — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
