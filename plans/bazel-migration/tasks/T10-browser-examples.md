# T10 — J2CL examples and browser verification

- Status: `complete`
- Blocked by: `T09`
- Spec coverage: `R7`, `R9`, `AC5`

## Delivers

Migrate retained documentation examples/entrypoints from GWT to J2CL. Compile all examples, provide local preview, and exercise meaningful React render/update/context behavior with pinned Chromium in development and optimized production.

After the permanent browser tests prove packaged-library/configuration behavior in both modes, remove T09's temporary compile-smoke Java/JS targets and any helper wiring with no continuing use (user AGENTS.md correction recorded in TASKMAP.md).

## Acceptance criteria

- [x] All retained examples compile; pinned-browser tests pass in both modes; no GWT entrypoint/bootstrap/super-source remains; example asset outputs are usable by the site.

## Validation

- Bazel example JVM/J2CL builds; development and production browser tests; preview help/build checks — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `bazel build //doc-examples:java //doc-examples:j2cl //doc-examples:hello_world`: all 25 retained authored Java files plus JSpecify package scopes and two permanent browser contract fixture classes compile with generated React4j/Arez bodies and full static policy. Removed legacy nullness/redundant guards and GWT entrypoint/XML; published core/DOM archives are the only product inputs.
- `bazel test //:browser_tests` and `bazel test -c opt //:browser_tests`: pinned Chromium/ChromeDriver 123, three real browser tests per mode, all pass. Tests render Hello World, mount generated stateful views, update required nullable input and nullable tree-context values while preserving the DOM node, unmount, and assert all five mode flags. Production uses ADVANCED optimization; development uses BUNDLE plus explicit runtime defines. React/ReactDOM remain 16.6.0 and load before compiled code.
- Optimized compilation exposed missing createContext argument and DOM container extern mismatches; corrected signatures to match the existing Java API. Full-policy compilation and runtime tests pass without check suppression.
- Retired T09 temporary CompileSmoke Java/JS, JVM/source/J2CL/entry/bundle targets and macro after permanent coverage passed. Retained browser test fixtures protect continuing generated-view/nullness/context contracts.
- `tools/examples.sh --help`, development/production build, `bazel run //tools/browser:preview -- --help`, and local serve verified. HTTP GET for index/app/React/ReactDOM all returned 200. Preview outputs copy assets so its path-boundary check accepts declared bundle files. Bash 3 empty-array issue fixed using explicit fastbuild mode. Buildifier, full Java format check and archive checks pass.
