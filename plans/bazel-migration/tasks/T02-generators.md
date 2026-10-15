# T02 — Java DOM and React asset generation

- Status: `complete`
- Blocked by: `T01`
- Spec coverage: `R2`, `AC1`

## Delivers

Bazel builds a small Java DOM generator and produces deterministic source archives and checksum-pinned patched React/ReactDOM 16.6.0 assets. Preserve all factory overloads and asset patch behavior.

## Acceptance criteria

- [x] Generator compiles under the reference tool policy. Repeated generation is identical and focused signature/patch checks compare against the read Ruby generator contracts without invoking Ruby. Compilation of generated DOM output depends on core/DOM and is explicitly owned by T04; T02 compiles the generator itself.

## Validation

- Build generator/generated-source/assets targets; compare repeated output/signature inventory; assert exactly the required React patches and source archive entries — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `bazel build //tools/generation:dom_sources //tools/generation:assets`: passed. Both Java tools compile under the unchanged complete reference Error Prone/JSpecify/NullAway policy.
- Independent extraction/expansion of the existing Ruby templates (without invoking Ruby) compared every public static signature: all 529 signatures across 42 HTML tags match the Java output after nullability annotation normalization.
- `bazel run //tools/generation:dom_generator -- /tmp/react4j-dom-repeat.srcjar`: passed; its bytes exactly match the Bazel action's source archive, containing only react4j/dom/DOM.java.
- Actual generated assets match the exact original development/production React.Element substitutions; both ReactDOM assets are byte-identical copies. All four fetches have pinned SHA256 values and retain 16.6.0.
- `bazel run //tools/buildifier:buildifier_check` and `bazel run //tools/ahab:hermeticity.check`: passed with generator reproducibility declarations and no exceptions.
- `git diff --check`: passed. Full generated DOM compilation remains T04 as planned.
