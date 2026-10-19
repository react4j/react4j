# T01 — Native J2CL assets and caller-owned checks

- Status: pending
- Blocked by: None
- Spec coverage: R2, R5; AC1, AC3

## Delivers

Ordinary J2CL libraries declare web_assets in deps and configure their own checks. CSS link collection propagates the existing J2CL javac jar as validation, so JS-only builds run the selected checks. The repository's checked-Java helper remains useful internally for JVM tests, unstripped sources and publication but no longer owns asset attachment.

## Acceptance criteria

- [ ] Official metadata-only Closure provider makes web_assets usable in J2CL deps and existing aspect collection retains catalogues transitively.
- [ ] JavaScript-only link rejects deliberate Error Prone and NullAway violations using caller configuration; valid code builds without a second Java library.
- [ ] External consumer uses native J2CL with both processors and real assets, preserving both modes, pruning, maps and manifest access.
- [ ] Redundant helper asset attachment is removed and callers migrate; no new public rule or option.

## Validation

Focused asset/processor/browser checks and external consumer in both modes, meaningful failed-check probes, buildifier and hermeticity. Planning probes removed before permanent verification.

## Evidence

Pending.
