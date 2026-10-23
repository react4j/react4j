# T06 — Retain animation and font variable dependencies

- Status: complete
- Blocked by: T05
- Spec coverage: R5 (animations/fonts), R6 (imports/assets), R10, R11 (support), R12 (assets); AC6

## Delivers

Standard Tailwind-shaped animation and owned-font utilities work when names are supplied through static custom properties and aliases, including checked font files and optimized removal.

## Acceptance criteria

- [x] A live animation utility retains its variable chain and keyframes; every resolved animation occurrence, including the name inside a property value, uses the same canonical name.
- [x] A live font utility follows property aliases to an owned family, keeps all faces of that family and finalizes its checked files. Dead utility-only families/frames/files disappear.
- [x] Analyze all potentially applicable static definitions without inlining variables or predicting the winning browser cascade.
- [x] Reject ambiguous/mixed name-carrier uses, including a font-name property also rendered as visible text, with a source/path diagnostic. Do not rewrite identifier-looking data or shorthand keywords by spelling alone.
- [x] Existing runtime checked animation/property getters, external names, font fallback/shorthand semantics and imported-provider ownership retain their contracts.
- [x] Dependency pruning removes providers used only by removed declarations and preserves intermediate overrides.

## Validation

- Focused processor/parser tests cover static and aliased animation/font values, multiple scopes, fallbacks, ambiguous mixed uses and dynamic non-name fields.
- Optimized Java/J2CL integration verifies renamed variable values, retained/dead keyframes/faces/assets and correct finalized URLs.
- Browser assertions verify animation-name and font-family computed values with an actual declared font asset.

## Evidence

- `bazel test //css/processor/src/test/java/react4j/css/processor:implementation_tests //css/internal/src/test/java/react4j/css/internal:tests //css/bundler/src/test/java/react4j/css/bundler:tests` passed; processor suite: 604/604. Two obsolete global layer/container rejection cases were removed; positive modern-global coverage remains.
- Final `:unit_tests` and `:utility_tests` passed after the grouped-global font correction. Cases cover aliases, every scoped definition, registered initial strings, fallback chains, imported canonical names, runtime animation getters, external exclusions, keyword collisions and mixed text/query uses. A many-scope data-variable regression avoids Cartesian expansion for ordinary data.
- `bazel test -c opt //css/examples/save-button/src/test/js/react4j/css/example:browser_tests` passed. The packaged application loads the actual declared font through aliases, computes its utility animation, finalizes hashed URLs and omits the unused face/frame/file in production. Development browser coverage also passed.
- Typed occurrence/source-map assertions preserve the Java origin. Existing bundler/property tests continue covering dependency removal and intermediate providers; explicit external frames/faces now have their own pruning regression.
- Java formatting, `git diff --check` and domain link validation passed. A disk-full test attempt was inconclusive and rerun after capacity was restored; it supplies no passing evidence.
- Durable classification: static-carrier semantics and literal types refine utility-input requirement 10; unresolved-runtime conservatism refines liveness requirement 5. Reader-facing syntax and external boundaries remain in css/README.md. Specification-only diff audit passed; no new ADR, glossary or deferred issue is required.

- Implementation-review correction: finite name-flow proof now checks literal property-registration constraints through all definitions and aliases. Unsafe animation/font literal constraints fail at the authored syntax descriptor; universal/type-only constraints preserve the existing renaming behavior. Regression cases cover direct animation names, aliases under conditions, font families and safe type grammars. Utility processor checks passed. The utility-input contract explicitly preserves registration validity; the reader guide owns the supported syntax boundary.
