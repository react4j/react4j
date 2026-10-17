# T01 — Unified resource imports

Status: pending. Blocked by: None. Coverage: R1, R3; AC1, AC3.

Generalize CssImport to infer all three declared kinds, make names conditional by kind, retain Css annotations across headers, and retire CssIncludes everywhere. Introduce only the compiler/import binding data needed for checked peer kinds; selector use itself is T02. Preserve existing global ordering and animation behavior. Validate kind/alias/placement/type/literal matrices, full/header-jar imports, emitted dependencies and representative paired goldens. Run CSS suites and formatting.

Evidence: pending.
