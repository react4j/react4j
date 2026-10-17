# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: complete
- Current frontier: closeout
- Planning reviewer: planning_review (3/3 rounds; passed; final confirmation unused)
- Plan checkpoint: automatic; latest user implementation authority and passing planning review
- Implementation reviewer: implementation_review (2/5 rounds; passed; final confirmation unused)
- Authority: latest user instruction authorizes implementation and heavy testing; no human plan checkpoint.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Shared keyframes](T01-keyframes.md) | complete | None |
| T02 | [Global selection](T02-globals.md) | complete | T01 |
| T03 | [Examples and complete validation](T03-examples.md) | complete | T02 |

## Full-scope validation
Gate: tools/check.sh passed on 2026-10-18. All 439 packaged CSS processor cases, development/optimized browser suites, coverage floors, hermeticity, isolated downstream consumer, site, unsigned release and 13-artifact reproducibility passed. README snippets and separate-library source-map provenance also verified. Exact unit coverage and example checks are recorded in T03.

## Promoted knowledge
No docs/adr, docs/glossary, docs/specs or docs/deferred directories exist; no promotion required. Final sweep found no additional durable artifact owner. README and changelog contain permanent user-facing behavior.

## Planning review history
Round 1: one blocker concerning overlapping root/dependency cascade order. Resolved by specifying first-completed dependency-first traversal, legacy shared files first, and explicit overlapping-root computed-style coverage. Round 2: Findings: none. Complete initial plan coverage and the ordering correction verified.

Implementation evidence requires a plan revision: Bazel header jars omit .css.json resources, so imports cannot read resource sidecars on the compilation classpath. Revised declaration annotations use CLASS retention; sidecars remain the bundler contract. Round 3: Findings: none. Reviewer verified full jars contain CSS sidecars and header jars contain only SharedPulse.class; CLASS-retained declaration contract and testing responsibilities accepted.

## Implementation review history
Round 1: P2 — global documentation omitted the explicit-selector requirement inside nested media/supports conditionals. Packaged-compiler probes confirmed both the accepted selector form and rejected direct-declaration form. Added the README caveat and focused positive/negative compiler and packaged-processor checks. bazel test //css:tests passed all seven suites and 442 packaged processor cases; tools/java_format.sh check and git diff --check passed. No production behavior changed. Round 2: Findings: none. Reviewer verified the README caveat, both accepted/rejected conditional forms, all seven passing CSS suites and 442 processor cases, unchanged production code, clean worktree and diff checks. Residual limitations remain the documented parser subset and conservative component retention.
