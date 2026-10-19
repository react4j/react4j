# T03 — CSS assets and owned fonts

- Status: `pending`
- Blocked by: T02
- Spec coverage: R3,R4,R5,R6; AC1,AC2,AC3

## Delivers

Resolve typed asset imports in component/global/keyframes declarations, structurally validate URL-bearing values and preserve suffix/fragment behavior. Add scoped local @font-face families, checked selected-family imports through @CssImport(font=...), and deliberate font-family/font handling, retaining faces from surviving CSS and conservatively for dynamic values. Export retained CSS asset evidence per CSS variant; keep all existing pruning/name/source-map contracts.

## Acceptance criteria

- [ ] Delivered behavior and its spec criteria pass focused validation.
- [ ] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [ ] Evidence is recorded and the implementation/task state committed together.

## Validation

Processor formatted/unformatted and cross-library tests; parsed-model URL/font unit tests; bundler/linker integration for dead rules/faces, weights/styles, cross-library shared-family identities and dead imports, external/data/fragment URLs, image-set strings, map preservation and independent CSS variants.

## Evidence

- pending
