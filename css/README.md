# CSS-in-Java

`react4j.css.Css` defines static component styles in Java text blocks. Annotation processing checks the CSS and generates class-name accessors and a stylesheet. CSS processing runs entirely on the JVM; parser dependencies stay on the processor classpath.

## Authoring

See [SaveButton.java](examples/save-button/src/main/java/react4j/css/example/SaveButton.java) for a style definition and [BrowserExample.java](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) for its use in React4j.

A definition is an abstract class annotated with a literal `@Css` text block. It declares `abstract String style()` and zero-argument abstract `String` methods for modifiers such as `success()`. Each modifier must have a corresponding local selector, such as `&.success`, and each local selector must have an accessor. Definitions need an accessible no-argument constructor; inheritance and type parameters are unsupported. Nested definitions must be static.

The processor generates a package-access subclass, such as `Styled_SaveButton`, that an ordinary Java factory can instantiate. `style()` returns the root class token; a modifier accessor returns both the root and its namespaced modifier token. Names derive from the Java package, class and enclosing classes, so authors do not allocate globally unique CSS names. Conflicting modifiers follow stylesheet order, independently of class-token order. Apply one component's root to each styled element.

## Supported CSS

Component styles support root declarations, nested selectors on the same element, and the functional pseudo-classes `:is`, `:not` and `:where`. `@media` and `@supports` blocks must contain explicit nested selectors rather than bare declarations. State pseudo-classes such as `:focus-visible` need no accessor.

Descendant and sibling selectors, global escapes, escaped selector identifiers, `:has`, keyframe bindings, `@container` and other at-rules are unsupported. Java constants, concatenation and interpolation cannot replace the literal text block. Validation checks CSS syntax and selector/accessor agreement; it does not validate property semantics or transform CSS for browser compatibility.

Shared stylesheets support style rules, `@media` and `@supports`. Bundles place shared styles before component styles using the `shared` and `components` cascade layers. `!important` is rejected because it reverses layer precedence. Processed CSS sources belong in `src/main/css`; assets copied unchanged belong in `src/main/assets`.

For values that change at runtime, use CSS custom properties:

```java
var custom = new CssProps().customProperty("--accent", "#04c");
```

Reference the value with `var(--accent)` in CSS and pass `custom` through the element's `style` prop. Values are CSS text, including any units; null removes a previously rendered value.

## Build integration

| Target | Use |
| --- | --- |
| `//css/core` | JVM annotation classpath. |
| `//css/core:core-j2cl` | J2CL annotation dependency; imports bytecode without emitting annotation JavaScript. |
| `//css/processor` | Standalone shaded annotation processor. |

The [styles rule](rules.bzl) extracts a generated source archive and CSS file through its `sources` and `css` output groups. Compile the archive alongside the style definitions and load the CSS with the application. The [example BUILD file](examples/save-button/src/main/java/react4j/css/example/BUILD.bazel) shows the wiring.

`styles.deps` supplies application dependencies on the JVM compilation classpath. For a `java_j2cl_library`, use its `<name>_java` target; bare J2CL dependencies are not accepted. Browser clients can use `core-j2cl` without declaring their own Java library. For javac service discovery, put core, JSpecify and application dependencies on the compilation classpath, and the processor jar on the processor path.

## Subprojects

- [core](core/): client annotation.
- [processor](processor/): annotation processor and extraction tool.
- [examples/save-button](examples/save-button/): React4j usage example.
- [integration-tests](integration-tests/): extraction with application dependencies.

Development commands and fixture updates are documented in [CONTRIBUTING.md](../CONTRIBUTING.md).
