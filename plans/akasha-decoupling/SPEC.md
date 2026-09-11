# Akasha Decoupling Spec

## Source

- Shared understanding: completed `$grill-me` conversation ending with explicit approval to proceed using the structured delivery workflow.
- Repository evidence: `build.yaml`, `buildfile`, core and DOM public APIs, processor fixtures, doc examples, downstream build tooling, generated Maven POMs, and all configured downstream repositories and branches.

## Problem

React4j core and DOM publish Akasha as a direct dependency and inherit the complete Akasha GWT module even though React4j uses only a small number of browser types. Downstream GWT and J2CL builds consequently compile and link roughly 1,850 Akasha source types, creating substantial compiler work with little value and forcing consumers to adopt React4j's browser-binding choice.

## Required outcome

React4j publishes browser-binding-neutral core and DOM artifacts with no live Akasha dependency. Its public API retains useful semantic distinctions through a minimal set of React4j-owned opaque JsInterop types, while downstream applications either remove Akasha when their usage is trivial or declare and retain it explicitly when they genuinely use broad browser APIs. Every configured release-size build compiles from a temporary React4j `0.229` upgrade branch and updated size/API evidence is recorded.

## Scope

- In scope: React4j core, DOM, processor output and fixtures, GWT modules, doc examples, generated API-diff and build-size fixtures, and all branches/repositories listed in `DOWNSTREAM_EXAMPLES`.
- In scope: ten new temporary downstream branches following the existing `*-React4jUpgrade-0.229` release convention, each committed and pushed without a pull request.
- Out of scope: VChat and every repository or branch not selected by `DOWNSTREAM_EXAMPLES`.
- Out of scope: migrating broad application-owned Web Audio, networking, Bluetooth, speech, WebRTC, or other browser APIs away from Akasha.
- Out of scope: changing immutable historical changelog entries or historical API-diff fixtures merely because they contain Akasha names.
- Out of scope: deprecated Akasha overloads, adapters, split-package shims, or a new general-purpose browser API.

## Constraints

- Remove Akasha completely from live React4j source, build dependencies, GWT inheritance, generated processor output, and published core/DOM POMs.
- Preserve runtime behavior, nullability, event wrapper behavior, and production/development configuration except for the explicitly accepted timestamp type correction.
- Keep replacement JsInterop facades deliberately opaque and document `Js.uncheckedCast` as the interoperability mechanism.
- Do not hand-edit ignored generated outputs. Regenerate processor golden fixtures through the supported fixture workflow.
- Keep permanent downstream branches unchanged. Push only new temporary upgrade branches using the established release naming convention.
- The structured workflow's plan and implementation commits supersede the earlier request to leave React4j uncommitted. Do not push the React4j branch unless separately requested.

## Requirements

- `R1`: Core must expose no Akasha types or dependency while preserving its supported behavior through neutral APIs.
- `R2`: DOM must expose no Akasha types or dependency while retaining meaningful event and target type distinctions.
- `R3`: Processor-generated source must contain no Akasha imports or fully qualified names.
- `R4`: In-repository examples must compile without Akasha by using only minimal local browser bindings.
- `R5`: React4j build metadata, GWT modules, dependency rules, and published metadata must not reference Akasha as a live dependency.
- `R6`: Breaking API changes and their browser-binding migration path must be documented and represented in the `0.228` to `0.229` API-diff fixture.
- `R7`: Every TodoMVC release branch must remove Akasha completely and use local JsInterop bindings for its small browser surface.
- `R8`: The four configured applications with broad browser use must retain Akasha explicitly and migrate only their React4j boundary calls.
- `R9`: The ten downstream migrations must be committed and pushed only to the approved temporary upgrade branches, without pull requests or permanent-branch changes.
- `R10`: Release-size statistics for `0.229` must be regenerated from the migrated branches and every configured downstream build must pass.
- `R11`: Historical records must remain intact, and no unrelated repositories, branches, refactors, or compatibility layers may enter the change.

## Acceptance criteria

