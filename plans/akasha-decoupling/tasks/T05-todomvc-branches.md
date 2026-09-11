# T05 — Migrate TodoMVC upgrade branches

- Status: `done`
- Blocked by: `T04`
- Spec coverage: `R7`, `R9`, `AC8`, `D7`, `D8`

## Delivers

All six configured TodoMVC variants compile against React4j `0.229` without any Akasha dependency or source usage, from pushed temporary release branches.

## Acceptance criteria

- [x] Each variant replaces root lookup, input value access, and hash routing with minimal local JsInterop types.
- [x] Each variant contains no Akasha dependency, GWT inherit, import, or active source reference.
- [x] Each variant compiles against the local React4j `0.229` artifacts before push.
- [x] One focused commit is pushed to each approved `*-React4jUpgrade-0.229` branch.
- [x] No permanent branch or pull request is changed.

## Validation

- Per-variant Buildr/Maven/J2CL build command used by the downstream harness — proves each migrated application compiles.
- Local and remote branch/reference inspection — proves zero Akasha use and exact branch delivery.

## Evidence

- Installed the React4j `0.229` artifacts locally and built all six variants before pushing. The Buildr variants (`raw`, `arez`, `spritz`, and `sting`) passed `bundle exec buildr clean package`; `sting_maven` and `sting_maven_j2cl` passed `./mvnw clean package`.
- The J2CL build uses released `j2cl-maven-plugin` `0.21.0`, a compiler-only two-annotation JSpecify compatibility jar assembled during the Maven lifecycle, and the standard browser extern environment. Its Java compilation, J2CL transpilation, Closure ADVANCED link, and WAR packaging all completed successfully on JDK 17.
- React4j's `react4j.js` now uses the legacy Closure-compatible statement form of `goog.require('jre')`; the behavior-equivalent change was required by the released J2CL compiler and was verified by both the J2CL production link and `BUILD_STATS=no DOWNSTREAM=no bundle exec buildr clean package` (12 core tests and 468 processor tests passed, with the GWT permutations linking successfully).
- `./mvnw dependency:tree -Dincludes=org.realityforge.akasha:akasha,org.realityforge.akasha:akasha-j2cl`, source scans, and remote-tree scans found no Akasha dependency or reference in any of the six upgrade branches.
- Pushed exactly one commit above each unchanged source branch:
  - `raw-React4jUpgrade-0.229` — `ea6d54bd`
  - `arez-React4jUpgrade-0.229` — `62fc5f1c`
  - `spritz-React4jUpgrade-0.229` — `3bb2e1e3`
  - `sting-React4jUpgrade-0.229` — `65691f8e`
  - `sting_maven-React4jUpgrade-0.229` — `990b811f`
  - `sting_maven_j2cl-React4jUpgrade-0.229` — `cce3b265`
- No permanent branch was pushed and no pull request was created.
