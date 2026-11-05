# Nested CSS declarations

## Source

- [Rose #6551](https://github.com/stocksoftware/rose/issues/6551) reports a valid nested conditional that React4j rejects before shipping the stylesheet.
- In this chat the user accepted shared CSS, `@GlobalCss` and `@Css`, every grouping rule each input already supports, and a locally carried ph-css patch. The user authorized implementation through structured delivery and a push. The delivery boundary is the feature branch on origin. Planning review is automatic.
- [The parser build](../../third_party/css-parser/parser.bzl) already compiles locally patched ph-css sources. [The CSS authoring guide](../../css/README.md) describes each input's admitted syntax. [CSS Nesting](https://www.w3.org/TR/css-nesting-1/#nested-declarations-rule) defines matching and ordering.

## Required outcome

Authors can put declarations directly inside supported groups nested under a style rule. Rose can bundle its original placeholder rule and can author it as checked global CSS. A component's implicit root is a style rule.

## Requirements and acceptance

| Requirement | Acceptance |
| --- | --- |
| R1. Accept declaration runs in each supported group beneath a style rule, through mixed groups and nested selectors. | AC1. Shared and global inputs accept media, supports, container, layer and starting-style groups. Components accept media and supports. Tests cover nested groups, explicit child selectors, and declarations before, between and after them. |
| R2. Preserve the nearest style rule's selector matching, per-branch specificity, pseudo-elements and cascade order. | AC2. Browser tests exercise the Rose placeholder, a competing rule and source order in development and optimized output. Component lowering uses the full resolved selector list for direct declarations. |
| R3. Retain ordinary declaration validation, symbol discovery, renaming, pruning, asset URL processing and source origins. | AC3. Tests exercise a nested declaration through checked processing and linking, raw name collision checks, final URL edits and source maps. |
| R4. Keep the input-specific rule sets and placement restrictions. Report invalid group declarations at the authored location. | AC4. Top-level groups in shared and global sheets reject direct declarations with a useful location. Flat utility CSS rejects native style nesting. Existing authored child selectors keep their checked behavior. |
| R5. Ship a single patched parser through the normal tooling and document the accepted contract. | AC5. Parser, shaded processor and bundler checks pass. The user-facing guide, changelog and durable specification describe the admitted scope. |

## Technical decisions

- Extend the existing pinned ph-css patch and represent declaration runs as actual nested declaration nodes reachable by the ordinary visitors. Carry style context through every admitted group, including opaque group bodies reparsed by React4j.
- Shared CSS emits the authored nesting. Checked global CSS preserves native nesting after rewriting. Components continue to lower checked selectors and declarations, using the nearest style rule's exact resolved selectors for a direct declaration run.
- Keep the verified Rose workaround as baseline evidence. Verify the original syntax through the new implementation and the normal link and application packaging paths.

## Verification

Run the focused parser, processor, bundler and browser checks, the domain link validator, then the repository gate `tools/check.sh` before implementation review.
