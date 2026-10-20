# Canonical runtime compilation

- Status: complete
- Blocked by: T01
- Spec coverage: R2/AC2

## Delivers

Point React4j and reachable upstream aliases to each library’s existing source-owned J2CL target. Add minimal pinned dependency patches where upstream currently recompiles sources. Remove old duplicate compilation paths. Exercise a real combined link/render consumer.

## Acceptance

- [x] Assigned spec outcome is demonstrably satisfied.
- [x] Validation passes with actual evidence and any independent limits recorded.

## Validation

Module resolution and patch applicability; native runtime builds and JavaScript/config/source-map assertions; combined link/browser checks; existing checked JVM publication-producer reachability/action checks and caller-owned checker negative tests; relevant Root suites. Native source owners retain their current compiler policy; this change does not introduce or impose a new upstream checker wrapper.

## Evidence

Root now aliases Braincheck/Zemeckis directly to their public source-package J2CL targets; minimal pinned Arez/Zemeckis patches replace their Braincheck archive compilations with aliases to that same source owner. Removed the old Root Zemeckis compilation. bazel mod deps --lockfile_mode=update and Buildifier passed with no lockfile diff. Configured deps(//:dom) has exactly three runtime j2cl_library owners: @arez//:core-j2cl, @braincheck//core/src/main/java/org/realityforge/braincheck:core-j2cl and @zemeckis//core/src/main/java/zemeckis:core-j2cl. Native output trees retain original Java, mapped Java origins and native config implementation. Core/DOM browser suites passed in development and production; dependency checks and eight-case packaging browser matrix passed. Aquery of Javac deps(//:core) verifies checked React4j, Braincheck and Zemeckis JVM producers remain, each with NullAway:ERROR. Existing external consumer now explicitly consumes all three public aliases and observes Braincheck/Zemeckis native configuration in both modes; this enhanced full external gate runs under T04. Plain unconfigured deps query traverses an unrelated unavailable J2CL implicit tool branch; configured cquery verifies the actual linked graph.
