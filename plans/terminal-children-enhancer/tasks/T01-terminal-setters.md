# T01 — Terminal child setters

- Status: pending
- Blocked by: None
- Spec coverage: R1, R5; AC1

## Delivers

Setting scalar child or plural children always produces ReactNode. Preserve existing build availability, scalar wrapping, defaults and key generation.

## Acceptance criteria

- [ ] Required and optional scalar/array children terminate with other optional properties present, for methods and constructors.
- [ ] Existing omission through build and scalar rendering remain correct.
- [ ] Affected generated fixtures are regenerated using the documented tool and reviewed.

## Validation

- Focused InputEnhancerTest compile cases and generated goldens.
- Real React browser scenarios for optional scalar and plural inputs.

## Evidence

Pending.
