# CSS-in-Java

`react4j.css.Css` defines static component styles in Java text blocks. Annotation processing checks the CSS and generates class-name accessors and a stylesheet. CSS processing runs entirely on the JVM; parser dependencies stay on the processor classpath.

## Authoring

See [SaveButton.java](examples/save-button/src/main/java/react4j/css/example/SaveButton.java) for a style definition and [BrowserExample.java](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) for its use in React4j.

A definition is an abstract class annotated with a literal `@Css` text block. It declares `abstract String style()` and zero-argument abstract `String` methods for modifiers such as `success()`. Each modifier must have a corresponding local selector, such as `&.success`, and each local selector must have an accessor. Definitions need an accessible no-argument constructor without throws; accessor methods cannot have parameters, type parameters or throws. Inheritance, type parameters and sealed classes are unsupported. Nested definitions must be static, and their enclosing types must be accessible from the package.

The processor generates a package-access subclass, such as `Styled_SaveButton`, that an ordinary Java factory can instantiate. `style()` returns the root class token; a modifier accessor returns both the root and its namespaced modifier token. Names derive from the Java package, class and enclosing classes, so authors do not allocate globally unique CSS names. Conflicting modifiers follow stylesheet order, independently of class-token order. Apply one component's root to each styled element.

## Supported CSS

Component styles support root declarations, nested selectors on the same element, and the functional pseudo-classes `:is`, `:not` and `:where`. `@media` and `@supports` blocks must contain explicit nested selectors rather than bare declarations. State pseudo-classes such as `:focus-visible` need no accessor. Component styles also support owner-local `@keyframes`.

Descendant and sibling selectors, global escapes, escaped selector or component property identifiers, `:has`, `@container` and other at-rules are unsupported. Java constants, concatenation and interpolation cannot replace the literal text block. Validation checks CSS syntax and selector/accessor agreement; it does not validate property semantics or transform CSS for browser compatibility.

Declare owner-local keyframes at the outer level of the text block, and reference their local names in `animation` or `animation-name`:

```java
@Css("""
    @keyframes beat {
      from { transform: scale(1); }
      to { transform: scale(0.9); }
    }
    &.beating {
      animation: beat 0.8s ease-in-out infinite alternate;
    }
    """)
abstract class HeartStyles {
    abstract String style();
    abstract String beating();
}
```

Keyframe names derive from the Java owner and local name, for example `jcss-com-biz-ui-HeartStyles--keyframes-beat`. They remain stable when the animation body changes. The processor rewrites parsed animation references, including comma-separated lists and shorthand, and reports duplicate definitions or unknown local names. Shorthand resolves keywords using CSS Animations Level 1 precedence. No Java accessor is needed for an animation name.

Definitions must use unprefixed `@keyframes` and unescaped, unquoted identifiers. Animation references must use unprefixed properties. Definitions inside selectors or conditional rules, cross-owner references and animation bindings inside keyframes are unsupported. Animation names must be static; for dynamic timing values, use `animation-name: beat` with longhands such as `animation-duration: var(--duration)`. Functions in shorthand are limited to `steps()`, `cubic-bezier()` and `linear()` timing functions. Shared stylesheets cannot define keyframes.

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

Apply `//css/processor:plugin` to the `j2cl_library` that owns the style definitions. The processor generates implementation classes and emits package-relative CSS and source-map resources, such as `com/biz/ui/SaveButton.css` and `SaveButton.css.map`. Factory signatures should use the authored abstract type. J2CL must preserve these generated resources in its JavaScript output tree; the repository's pinned J2CL patch supplies this behavior.

The [css_bundle rule](rules.bzl) starts at a Closure linking target and collects J2CL resources through its transitive `deps` and `exports`. Shared files retain their declared order; component resources are ordered by binary owner path. A dependency reached through multiple paths contributes its resources once; duplicate owner paths from different libraries fail the build.

```starlark
css_bundle(
    name = "styles",
    root = ":application_bundle",
    shared = [":shared.css"],
    css = "application.css",
    source_map = "application.css.map",
)
```

Load the generated CSS with the application. The CSS references its source map, which uses indexed source-map v3 sections to preserve component mappings back to Java and shared mappings back to CSS. Java mappings identify selectors, declarations, conditional-rule headers and keyframe blocks; synthetic layer wrappers are unmapped. Maps include original source content. Component CSS is emitted compactly; shared CSS retains its formatting with normalized line endings. Collection includes all styles in reachable build targets.

The [example Java build](examples/save-button/src/main/java/react4j/css/example/BUILD.bazel) shows processor wiring, and its [browser test build](examples/save-button/src/test/js/react4j/css/example/BUILD.bazel) assembles CSS from the Closure binary. The checked `java_j2cl_library` macro runs the same plugin during JVM checks and J2CL compilation without feeding generated implementations back through the processor.

Browser clients can use `core-j2cl` without declaring their own Java library. For javac service discovery, put core, JSpecify and application dependencies on the compilation classpath, and the standalone processor jar on the processor path.

## Subprojects

- [core](core/): client annotation.
- [processor](processor/): isolated annotation processor.
- [bundler](bundler/): JVM CSS and source-map assembly.
- [internal](internal/): shared build-time CSS parsing and source-map emission.
- [examples/save-button](examples/save-button/): React4j usage example.
- [integration-tests](integration-tests/): J2CL resource retention and dependency-graph assembly.

Development commands and fixture updates are documented in [CONTRIBUTING.md](../CONTRIBUTING.md).
