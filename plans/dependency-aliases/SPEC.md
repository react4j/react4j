# Dependency aliases and shared J2CL ownership

## Source and authority

The user requested internal aliases for external dependencies, proposed public aliases as a strategy for shared dependency collisions, and instructed “proceed” after the assessment recommending curated public aliases and one compilation owned by each shared library. Continue the existing React4j PR112 and Rose draft PR6342; commits and their authorized publication continue. No merge, deployment or unrelated upstream publication. Root baseline4201cca79; isolated Rose baseline98126ce001f. Main Rose checkout and credentials remain untouched.

## Problem and required outcome

Application BUILD files currently embed upstream target layouts. React4j and Replicant also compile identical Arez/Braincheck/Zemeckis sources into distinct Closure modules: current Acal linking fails393 diagnostics,392 duplicate modules. Stable aliases must preserve target/provider identity, while the complete linked graph uses one compilation for each runtime library.

## Scope and constraints

Implement a curated //third_party dependency facade; migrate handwritten dependency/tool references, examples and isolated consumer checks. Generated dependency-import definitions, module declarations, Starlark load() references and Bazel-mandated special labels remain with their owners. Keep unsupported JVM/tool implementation aliases private. Use existing source-owned upstream J2CL targets; dependency patches on immutable pins may route upstream third-party targets to them. Export those patches through existing consumer configuration. Update the isolated Rose tree, including vendored Replicant aliases and its standalone dependency configuration as needed. Do not edit Bazel caches, add a dependency platform repository, force Replicant to depend on React4j, suppress duplicates or retain obsolete compilation paths. Keep explicit sources, narrow visibility, user-owned checker selection and development/compression boundaries. No unrelated Java, API or application migration.

## Requirements and acceptance criteria

- R1/AC1 (T01): React4j callers use stable aliases for external dependency/tool labels. Public browser runtime and processor aliases forward original providers without recompilation; raw imports and build implementation dependencies stay private. Existing public examples copied into an external workspace compile through supported labels; positive and negative consumer checks enforce the facade.
- R2/AC2 (T02): Arez, Braincheck and Zemeckis each have one native J2CL owner in a combined graph. Preserve native JavaScript/configuration, generated outputs, Java source origins and existing Root-selected static checks. React4j publication keeps its checked JVM source producers; native owners retain their current explicit compiler policy, without imposing additional checker selection on downstream applications. Aliases replace source-archive recompilations throughout reachable upstream and React4j paths. Root browser suites and a real combined consumer link/render check pass without suppressed duplicate diagnostics.
- R3/AC3 (T03): Rose/Replicant consume canonical owners and the curated public facade while preserving standalone dependency configuration. Real Acal and Rose BUNDLE/ADVANCED links pass, packaged delivery tests pass, and local action graph remains two BUNDLE links without production compression/service-worker/WAR work. Full production WAR graph analyzes; execute build/browser checks where supported without deploying or requiring credentials. Distinguish any independent failures from resolved collisions.
- R4/AC4 (T04): Permanent consumer documentation defines public alias guarantees and root-owned module resolution. Run simplify, domain promotion, full React4j gate and affected Replicant checks, independent review and exact temporary-tree cleanup. Publish existing PR updates with immutable remotely resolvable pins and honest verification boundaries.

## Decisions

| Decision | Rationale and impact | Verification |
| --- | --- | --- |
| Curated //third_party facade, one public spelling per dependency | Stable consumer names; implementation JVM/tool imports remain private. Explicit supported runtime/plugins only, no blanket visibility. | Copied examples, native provider compilation and denied internal labels |
| Existing source-package J2CL target owns each shared library | Removes duplicate code generation and preserves owner-selected native companions; aliases reuse compilation. | Combined Closure links and ownership query |
| Patch existing immutable upstream pins through existing export mechanism | Avoids new module ownership, publishing unrelated repositories or coupling Replicant to React4j. Consumers must apply the exported root configuration; vendored Replicant retains needed standalone patches. | Clean module resolution, patch application, standalone checks and Rose links |
| Consumer retains compiler and version selection | Aliases offer supported targets without forcing NullAway or hiding root Bzlmod overrides. | Existing caller-owned checker positive/negative tests |

## Testing and delivery

Tasks remain sequential and primary-implemented. Read-only independent planning and implementation reviewers are workflow gates. Start with target/provider checks and negative collision reproduction, then producer/action reachability checks for existing checked JVM publication inputs, caller-owned checker negative tests, real combined native links, copied public examples, browser suites, downstream adapter tests and actual Rose app links. Run tools/check.sh for React4j and affected vendored Replicant as applicable; preserve exact unrelated gate failures instead of masking them. Verify no-op/asset cache boundaries only if implementation changes action behavior; otherwise verify graph equivalence. Retain durable contracts at existing spec/setup owners; raw diagnostics and delivery sequencing stay in this temporary tree/PR evidence. Review and cleanup precede final Root publication, then final Rose immutable pin and normal remote resolution.

## Open questions

None requiring user direction. Downstream failures beyond dependency convergence are investigated and bounded explicitly; they do not waive the promised successful combined graph.
