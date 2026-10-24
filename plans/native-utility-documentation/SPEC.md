# Native utility documentation and teaching example

## Source

- Accepted scope: interview answers 1A/2A/3A/4B/5B/6A in this chat; final shared-understanding confirmation.
- Execution authority: “confirmed and implement using structured-delivery-workflow”. Automatic plan checkpoint; no deployment or merge authority.
- Repository evidence: `css/utility-language/README.md`, `docs/specs/native-utility-language.md`, ADRs 0005/0006, existing finite utility app and native browser fixtures. Commit `165ef456a` already extracted and removed the old design document.

## Problem

Developers lack a progressive introduction to native utilities, and rule authors must piece together syntax, provider setup and authoring commands. Useful concrete examples were compressed when the design was retired. Neither native utility guidance nor a teaching application appears on the public website.

## Required outcome

A developer can run a small example, select its utilities from Java, understand their rules, and follow checked instructions to author or troubleshoot a rule library. Agents have a precise reference. Public documentation and the runnable example are linked from the website build.

## Scope and constraints

- Documentation and a separate teaching application using existing language behavior. Retain the finite utility example.
- JVM-only compilation; preserve the narrow internal language package and current public APIs.
- Preserve existing compiler guarantees and explicit application responsibilities for container setup, popover association, opening/closing and focus. No Rose migration or browser compatibility claim.
- No new compiler features, site redesign, executable generator, external service, deployment or merge.
- Keep prior authorized CssInputIntegrationTest cleanup separate from new work; do not overwrite it.

## Requirements

- R1: The new runnable application demonstrates utility selection, spacing, colors, state composition and variants through ordinary Java declarations.
- R2: The application demonstrates an actual responsive container, repeated scoped anchored popovers and a real entry transition; controls are labelled, keyboard usable and reduced motion is respected.
- R3: The public guide starts with consuming utilities, then teaches authoring using the same source vocabulary. Explain that the language supplies no standard utility library. Include setup, local precedence, base/modifier composition and explicit globals.
- R4: Author guidance provides an end-to-end validate/describe/expand workflow with checked provider provenance, useful diagnostics and positive/negative examples. Preserve precise lexical, escaping, source-overlap, ordering, value and browser-dependent limitations.
- R5: Portable recipes recover useful tables, shadows, negative margins, direct multi-declaration replacements for apply macros and popover role mapping. Include a short Tailwind comparison rather than a migration promise.
- R6: The website includes navigable user guidance and a runnable optimized example, with local development/production preview instructions. Repository entry points route readers without duplicating normative contracts.
- R7: Audit design knowledge against existing glossary/specifications/ADRs; promote only missing qualifying durable knowledge, remove residual task narration and repair links. Do not restore the deleted design or create redundant artifacts.

## Acceptance criteria

- AC1 (R1,R2): Packaged app browser tests in development and optimized modes verify base/state classes, spacing/color changes, container response, per-instance popover placement/open/close/focus, and a nonzero entry transition. Representative narrow and wide visual inspection passes.
- AC2 (R3,R6): Website build/link checks pass; navigation reaches consumer and author guidance plus the shipped example. Both local preview modes build.
- AC3 (R4,R5): Documented commands execute on real sources and an actual compiled checked provider; successful and intentionally invalid requests produce documented outcomes. Recipes are validated using the JVM compiler rather than text matching.
- AC4 (R7): Knowledge routing records one owner per conclusion, relevant domain links validate, and no current link references the retired design.
- AC5 (all): Full `tools/check.sh` gate passes, independent implementation review reports Findings: none, and temporary plan/evidence files are removed at closeout.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Consumer-first public guide plus precise author reference | Accepted 1A/3A; current combined reference is not a tutorial | Adds navigable learning paths without changing language | Follow first walkthrough from Java to rules |
| D2 | Separate native app using the existing example build pattern | Accepted 4B/5B; finite example remains useful | Maintained application and browser tests, no new framework | Resize, change state and open both popovers |
| D3 | Portable recipes and short Tailwind comparison | Accepted 2A/6A | Preserves practical knowledge without Rose-specific migration promises | Check familiar examples and limitations |
| D4 | Extend the existing implementation branch/PR | Existing implementation PR120 owns this language and delivery authority | Review includes this follow-up, no separate competing PR | Review documentation and application additions |

## Technical decisions

- Explicit package-local Java/J2CL/CSS source lists and existing annotation processors, browser_application, CSS bundling, test and preview facilities.
- Native browser popover attributes handle invokers/light dismissal/Escape; scope remains within each trigger/panel wrapper. No coordinate runtime.
- Keep implementation and mutable CLI/build details in source-adjacent reference; accepted requirements and rationale retain their existing canonical owners.
- Link site pages locally; repository-only references use committed GitHub source links where the site cannot package them. Do not expand the site generator merely to copy snippets.

## Testing decisions

- Narrow app build/browser tests first; real DOM/computed styles and animation objects, no placeholder fixtures.
- Compiler verification of documented sources and diagnostics; site build/check and domain link validator.
- Final full repository gate, then independent read-only implementation review. Review budgets: planning 3 rounds plus final confirmation; implementation 5 plus final confirmation.

## Open questions

None.
