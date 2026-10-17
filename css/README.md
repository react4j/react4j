# CSS-in-Java — work in progress

This branch is being refined from a feasibility spike into a production implementation of static CSS extraction with checked Java style accessors. It remains a draft and is not ready to merge or publish. Parsing, validation and output run inside the JVM; the CSS toolchain does not require Node, npm or native executables. CSS code lives under `react4j.css`; the product name, repository and publication coordinates remain provisional. The annotation and processor targets are separate so the parser dependencies do not enter the application annotation classpath.

WorldBuilder's [authoring architecture](https://github.com/realityforge/WorldBuilder/blob/ad52a87/docs/adr/0020-extract-static-client-styles.md) and [separate-product boundary](https://github.com/realityforge/WorldBuilder/blob/ad52a87/docs/adr/0021-consume-a-separate-style-generator.md) provide the consumer context. The JVM-only requirement supersedes that experiment's Node-based tool selection. This proof does not migrate WorldBuilder or publish a release.

## Layout

- `core/`: client annotation and JSpecify package contract.
- `processor/`: JVM parser, processor, extraction adapter, unit tests and `src/test/fixtures/{input,bad_input,expected}` goldens.
- `examples/save-button/`: checked React4j and browser example.
- `integration-tests/`: application dependency and extraction integration.

Java libraries and tests are declared beside their package sources in `src/main/java` and `src/test/java`. The browser test lives in `examples/save-button/src/test/js/react4j/css/example`; the shared Chromium configuration lives in `tools/webtesting` at the repository root. BUILD files enumerate sources and fixture data explicitly. Extraction actions live beside their Java inputs. Subproject roots expose public module handles and assembled artifacts.

The example’s [shared.css](examples/save-button/src/main/css/shared.css) is source: the JVM action parses it and combines it into the shared cascade layer. `src/main/assets` is reserved for assets copied unchanged. The browser-ready `styles.css` is a generated build output.

## Authoring and generated API

See [SaveButton.java](examples/save-button/src/main/java/react4j/css/example/SaveButton.java) and [BrowserExample.java](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) for the complete checked example. A style definition is an accessible abstract class with an accessible no-argument constructor, `abstract String style()`, and zero-argument abstract `String` modifier accessors. A literal `@Css` text block supplies root declarations and nested same-element rules such as `&:focus-visible` and `&.success`. Java interpolation, constant concatenation and runtime stylesheet generation are unsupported. Static nested definition classes are supported; inheritance and generic style definitions are outside this proof.

The processor generates a package-access `Styled_SaveButton` subclass. `style()` returns the root class token; `success()` returns a space-separated root and namespaced success token. The factory remains ordinary author-written Java. The modifier methods select static classes and allocate no CSS. Applying conflicting modifiers follows authored CSS order, independently of class-token order. Fragment independence assumes that an element uses one component owner; the String API does not prevent callers from combining roots from different owners.

Names derive from the Java binary owner name, including package and enclosing classes. ASCII letters and digits remain readable, package dots become single hyphens, and all other UTF-16 code units become `_hhhh_`. The root has a `jcss-` prefix and modifier names follow `--`. Because Java owner names contain no hyphens and literal underscores are escaped, separator and escape sequences cannot be confused. Generated subclass names use the encoded local binary name. Names do not depend on content, compilation order or checkout paths. Name shortening is outside this proof.

The parsed selector tree must agree with the declared accessors in both directions. Comments, property values and quoted strings do not declare modifiers. State pseudo-classes need no accessor. Functional pseudos are limited to `:is`, `:not` and `:where`. Component selectors must begin with `&` and stay on the owned element; descendant/sibling reach, global escapes, `:has`, escaped selector identifiers, component keyframes and global at-rules are diagnosed. Conditional `@media` and `@supports` rules containing explicit nested selectors are supported. Bare declarations inside those conditional rules and `@container` are outside this parser integration's supported subset and fail compilation.

## Build and parser

```sh
bazel build //css/examples/save-button/src/main/java/react4j/css/example:styles //css/examples/save-button/src/main/java/react4j/css/example:bindings
bazel test //css:tests //css/examples/save-button/src/test/js/react4j/css/example:browser_tests --test_output=errors
bazel test -c opt //css/examples/save-button/src/test/js/react4j/css/example:browser_tests --test_output=errors
css/check_reproducibility.sh
```

The annotation has one checked JVM compilation and a bytecode import for J2CL:

