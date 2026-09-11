# T03 — Remove the repository dependency

- Status: `pending`
- Blocked by: `T02`
- Spec coverage: `R4`, `R5`, `AC5`, `D9`

## Delivers

React4j builds, packages, and compiles its examples without Akasha, and neither core nor DOM publishes or inherits the dependency.

## Acceptance criteria

- [ ] BrowserLocation uses only minimal private local browser facades.
- [ ] HelloWorld uses a package-local WindowGlobal binding.
- [ ] Akasha is absent from `build.yaml`, `CORE_DEPS`, and React's GWT inheritance.
- [ ] No remaining dependency reintroduces Akasha transitively.
- [ ] Core, DOM, processor, and doc examples compile/package without Akasha.
- [ ] Generated core and DOM POMs contain no Akasha dependency.

## Validation

- `bundle exec buildr clean package` with downstream work disabled — proves clean Java/GWT/example/package construction.
- Generated-POM inspection and active-reference `rg` checks — prove dependency and source absence.

## Evidence

- `pending`
