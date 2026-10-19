# T02 — J2CL-first root API

- Status: pending
- Blocked by: T01
- Spec coverage: R1; AC1

## Delivers

Five requested root library/plugin labels. Separate private publication jars and J2CL backings support internal checks and public browser libraries; retired public names disappear and repository/external callers migrate atomically. Existing macros remain supported.

## Acceptance criteria

- [ ] Core/DOM/CSS root targets provide browser compilation; both plugins compile generated code. JVM compiler imports remain internal.
- [ ] Retired root suffix/plugin names have no definitions or active callers; CSS package targets may remain internal backings. Generated companions stay on backing targets.
- [ ] No source compilation/checking or packaged-archive evidence is bypassed.

## Validation

Focused JVM/publication/processor suites, J2CL example and asset browser checks; external consumer after updating labels. Buildifier and relevant static checks.

## Evidence

Pending.
