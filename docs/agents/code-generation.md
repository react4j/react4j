# Code generation

Change generators and regenerate outputs rather than hand-editing `core/generated`, `dom/generated`, or generated processor fixture outputs. Keep local build output (`target/`, `reports/`, `node_modules/`) out of changes.

## Annotation processor

`processor/` processes `@react4j.annotations.View` and generates `React4j_*` classes, builders, and factories. Arez/Sting integration constants are in [`Constants.java`](../../processor/src/main/java/react4j/processor/Constants.java); [`buildfile`](../../buildfile) shades JavaPoet and Proton under `react4j.processor.vendor.*`.

### Current behavior

- For `TRACKING` and `MAYBE_TRACKING` views, render-prelude observation scans subclass-accessible declared/inherited instance fields and zero-argument instance methods annotated with `@ComponentDependency` or `@AutoObserve`. `@Input` and `@CascadeDispose` do not select candidates. Known nullable candidates use `ComponentObservable.maybeObserve(...)`; uncertain types use a runtime `instanceof ComponentObservable` guard.
- Explicit `@View` constructors warn with `React4j:ConstructorParameterOrder` when parameters are not grouped as non-`@Input`, then `@Input(fromTreeContext = true)`, then other `@Input`. The suppressible diagnostic attaches to the constructor and labels the groups `inject`, `tree`, and `input`.
- `@View(exportBuilder = true|false)` alone controls builder API visibility. `false` gives package access to the top-level builder, `newBuilder()`, static entry methods, and step interfaces; `true` makes them public. Public `@View` classes emit the suppressible `React4j:PublicView` warning.
- React's native-view `displayName` trims a trailing `View` from the effective `@View.name`; the descriptor name and builder keys retain it. Builder class naming separately trims the suffix from the Java type name in [`ViewDescriptor`](../../processor/src/main/java/react4j/processor/ViewDescriptor.java).

### Fixtures

Fixtures live under `processor/src/test/fixtures`: `input`, `bad_input`, `expected`, and `expectedFormatted`. When generator behavior changes, regenerate affected expected outputs, including for annotation-only changes. The test harness uses `react4j.fixture_dir` and `react4j.output_fixture_data`; [`buildfile`](../../buildfile) includes an IntelliJ fixture-generation configuration.

Builder output expectations are split between `React4jProcessorTest.deriveExpectedOutputs()` and dedicated nested/inheritance tests with hardcoded filenames in [`React4jProcessorTest.java`](../../processor/src/test/java/react4j/processor/React4jProcessorTest.java). Naming changes must account for both paths.

## DOM factories

Edit [`tasks/react_dom.rake`](../../tasks/react_dom.rake), then run `bundle exec buildr react4j:dom:reactgen` to regenerate `dom/generated/reactgen/main/java/react4j/dom/DOM.java`.

## Bundled JavaScript

[`tasks/js_asset.rake`](../../tasks/js_asset.rake) downloads React/ReactDOM into `core/generated/js_assets` and `dom/generated/js_assets` and patches React to expose `React.Element`. Change asset versions and patches in that task; regenerate with `bundle exec buildr react4j:core:js_resources react4j:dom:js_resources`.
