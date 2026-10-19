# T01 — Native J2CL assets and caller-owned checks

- Status: complete
- Blocked by: None
- Spec coverage: R2, R5; AC1, AC3

## Delivers

Ordinary J2CL libraries declare web_assets in deps and configure their own checks. CSS link collection propagates the existing J2CL javac jar as validation, so JS-only builds run the selected checks. The repository's checked-Java helper remains useful internally for JVM tests, unstripped sources and publication but no longer owns asset attachment.

## Acceptance criteria

- [x] Official metadata-only Closure provider makes web_assets usable in J2CL deps and existing aspect collection retains catalogues transitively.
- [x] JavaScript-only link rejects deliberate Error Prone and NullAway violations using caller configuration; valid code builds without a second Java library.
- [x] External consumer uses native J2CL with both processors and real assets, preserving both modes, pruning, maps and manifest access.
- [x] Redundant helper asset attachment is removed and callers migrate; no new public rule or option.

## Validation

Focused asset/processor/browser checks and external consumer in both modes, meaningful failed-check probes, buildifier and hermeticity. Planning probes removed before permanent verification.

## Evidence

- Focused asset bundler and integration suites pass; six CSS browser variants pass across fastbuild/opt.
- Disposable external workspace passes native @View/@Css/@Assets compilation, generated-source strict dependencies, application/manifest builds and exact ArrayEquals/NullAway rejection from JS-only links in both modes.
- Native public J2CL exports now supply framework dependencies referenced by generated code; downstream NullAway enables assertion refinements. No additional Java compilation adapter is introduced.
- Buildifier, shell syntax, diff whitespace and hermeticity checks pass. Temporary probe packages removed.
