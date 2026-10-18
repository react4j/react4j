# T06 — Examples, permanent docs and complete validation

Status: complete. Blocked by: T05. Coverage: R5 plus regression R1-R4; AC1-AC5.

Demonstrate peer interaction and automatic global inclusion in relevant examples and save-button browser tests. Keep single-owner styles colocated and use inline/local singleton accessors. Update permanent CSS guide/changelog and contributor coverage explanation, compile snippets and verify maps. Run tools/check.sh, inspect the complete diff, record evidence, obtain independent implementation review, remove the completed exact plan tree, and push the existing draft PR without merging.

Evidence: save-button browser checks exercise focus-driven peer styling and a bundle without explicit global roots, retaining required defaults and excluding unselected globals. All Java fences in css/README.md compile through the actual processor and strict generated-code checker. Permanent CSS/contributor docs describe import kinds, graph semantics and coverage attribution; the changelog describes downstream behavior. Simplification removes obsolete module-argument parsing and narrows internal visibility.

Validation: tools/check.sh passed on 2026-10-18: formatter/buildifier, default/optimized hermeticity, public builds, 36 development test targets, six optimized browser targets, 18 JVM coverage targets, every coverage rule, isolated JVM/J2CL consumers in both modes, site build/check, unsigned release validation and all 13 independent reproducibility comparisons. Native JVM browser suites use a shared wrapper that grants loopback access only to TestRunner actions; compilation remains sandboxed. Independent implementation review and delivery cleanup/push are subsequent workflow gates.
