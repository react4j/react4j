# Code generation

[CONTRIBUTING.md](../../CONTRIBUTING.md) owns generation/update commands. Edit generators and regenerate outputs, including changes caused only by annotations. Keep generated build output outside version control.

## Annotation processor

`processor/` generates enhanced views, builders and factories. `Constants.java` owns integration names; `tools/release/release.bzl` shades JavaPoet and Proton under `react4j.processor.vendor.*` and merges services.

### Current behavior

- Query constructor origins through Proton's `ElementsUtil.getOrigin(...)` for validation, descriptor metadata and parameter-order diagnostics. Proton owns compiler-specific corrections and javac/Turbine regression coverage; React4j retains its normal processor behavior and diagnostic tests.
- For `TRACKING` and `MAYBE_TRACKING` views, render-prelude observation scans subclass-accessible declared/inherited instance fields and zero-argument instance methods annotated with `@ComponentDependency` or `@AutoObserve`. `@Input` and `@CascadeDispose` do not select candidates. Known nullable candidates use `ComponentObservable.maybeObserve(...)`; uncertain types use a runtime `instanceof ComponentObservable` guard.
- Explicit `@View` constructors warn with `React4j:ConstructorParameterOrder` when parameters are not grouped as non-`@Input`, then `@Input(fromTreeContext = true)`, then other `@Input`. The suppressible diagnostic attaches to the constructor and labels the groups `inject`, `tree`, and `input`.
- `@View(exportBuilder = true|false)` alone controls builder API visibility. `false` gives package access to the top-level builder, `newBuilder()`, static entry methods, and step interfaces; `true` makes them public. Public `@View` classes emit the suppressible `React4j:PublicView` warning.
- React's native-view `displayName` trims a trailing `View` from the effective `@View.name`; the descriptor name and builder keys retain it. Builder class naming separately trims the suffix from the Java type name in [`ViewDescriptor`](../../processor/src/main/java/react4j/processor/ViewDescriptor.java).

### Nullness and fixtures

`Nullability.java` resolves declaration scopes and substituted inherited types, including compiled generic bounds, nested/enclosing arguments, wildcards and arrays. Emitted type names must retain type-use annotations. Unmarked injected types stored in generated fields need specified nullness throughout their type tree; generated marked scopes must not silently strengthen unknown contracts.

Input defaults and handlers must match the complete effective type tree, including generic upper bounds, invariant arguments, wildcard bounds, enclosing arguments and array elements. Optional parametric inputs need a valid default or tree-context source even with `require=DISABLE`. Keep nested qualifiers in generated validator locals; root local-variable nullness annotations have no JSpecify meaning.

The pinned javac's `Types.directSupertypes` drops substituted type-use annotations. The contract matcher binds the original declared parent arguments instead. Preserve this behavior in both source and compiled-declaration regressions.

Javac needs `-XDaddTypeAnnotationsToSymbol=true` to retain compiled JSpecify type annotations. The strict generated-code pass compiles every emitted Java body, including Arez/Sting integration files; golden comparisons maintain the React4j output set in both formatting modes. Keep the deliberate invalid-body NullAway control, no-Sting classpath regression and formatter subprocess export regression.

The golden updater operates in an isolated runfiles/temporary compilation tree. Its baseline comparison preserves unrelated maintained edits and rejects conflicts before copying. Naming changes must account for both `deriveExpectedOutputs()` and dedicated nested/inheritance output names in `React4jProcessorTest.java`.

## DOM and assets

`tools/generation/src/main/java/react4j/generation/DomGenerator.java` and adjacent resource templates own DOM factories. Its deterministic source archive includes the generated factories compiled by the DOM target and distributed in its source archive.

`ReactAssetPatcher.java` transforms the pinned React 16.6.0 UMD inputs to expose React.Element. `MODULE.bazel` owns URLs/checksums. Published source archives include the native companions, externs and UMD assets; binaries contain bytecode and metadata. Keep these boundaries in archive checks when changing resources.

## CSS authoring implementation

[`css/README.md`](../../css/README.md) owns the CSS authoring API, current limits and production refinement sequence. The work remains on a draft PR pending production acceptance. Its annotation processor parses and validates CSS in-process with ph-css before emitting static Java bindings and Filer resources; a separate JVM extraction action collects those resources before ordinary checked Java/J2CL compilation. Node and native CSS tools are excluded from this toolchain. `css/core` owns the client annotation; `css/processor` owns the implementation and its assembled shaded jar. Final publication coordinates remain open. The public processor target and extraction adapter use the assembled jar, with ph-css/commons and SLF4J relocated under `react4j.css.processor.vendor`. JSpecify stays a public generated-source annotation; do not relocate that string into the vendor namespace. The processor jar has javac service registration and resolves the annotation by compiler model rather than loading the client class; the extraction rule's `deps` supplies application compilation jars independently of the tool runtime. Keep all jars as declared inputs and use argument files for source/classpath lists.

Check the parser's syntax tree before converting it to the domain model: unsupported nested rules can otherwise disappear during conversion. Keep selector/accessor validation on parsed selector nodes. Retain the original Java token and javac's cooked text-block value together: indentation stripping, Unicode/Java escapes and continued lines make a fixed line/column offset insufficient for diagnostics.

CSS fixtures live under `css/processor/src/test/fixtures/{input,bad_input,expected}`. Input fixtures use ordinary imports except where qualification exercises name shadowing. The formatter checks their explicit `:inputs` filegroup; keep generated goldens outside that group so only the generator changes expected Java. Keep complete Java/resource output sets and exact diagnostic text (normalizing only the temporary compilation root). The packaged processor path contains one shaded jar; the application classpath contains only core/JSpecify. `tools/update_goldens.sh css` uses the same isolated updater with a single expected tree. Successful fixtures pass `StrictGeneratedCode`; retain its deliberately invalid-body control. Default-package owners require a narrow generated `DefaultPackage` suppression to pass the full policy.

CSS compiler and integration tests should query the existing ph-css parsed model for semantic assertions; avoid stylesheet substring checks. Keep complete exact-output goldens as the separate serialization/reproducibility contract. Test-only parser dependencies must stay off the application binding classpath. The annotation J2CL target is a bytecode import of the checked JVM annotation target.

Query model fields and rule counts rather than comparing whole model objects: ph-css 8.2.3 `CSSLayerRule.equals` compares only the class. Its parser also rejects empty layer blocks; preserve the layer-order declaration and omit empty bodies when bundling.