- `AC1` (`R1`, `R3`): Core, processor source, and newly generated source contain no live `akasha` imports or fully qualified names; `ReactNode.of(JsArray<ReactNode>)` is absent and `@OnError` uses `react4j.JsError`.
- `AC2` (`R2`): DOM public signatures use `Object` for render/root/portal/unmount containers and React4j-owned `Event`, `EventTarget`, `Element`, `Document`, `DataTransfer`, and `TouchList` facades for event outputs.
- `AC3` (`R2`): `Element` and `Document` extend `EventTarget`; concrete wrappers use the neutral `Event`; `SyntheticEvent<E extends Event>` remains generic.
- `AC4` (`R2`): `SyntheticEvent.getTimeStamp()` is `double` and the bundled Closure extern declares `timeStamp` as `number`, matching ReactDOM 16.6.0.
- `AC5` (`R4`, `R5`): `BrowserLocation` uses private minimal global bindings, HelloWorld uses a package-local `WindowGlobal`, React's GWT module no longer inherits Akasha, and both generated Maven POMs omit Akasha.
- `AC6` (`R3`): All processor fixtures are regenerated and processor tests pass without Akasha on their generated-source contract.
- `AC7` (`R6`): `CHANGELOG.md` contains a current `Breaking:` migration entry and `api-test/src/test/resources/fixtures/0.228-0.229.json` records the accepted API changes.
- `AC8` (`R7`, `R9`): All six TodoMVC upgrade branches exist remotely, compile, and contain no Akasha dependency, GWT inherit, import, or source reference.
- `AC9` (`R8`, `R9`): The four master-based upgrade branches exist remotely, compile, retain an explicit Akasha dependency where required, and no longer rely on React4j for GWT inheritance or API types.
- `AC10` (`R10`): The complete downstream release/statistics workflow passes and the checked-in `0.229` size values reflect the migrated outputs.
- `AC11` (`R11`): React4j and downstream diffs contain only approved changes; permanent downstream branches and historical records remain unchanged.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| `D1` | Make a clean break with no Akasha compatibility API. | Any real compatibility shim retains the dependency or creates class conflicts. | Binary and source API changes are explicit in `0.229`. | Confirm no deprecated Akasha overloads or adapters exist. |
| `D2` | Use `Object` for ReactDOM container inputs and `ReactPortal.containerInfo`. | React4j only passes these objects through; `Object` preserves ordinary call-site source compatibility across browser bindings. | Consumers cast returned portal container information when a specific API is needed. | Confirm ReactDOM accepts any browser library's element object directly. |
| `D3` | Add opaque React4j-owned event facades and `react4j.JsError`. | Event APIs retain useful semantic types without adopting another browser library. | Consumers use `Js.uncheckedCast` for browser-specific members. | Confirm public Javadocs identify the intended cast boundary. |
| `D4` | Collapse concrete native event parameters to one neutral `Event` while retaining `SyntheticEvent<E extends Event>`. | Eight otherwise-empty native-event marker classes add API without capability. | Native-event-specific access requires an unchecked cast. | Confirm event wrappers and custom subclass generic use remain coherent. |
| `D5` | Correct timestamp from `JsDate`/`Date` to `double`/`number`. | Vendored ReactDOM 16.6.0 stores `event.timeStamp || Date.now()`, both JavaScript numbers. | This intentionally corrects an existing public type error. | Confirm Java and extern declarations agree with the vendored runtime. |
| `D6` | Remove the public `JsArray` overload and use existing arrays/varargs in generated builders. | No downstream use was found and the varargs API already represents the behavior. | Callers of the removed overload migrate to `ReactNode.of(ReactNode...)`. | Confirm generated builders contain no Akasha array call. |
| `D7` | Remove Akasha entirely from every TodoMVC branch but retain it in broad-browser applications. | TodoMVC has only five trivial touchpoints; the other applications genuinely use extensive browser APIs. | Release builds remain representative without forcing unrelated rewrites. | Confirm dependency absence/presence matches the approved repository matrix. |
| `D8` | Use ten temporary `*-React4jUpgrade-0.229` branches. | This is the release tool's existing resume/build/cleanup protocol and preserves statistic keys. | New branches are pushed; permanent branches and PR state do not change. | Confirm the exact remote branch list and absence of permanent-branch commits. |
| `D9` | Use existing structural checks rather than add a generated-POM test harness. | JDepend, generated source checks, direct POM inspection, and actual downstream builds cover the important seams. | No new test abstraction is introduced. | Confirm both generated POMs and all downstream builds omit accidental transitive reliance. |

## Technical decisions

- `react4j.JsError` is a public opaque native binding for the global JavaScript `Error` type.
- `react4j.dom.events.Event`, `EventTarget`, `Element`, `Document`, `DataTransfer`, and `TouchList` are public opaque native bindings in the event package. `Element` and `Document` extend `EventTarget`; they expose no browser convenience methods.
- Event wrapper names remain unchanged. Their browser-native generic parameter becomes the neutral React4j `Event`.
- `ReactDOM` private native declarations and public methods use `Object` for container values; `ReactPortal.containerInfo` is `Object`.
- Processor child generation uses Java arrays or existing `ReactNode` varargs behavior and processor error generation uses `react4j.JsError`.
- BrowserLocation follows the proven Arez pattern: private `@JsFunction` listener, private native event/location/history/document facades, and direct global functions/properties containing only used members.
- HelloWorld uses a separate package-local `WindowGlobal` and package-local nested `Document` facade so the example snippet remains small.
- Remove the Akasha artifact from `build.yaml` and `CORE_DEPS`, remove `akasha.Akasha` GWT inheritance, retain lower-level JsInterop dependencies, and adjust dependency tests.
- Keep existing Akasha versions in broad-browser downstream applications. Flux Challenge gains its own explicit GWT Akasha inherit.
- Push exactly six TodoMVC and four master-based upgrade branches, each with one focused migration commit and no PR.

## Testing decisions

- Run focused core/DOM/processor tests during each React4j slice, including GWT compilation.
- Regenerate processor golden outputs through the fixture output flag, then run the full processor suite.
- Generate and test the `0.228` to `0.229` API diff.
- Package core and DOM and inspect generated POMs for zero Akasha dependency.
- Check active source/generated contracts for zero Akasha references while explicitly excluding immutable historical records.
- Compile every downstream upgrade branch before pushing and then run the actual downstream release/statistics workflow against the pushed branches.
- Refresh the `0.229` statistics fixture and run the strongest complete React4j test/build gate.
- Inspect all diffs and remote branch tips before implementation review.

## Open questions

None.
