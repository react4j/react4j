# T01 — Deliver the expanded language and package design

- Status: `complete`
- Blocked by: `None`
- Spec coverage: `R1`–`R5`, `AC1`–`AC5`

## Delivers

A complete reviewable language proposal connecting agent-authored rules, checked application class lists, a narrow JVM compiler package, named identities, diagnostics and illustrative Rose examples. Workflow review, publication and cleanup follow task completion under the spec's delivery obligations.

## Acceptance criteria

- [x] Scalar/compound domains, compact sources and range constraints have consistent grammar, examples and failures.
- [x] Container size/style/scroll-state and starting-style wrappers preserve explicit setup and written nesting order.
- [x] Named anchors have explicit ownership and instance scope; the Rose recipe demonstrates wiring, placement and sizing without claiming a bug fix.
- [x] Proposed package, dependency direction and complete caller contract are clear and grounded in current build/source layout.
- [x] Documentation checks pass and the lasting diff is design-only, ready for the workflow's independent review and closeout gates.

## Validation

- Run the domain-modeling local-link validator and explicitly check the design (which is outside its default artifact scope) for local targets/fragments.
- Check balanced fences, JSON examples, exact decimal scale/range examples, family/variant example coverage, and ownership/setup consistency.
- Review the whole document against `R1`–`R5`; independent delivered-document review follows task completion under the workflow.
- Inspect Git diff/status before commits and publication; confirm no production source, build file or secrets changes.
- At workflow closeout, verify the created PR URL, readiness and check status; do not merge it.

## Evidence

- Domain link validator passed. Explicit design/plan checks passed: 16 local links/fragments, 17 fenced examples with JSON parsed, 15 value types, 16 distinct literal utilities, 27 distinct family definitions, five exact scale calculations and two range rejection cases.
- Checked all 17 external W3C specification links/fragments; corrected one obsolete scroll-state fragment and verified the replacement.
- Author reviewed R1–R5, EBNF/source examples, static/dynamic name boundaries, range uncertainty and source/build/packaging separation. The staged lasting change is one design document; no code, build configuration, Rose files or secrets are changed.
- No compiler/browser tests run: grammar and package are proposals. The document records concrete implementation and browser feasibility checks without claiming they passed.
