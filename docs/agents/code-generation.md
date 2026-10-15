# Code generation

[CONTRIBUTING.md](../../CONTRIBUTING.md) owns generation/update commands. Edit generators and regenerate outputs, including changes caused only by annotations. Keep generated build output outside version control.

## Annotation processor

`processor/` generates enhanced views, builders and factories. `Constants.java` owns integration names; `tools/release/release.bzl` shades JavaPoet and Proton under `react4j.processor.vendor.*` and merges services.

### Current behavior

- For `TRACKING` and `MAYBE_TRACKING` views, render-prelude observation scans subclass-accessible declared/inherited instance fields and zero-argument instance methods annotated with `@ComponentDependency` or `@AutoObserve`. `@Input` and `@CascadeDispose` do not select candidates. Known nullable candidates use `ComponentObservable.maybeObserve(...)`; uncertain types use a runtime `instanceof ComponentObservable` guard.
- Explicit `@View` constructors warn with `React4j:ConstructorParameterOrder` when parameters are not grouped as non-`@Input`, then `@Input(fromTreeContext = true)`, then other `@Input`. The suppressible diagnostic attaches to the constructor and labels the groups `inject`, `tree`, and `input`.
- `@View(exportBuilder = true|false)` alone controls builder API visibility. `false` gives package access to the top-level builder, `newBuilder()`, static entry methods, and step interfaces; `true` makes them public. Public `@View` classes emit the suppressible `React4j:PublicView` warning.
- React's native-view `displayName` trims a trailing `View` from the effective `@View.name`; the descriptor name and builder keys retain it. Builder class naming separately trims the suffix from the Java type name in [`ViewDescriptor`](../../processor/src/main/java/react4j/processor/ViewDescriptor.java).

### Nullness and fixtures

`Nullability.java` resolves declaration scopes and substituted inherited types, including compiled generic bounds, nested/enclosing arguments, wildcards and arrays. Emitted type names must retain type-use annotations. Unmarked injected types stored in generated fields need specified nullness throughout their type tree; generated marked scopes must not silently strengthen unknown contracts.

Javac needs `-XDaddTypeAnnotationsToSymbol=true` to retain compiled JSpecify type annotations. The strict generated-code pass compiles every emitted Java body, including Arez/Sting integration files; golden comparisons maintain the React4j output set in both formatting modes. Keep the deliberate invalid-body NullAway control, no-Sting classpath regression and formatter subprocess export regression.

The golden updater operates in an isolated runfiles/temporary compilation tree. Its baseline comparison preserves unrelated maintained edits and rejects conflicts before copying. Naming changes must account for both `deriveExpectedOutputs()` and dedicated nested/inheritance output names in `React4jProcessorTest.java`.

## DOM and assets

`tools/generation/src/main/java/react4j/generation/DomGenerator.java` and adjacent resource templates own DOM factories. Its deterministic source archive includes the generated factories compiled by the DOM target and distributed in its source archive.

`ReactAssetPatcher.java` transforms the pinned React 16.6.0 UMD inputs to expose React.Element. `MODULE.bazel` owns URLs/checksums. Published source archives include the native companions, externs and UMD assets; binaries contain bytecode and metadata. Keep these boundaries in archive checks when changing resources.
