# T01 — Unified resource imports

Status: complete. Blocked by: None. Coverage: R1, R3; AC1, AC3.

Generalize CssImport to infer all three declared kinds, make names conditional by kind, retain Css annotations across headers, and retire CssIncludes everywhere. Introduce only the compiler/import binding data needed for checked peer kinds; selector use itself is T02. Preserve existing global ordering and animation behavior. Validate kind/alias/placement/type/literal matrices, full/header-jar imports, emitted dependencies and representative paired goldens. Run CSS suites and formatting.

Evidence: tools/update_goldens.sh css passed (444 processor cases, four regenerated files); bazel test //css:tests --test_output=errors passed all seven targets, including separate J2CL integration; bazel build //:java_format_check passed. Full/header imports cover all declaration kinds; peer aliases remain unused until T02. rg CssIncludes css CHANGELOG.md found no remaining references; git diff --check passed.
