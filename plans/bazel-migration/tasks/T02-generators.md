# T02 — Java DOM and React asset generation

- Status: `pending`
- Blocked by: `T01`
- Spec coverage: `R2`, `AC1`

## Delivers

Bazel builds a small Java DOM generator and produces deterministic source archives and checksum-pinned patched React/ReactDOM 16.6.0 assets. Preserve all factory overloads and asset patch behavior.

## Acceptance criteria

- [ ] Generator compiles under the reference tool policy. Repeated generation is identical and focused signature/patch checks compare against the read Ruby generator contracts without invoking Ruby. Compilation of generated DOM output depends on core/DOM and is explicitly owned by T04; T02 compiles the generator itself.

## Validation

- Build generator/generated-source/assets targets; compare repeated output/signature inventory; assert exactly the required React patches and source archive entries — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
