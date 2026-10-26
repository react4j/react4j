# T01 — Terminal child setters

- Status: complete
- Blocked by: None
- Spec coverage: R1, R5; AC1

## Delivers

Setting scalar child or plural children always produces ReactNode. Preserve existing build availability, scalar wrapping, defaults and key generation.

## Acceptance criteria

- [x] Required and optional scalar/array children terminate with other optional properties present, for methods and constructors.
- [x] Existing omission through build and scalar rendering remain correct.
- [x] Affected generated fixtures are regenerated using the documented tool and reviewed.

## Validation

- Focused InputEnhancerTest compile cases and generated goldens.
- Real React browser scenarios for optional scalar and plural inputs.

## Evidence

- Isolated Bazel 9.3.1 output after the user-global output base caused server shutdown/runfiles interference; no repository build configuration changed.
- bazel test //processor/src/test/java/react4j/processor:enhancers //core/src/test/js/react4j:browser_tests: both passed; 48 processor cases, including eight child variants, plus real React rendering and omitted optional children.
- tools/update_goldens.sh generated: passed generation and rerun; 16 golden files changed only terminal signatures/returns. Inspected scalar and optional array diffs; wrapping/key paths unchanged.
- tools/java_format.sh write and git diff --check: passed.
