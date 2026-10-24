# Native utility language design delivery

## Source

- Accepted scope: the user's confirmed language interview, followed by “confirm. Proceed with structured-delivery-workflow and create a PR for me to review.” The same request asks for a separate narrowly scoped package where possible.
- Execution authority: edit and publish the design for review, including necessary local commits and a pull request. The repeated JVM-only and design-only constraints remain in force. The interview's final confirmation has occurred; no further human plan checkpoint was requested.
- Repository evidence: [current utility contract](../../docs/specs/utility-css.md), [current architectural decision](../../docs/adr/0004-consume-finite-utility-libraries.md), [existing draft](../../docs/design/utility-language.md), and the CSS internal, processor and bundler package-local build declarations.
- Rose evidence: inspected `NativePopoverView.java`, `InlineListPickerView.java` and `server/web_resources/static/css/rose.css` in the local Rose checkout. Rose already wires named anchors in Java and has placement/flip CSS. No browser reproduction of reported positioning failures has been performed.

## Problem

The existing proposal has narrow value types and lacks container, starting-style and named-anchor rules. A reviewer also needs to see how the language can remain a concentrated tooling module rather than spreading grammar knowledge across Java processing and linking.

## Required outcome

A reviewable design document describes the confirmed language and diagnostics, a narrow package and caller contract, and concrete examples. A published pull request contains only lasting design documentation after temporary workflow artifacts are removed.

## Scope

- In scope: extend the design, check examples and references, perform independent plan and delivered-document reviews, commit, and publish a ready-for-review PR.
- Out of scope: production code, build wiring, generated APIs, implementing the compiler, changing current specifications/ADRs to claim the new language exists, modifying Rose, fixing popover bugs, or merging the PR.

## Constraints

- Keep all proposed tool execution on the JVM; retain declarative rule authorship and checked utility class consumption.
- Preserve accepted source selection, one-parameter families, dispatch, selector guards, cascade, ownership and separate-compilation direction except for the expressly approved additions.
- Preserve current finite CSS support and clearly distinguish existing behavior from proposed behavior.
- Leave `etc/secrets` untouched and do not retain temporary delivery artifacts at closeout.

## Requirements

- `R1`: Define broader scalar and compound value domains, compact arguments and rule-specific numeric bounds with honest limits on browser-dependent checking.
- `R2`: Define size, style and scroll-state container queries with named and bracket thresholds, and explicit starting-style variants.
- `R3`: Define checked named-anchor ownership, external references, instance scoping and compatibility with omitted-name anchor expressions; demonstrate a bounded Rose popover recipe without claiming its bugs are fixed.
- `R4`: Concentrate language responsibility behind a small tooling interface in a separate proposed source/build package; distinguish source, build, packaging and runtime boundaries and identify necessary integration changes.
- `R5`: Provide coherent grammar, expansion examples and structured diagnostics that cover accepted behavior and failure cases without creating generic plugins or runtime infrastructure.
- `R6`: Publish the design-only PR after independent review and proportionate verification, preserving the workflow audit in Git history and removing the active plan tree from the final diff.

## Acceptance criteria

- `AC1` (`R1`, `R5`): Value tables, source grammar, numeric examples, range rules and diagnostics agree, including strings, images, positions, grid values and anchor expressions.
- `AC2` (`R2`, `R5`): Container and starting-style definitions show valid composition, checked holes, explicit setup and representative invalid inputs.
- `AC3` (`R3`, `R5`): Anchor definitions distinguish owned/external identities from properties, explain repeated-instance scope/liveness, and a self-contained illustrative popover example states its browser-verification limits.
- `AC4` (`R4`): A separate proposed language package has clear responsibilities, caller inputs/results/errors/effects and dependency direction grounded in current packages; no unnecessary plugin interface or separate deployment is introduced.
- `AC5` (`R6`): Local links, headings, fenced JSON and document consistency checks pass; independent review reports `Findings: none`; final PR diff contains lasting documentation only and is published for human review.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| `D1` | Deliver a design-only PR. | Prior explicit scope remains controlling. | Current processors, APIs and builds remain unchanged. | Check the final diff is documentation only. |
| `D2` | Propose one internal language package and an explicit JVM build target. | Parsing, typing, matching, variant composition and language diagnostics belong together. | Java processing handles annotations/binding; linking handles collected fragments and liveness. | Review the interface and integration obligations. |
| `D3` | Support common compound values and bounded compact scalars. | Approved choices 3A, 4A and 5A balance class ergonomics and early diagnostics. | The design must state recognizer boundaries and runtime uncertainty. | Review valid/invalid examples and range behavior. |
| `D4` | Include all three query categories, starting styles and named anchors. | Approved 1B, 2A, 6A and final B address current application needs. | Container/anchor identities need explicit setup and scope; browser layout is not inferred. | Review container examples and repeated popover instances. |

## Technical decisions

- Keep proposed behavior in the owning design document. Existing specifications and ADRs describe current finite CSS behavior and are linked as future integration obligations, not rewritten by this delivery.
- Describe a language compiler facade with immutable validated descriptors, explicit candidate batches and origin-bearing expansion results. Annotation processing and the authoring tool are representative callers. Source loading, Java element diagnostics, link assembly and final name assignment remain outside the grammar package.
- Reuse low-level CSS syntax/origin support without introducing reverse dependencies from shared CSS infrastructure to the language. Native grammar changes should remain local; new anchor symbols and candidate fragments necessarily affect shared metadata and delivery later.
- Use built-in flip tactics in the popover example. Custom named `@position-try` definitions, new runtime name getters and Rose migration are outside this design expansion.

## Testing decisions

- For this documentation-only delivery, use domain-link validation plus explicit checking of the design's local links, balanced fences, JSON, scale/range examples and definition-to-example consistency. Inspect the final diff for accidental code/configuration changes.
- Do not run or claim compiler/parser/browser behavior for an unimplemented grammar. Record focused future feasibility and browser checks in the design.
- Independent reviews inspect the full plan and then the complete delivered design against these acceptance criteria.

## Open questions

None requiring user direction for this design delivery.
