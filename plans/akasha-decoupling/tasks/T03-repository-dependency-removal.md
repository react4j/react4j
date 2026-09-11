# T03 — Remove the repository dependency

- Status: `done`
- Blocked by: `T02`
- Spec coverage: `R4`, `R5`, `AC5`, `D9`

## Delivers

React4j builds, packages, and compiles its examples without Akasha, and neither core nor DOM publishes or inherits the dependency.

## Acceptance criteria

- [x] BrowserLocation uses only minimal private local browser facades.
- [x] HelloWorld uses a package-local WindowGlobal binding.
- [x] Akasha is absent from `build.yaml`, `CORE_DEPS`, and React's GWT inheritance.
- [x] No remaining dependency reintroduces Akasha transitively.
- [x] Core, DOM, processor, and doc examples compile/package without Akasha.
- [x] Generated core and DOM POMs contain no Akasha dependency.

## Validation

- `bundle exec buildr clean package` with downstream work disabled — proves clean Java/GWT/example/package construction.
- Generated-POM inspection and active-reference `rg` checks — prove dependency and source absence.

## Evidence

- `BUILD_STATS=no DOWNSTREAM=no bundle exec buildr clean package` passed from a clean tree: core `12/12`, DOM `1/1`, processor `468/468`, both core and DOM GWT permutations, all documentation examples, Javadocs, and artifact packaging.
- The clean compile and Javadoc classpaths contained no Akasha artifact; direct inspection of `target/react4j_core/*.pom` and `target/react4j_dom/*.pom` found no Akasha dependency.
- `rg -n -i 'akasha' . --glob '!target/**' --glob '!plans/**' --glob '!CHANGELOG.md' --glob '!api-test/src/test/resources/fixtures/**' --glob '!**/.git/**'` returned no active repository references.
- `git diff --check` passed.
- An initial `DOWNSTREAM=no bundle exec buildr clean package` invocation reached the historical downstream-statistics hook because that hook has a separate switch. It passed every React4j module and documentation example, then failed in the unmigrated WebSpeechDemo boundary casts; the corrected validation above also set `BUILD_STATS=no`. T05-T07 migrate and revalidate those downstream consumers.
