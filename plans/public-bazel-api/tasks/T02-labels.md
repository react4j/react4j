# T02 — J2CL-first root API

- Status: complete
- Blocked by: T01
- Spec coverage: R1; AC1

## Delivers

Five requested root library/plugin labels. Separate private publication jars and J2CL backings support internal checks and public browser libraries; retired public names disappear and repository/external callers migrate atomically. Existing macros remain supported.

## Acceptance criteria

- [x] Core/DOM/CSS root targets provide browser compilation; both plugins compile generated code. JVM compiler imports remain internal.
- [x] Retired root suffix/plugin names have no definitions or active callers; CSS package targets may remain internal backings. Generated companions stay on backing targets.
- [x] No source compilation/checking or packaged-archive evidence is bypassed.

## Validation

Focused JVM/publication/processor suites, J2CL example and asset browser checks; external consumer after updating labels. Buildifier and relevant static checks.

## Evidence

- Fourteen focused core/DOM/processor/publication suites pass; CSS browser suite passes in fastbuild and opt.
- External native J2CL consumer passes with all five requested labels in both modes, including selected-check failures.
- Root query reports four browser/plugin aliases and the processor plugin; old root names have no build callers. Private backing visibility prevents generated platform companions from becoming supported entry points.
- Buildifier, shell syntax and diff checks pass.
