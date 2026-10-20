# Canonical runtime compilation

- Status: pending
- Blocked by: T01
- Spec coverage: R2/AC2

## Delivers

Point React4j and reachable upstream aliases to each library’s existing source-owned J2CL target. Add minimal pinned dependency patches where upstream currently recompiles sources. Remove old duplicate compilation paths. Exercise a real combined link/render consumer.

## Acceptance

- [ ] Assigned spec outcome is demonstrably satisfied.
- [ ] Validation passes with actual evidence and any independent limits recorded.

## Validation

Module resolution and patch applicability; native runtime builds and JavaScript/config/source-map assertions; combined link/browser checks; existing checked JVM publication-producer reachability/action checks and caller-owned checker negative tests; relevant Root suites. Native source owners retain their current compiler policy; this change does not introduce or impose a new upstream checker wrapper.

## Evidence

Pending.
