# T04 — Activate checked globals with correct precedence

- Status: in_progress
- Blocked by: T03
- Spec coverage: R6 (cross-owner imports), R7, R8, R9 (global permissions); AC3 (boundary/reset), AC4 (precedence)

## Delivers

A separately supplied file or inline reset uses checked theme imports and activates only when explicitly selected. The bundled cascade preserves component precedence while allowing important declarations in global inputs.

## Acceptance criteria

- [ ] Utility definitions reject unguarded ordinary element styles but allow support declarations, registrations and custom-property initialization; selector-function branches cannot bypass the boundary.
- [ ] Selecting a utility does not activate reset/default globals automatically. Explicit global roots/imports preserve deduplication, inclusion order and cycle validation.
- [ ] File-backed and inline globals import library-owned properties/resources using the same canonical identities.
- [ ] Ordinary component rules beat utility rules; authored utility layer ordering remains intact. Independent libraries do not accidentally merge internal layer identities.
- [ ] Standard important declarations work in explicit checked/raw global inputs and continue failing in utility/component definitions.
- [ ] Browser computed styles prove reset selection, utility-to-component precedence and the explicit global important exception.

## Validation

- Processor/bundler boundary tests include selector lists, is/where/not/has, mixed guarded and unguarded branches, support-only blocks and unsupported constructs.
- Browser fixture asserts actual computed styles for selected/unselected reset and normal/important cascade.
- Existing global-root, inclusion-order, raw-name-collision and multi-variant tests remain green.

## Evidence

- pending