| Target | Consumer |
| --- | --- |
| `//css/core` | JVM extraction and javac annotation classpaths. |
| `//css/core:core-j2cl` | Browser application dependencies. It imports the same checked annotation bytecode and generates no annotation JavaScript. |
| `//css/processor` | Build tools and javac processor paths; the standalone shaded processor. |

A browser app can depend on `core-j2cl` without declaring a `java_library`. The `styles` action supplies the JVM annotation dependency itself. The repository’s `java_j2cl_library` macro also creates a JVM target for strict checking before J2CL compilation; the example uses that existing convention. Application types referenced during extraction currently need JVM compilation targets in `styles.deps`; with the macro, use its `<name>_java` target. Accepting bare J2CL targets there remains compiler-integration work.

`styles` runs the annotation processor through a declared Java compiler action before ordinary checked Java/J2CL compilation. It emits a deterministic generated source archive and external compact CSS. CSS resources are emitted through Java's Filer, then collected without relying on J2CL to retain auxiliary resources. The `styles` rule accepts Java dependencies through `deps`; their transitive compilation jars and the annotation library form the extraction classpath. Processor/parser jars stay on the tool runtime classpath. Sources, shared files and classpath jars are declared action inputs, and a multiline argument file keeps large source sets off the host command line. The integration fixture compiles a style definition that references a type from a separate Java library, then checks its generated methods and CSS.

