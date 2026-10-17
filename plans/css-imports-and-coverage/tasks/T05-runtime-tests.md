# T05 — Core and DOM coverage to 95/85

Status: pending. Blocked by: T04. Coverage: R4; AC4.

Add meaningful real-browser runtime tests for core element/context/node/view state/introspection/scheduler behavior and DOM prop/style/event roundtrips, rendering and batching, including null/removal/error/flag variations. Test the public fluent property contract systematically without replacing production code or fabricating hits. Add JVM-only config tests and verify their counters contribute to the enforced mixed-platform root totals. Raise measured runtime roots to 95/85 with all owned modules present; verify root/class floors and denominator controls.

Evidence: pending.

Runtime line floors use the complete independent compilation line inventory. Missing browser map origins remain uncovered. If meaningful tests cannot satisfy 95/85 with conservative, safely attributed evidence, return to the measurement decision with concrete residual lines; do not infer neighboring hits, shrink the denominator or lower floors.