The processor calls [ph-css 8.2.3](https://github.com/phax/ph-css/tree/ph-css-parent-pom-8.2.3) in-process. Its JavaCC parser supplies the syntax and selector trees. Recovery is disabled and unsupported rule types are rejected in the syntax tree before conversion to the higher-level model, which can otherwise omit unsupported nested rules. Selector nodes are checked and namespaced, nesting is flattened while preserving declaration order and selector-list specificity, and the library's compact writer serializes declaration values. Shared styles precede component rules through explicit `shared, components` cascade layers. The order declaration is always emitted; empty layer bodies are omitted (ph-css rejects empty layer blocks). `!important` is rejected in both inputs because it reverses layer precedence. Shared inputs support style rules, `@media` and `@supports`; imports, author layers and other at-rules are outside this proof.

The dependency graph is checksum-pinned through `third_party/java/dependencies.yml` and the generated Bazel declarations. The implementation compilation graph includes ph-css, nine ph-commons modules, SLF4J API and JSpecify annotations. The parser and logging libraries are JVM build dependencies and do not enter generated bindings or browser output. The annotation, processor, examples and integration tests now have separate source roots. The public `//css/processor` target imports the assembled `css-processor.jar`; its dependency graph exposes no parser libraries. The extraction adapter also runs from this jar. The shared deterministic archive builder collects the implementation runtime closure and relocates ph-css/commons and SLF4J under `react4j.css.processor.vendor`. JSpecify annotation classes are excluded from the processor jar, while generated source keeps its public JSpecify annotation reference.

The annotation processor reads javac's cooked text-block value and parses the original token to retain per-character Java offsets. The mapper handles incidental indentation, trailing whitespace, CRLF, Java escapes, octal escapes, Unicode escapes and continued lines, and checks its result against javac before reporting CSS locations. Diagnostics include the original Java path, line and column; javac's attached caret covers the annotation value. This uses javac's public tree API and has not been verified under Turbine. A direct Bazel `java_plugin` integration is not yet supplied; the extraction action still runs before checked Java/J2CL compilation.

## Dynamic properties

React style props are objects; the [React renderer](https://github.com/facebook/react/blob/v16.6.0/packages/react-dom/src/shared/CSSPropertyOperations.js) applies `--*` entries using the DOM style object’s `setProperty` method. React4j now exposes this through the existing prop APIs:

```java
var custom = new CssProps().customProperty("--success", "#04c");
var props = new BtnProps().data("shared", "true").style(custom);
```

`customProperty` takes the full CSS name, including `--`. `data` takes the attribute suffix, so `"shared"` writes `data-shared`. Both take strings and accept null to remove a previously rendered value. An empty data string retains an empty attribute. Supply custom-property values as CSS text, including any units. Callers need no `Js.asAny` or property-map casts. Browser tests exercise setting, updates and removal on retained DOM nodes in both compilation modes.

## Processor fixtures

`bazel test //css/processor/src/test/java/react4j/css/processor:processor_tests --test_output=errors` discovers the assembled processor jar with a client classpath containing only core and JSpecify. Each fixture case compiles independently. The harness compares the complete emitted Java/non-class resource set and exact contents, plus normalized diagnostics (including Java source locations). It strips only the temporary directory prefix. Every successful compile also checks generated bodies with the repository’s full Error Prone/JSpecify-mode NullAway policy; a deliberate invalid return verifies failure detection.

Fixtures cover basic modifiers, functional selectors, conditional rules and specificity, declaration order, naming collisions, nested owners, shadowed Java names, default-package definitions and text-block escapes. Invalid cases preserve Java contract failures, selector mismatches/reach, unsupported syntax and source diagnostic locations. Default-package generated classes suppress only the unavoidable `DefaultPackage` warning in addition to the fully qualified name suppression.

Use `tools/update_goldens.sh css` after editing the generator or fixtures. It generates in a temporary copy, checks a baseline for concurrent edits, copies changed expected bytes and reruns the tests. For a new emitted file, declare its expected path first (an empty file is sufficient for the updater); explicitly remove an obsolete expected path. Review additions/removals and diagnostics in the diff. There is one generated output mode. New fixture files must also be declared in the fixture BUILD file. Input sources belong in its `inputs` filegroup and pass the same Java formatter as implementation sources; generated goldens remain exact processor output. Semantic compiler/integration assertions query ph-css model nodes (selectors, declarations, conditions and layers); goldens separately check exact generated text.

## Evidence and remaining product work

The processor jar registers `react4j.css.processor.CssProcessor` through `META-INF/services/javax.annotation.processing.Processor`. Conventional javac users place the core annotation library, JSpecify and their application dependencies on `--class-path`, and only `css-processor.jar` on `--processor-path`. The processor resolves the annotation through javac’s model and does not load the client annotation class. The tests load the packaged processor in isolation, check relocated classes and service registration, and reject application imports of parser classes. Maven coordinates, publication and a complete build-plugin distribution remain open.

The maintained JVM unit and golden tests exercise generated methods, complete CSS resources, independent and reversed compilation order, clean compiler outputs, nested owners, encoding collisions, syntax and ownership failures, functional selectors, conditional rules, declaration order and source diagnostics. The browser case exercises real React4j props, custom-property assignment, shared/component precedence, focus and disabled states, conflicting modifiers, nested instances and independent fragment reordering while preserving order inside each component. It uses pinned Chromium with `--headless=new`; Firefox and WebKit runtime behavior remain unverified.

This proof checks CSS syntax and selector/accessor agreement. It does not provide a property registry or Stylelint-equivalent semantic linting. Source formatting, browser-target transforms, vendor-prefix generation and Brotli packaging are not implemented in the JVM path. Generated component nesting is flattened, but other modern CSS features are emitted as authored. Those gaps need JVM-compatible solutions before claiming the capabilities of the earlier Node pipeline.

The proof also leaves final publication coordinates, publication, editor integration, cross-file style composition, richer owned subelements and keyframe bindings open. Java and shared CSS inputs share one extraction action: shared edits rerun that action with unchanged Java bindings, while embedded edits also invalidate checked Java, J2CL and the development bundle. Scaling the development loop across thousands of definitions and many compilation targets still requires measurement before product adoption. Earlier Node-pipeline timings and compressed sizes do not describe this JVM implementation.

## Production refinement

Continue on the draft branch; completing a refinement does not make the spike ready for master. The accepted authoring model and JVM-only toolchain remain constraints. Final names, publication coordinates, consumer build plugins and consumer adoption remain open.

1. **Structure and test baseline:** core/processor/example/integration-test roots, the standalone relocated processor jar, complete Java/CSS/diagnostic goldens and strict generated-body checks are implemented. This establishes the regression baseline for subsequent production work.
2. **Compiler integration:** javac service discovery, separate application/processor classpaths, and declared Bazel extraction dependencies are implemented and covered by maintained integration tests. Direct Bazel plugin/Turbine support remains to be resolved.
3. **CSS contract:** define the supported language against real consumer styles and verify parser behavior and diagnostics. Formatting, semantic checks and required browser transforms remain production work needing JVM-compatible implementations. The current parser subset does not select the final production contract.
4. **Resources and incremental builds:** define how independently compiled libraries contribute CSS, handle changed/deleted styles without stale resources, and verify deterministic assembly and realistic edit/build costs across thousands of definitions.
5. **Distribution and acceptance:** publish the now-separated annotation/standalone shaded processor artifacts after settling coordinates, supply consumer build integration, and validate the supported browser baseline. Review the complete production outcome before making the PR ready to merge; publication and WorldBuilder adoption remain separate work.
