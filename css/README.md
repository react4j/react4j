# CSS-in-Java

Define component styles alongside Java views with `react4j.css.Css`. The annotation processor checks selectors against Java accessors and generates namespaced CSS. Processing and bundling run entirely on the JVM.

## Authoring

Declare an abstract style class with a literal CSS text block:

```java
import react4j.css.Css;

@Css("""
    color: var(--on-accent);

    &:focus-visible {
      outline: 2px solid var(--focus);
    }

    &.success {
      background-color: var(--success);
    }
    """)
abstract class SaveButton {
    abstract String style();

    abstract String success();
}
```

Use the generated singleton in the same package:

```java
DOM.button(new BtnProps().className(Styled_SaveButton.get().success()), "Saved");
```

`style()` returns the root class token. Each modifier accessor returns both the root and its modifier token, so `success()` applies both sets of declarations. Every modifier must match a local selector such as `&.success`, and every local class selector must have an accessor. State pseudo-classes such as `:focus-visible` need no accessor.

CSS names derive from the Java package and class, including enclosing classes. The generated subclass is always package-access, including for public definitions. To keep styles in a view's source file, use a static nested class: `View.Styles` generates `View_Styled_Styles`. See [BrowserExample.java](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) for a complete rendering example.

Style definitions must be abstract, public or package-access, and stateless: no fields, explicit constructors or instance initializers. Inheritance, interfaces, type parameters and sealed classes are unsupported. Nested definitions must be static with accessible enclosing types. Abstract accessors must return `String` and have no parameters, type parameters or throws. Concrete and static helpers are allowed; use names other than `get()` for helper factories to avoid conflicts with the generated method.

## Supported CSS

Component styles support root declarations and nested selectors beginning with `&` that target the same element, including attributes and `:is`, `:not` and `:where`. `@media` and `@supports` blocks require explicit nested selectors. Apply one component's root to each styled element. When modifiers conflict, stylesheet order determines precedence, regardless of class-token order.

Descendant and sibling selectors, global escapes, escaped selector or property identifiers, `:has`, `@container` and other at-rules are unsupported, except for the keyframes described below. CSS must be a literal Java text block. Validation checks syntax and selector/accessor agreement; it does not check property semantics or transform CSS for browser compatibility.

### Animations

Declare local keyframes at the outer level of the text block and reference them in `animation` or `animation-name`:

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

Keyframe names are namespaced automatically and remain stable when the animation body changes. No Java accessor is needed for an animation name. Duplicate definitions and unknown animation names are errors. [StepButton.java](../examples/drumloop/src/main/java/react4j/drumloop/views/StepButton.java) shows an animation colocated with its view.

For reuse across style definitions, declare a `@Keyframes` class and import it under a local name:

```java
@Keyframes("""
    from { opacity: 0.4; }
    to { opacity: 1; }
    """)
abstract class Pulse {}

@CssImport(name = "pulse", value = Pulse.class)
@Css("""
    animation: pulse 0.8s ease-in-out infinite alternate;
    """)
abstract class StatusStyles {
    abstract String style();
}
```

The shared animation's name derives from its Java class. Multiple consumers emit one definition. Imports work across separately compiled libraries and are checked even when unused; only animation references retain their definitions. Repeat `@CssImport` to import more names. Aliases must be distinct and cannot conflict with local keyframe definitions. Use `animation-name` to avoid shorthand ambiguity when an alias is also a timing or direction keyword.

Use unprefixed `@keyframes`, `animation` and `animation-name`, with unescaped, unquoted names. Keyframe definitions inside selectors or conditional rules and animation bindings inside keyframes are unsupported. Names must be static. For dynamic timing, use `animation-name: pulse` with longhands such as `animation-duration: var(--duration)`. Shorthand accepts only `steps()`, `cubic-bezier()` and `linear()` functions.

### Global styles

Use `@GlobalCss` for page defaults and rules that target elements outside a component:

```java
@GlobalCss("""
    body { margin: 0; }
    button, select { border-radius: 2px; }
    """)
final class ApplicationDefaults {}
```

Selectors retain their authored names and require no Java accessors. Global styles support style rules, nesting, `@media` and `@supports`. Conditional blocks require explicit selectors even inside a style rule, for example `body { @media screen { & { color: red; } } }`. Import shared animations with `@CssImport`; inline keyframe definitions and other at-rules, including `@font-face`, are unsupported.

Select application globals through the bundle's `globals` attribute below. Add `@CssImport(ApplicationDefaults.class)` to a component or another global definition that requires those defaults. Global imports work across separately compiled libraries; duplicate imports, self-inclusion, names on global imports and incompatible types are errors. The bundler rejects dependency cycles.

Keyframe and global declaration classes may be concrete or abstract, public or package-access. Inheritance, interfaces, type parameters and sealed classes are unsupported. Nested declarations must be static with accessible enclosing types. They must not declare fields, explicit constructors, initializers or abstract methods; concrete and static helpers are allowed. These definitions produce resources without generated Java bindings.

### Runtime values

Use CSS custom properties for values that change at runtime. Reference them with `var(--success)` in CSS and set them through the element's `style` prop:

```java
new BtnProps()
        .className(Styled_SaveButton.get().success())
        .style(new CssProps().customProperty("--success", "#04c"));
```

Values are CSS text, including any units. Passing null removes a previously rendered value.

## Build integration

Apply `//css/processor:plugin` to the J2CL target that owns the style definitions and add `//css/core:core-j2cl` to its dependencies. Browser clients do not need to declare a separate Java library. See the [example Java build](examples/save-button/src/main/java/react4j/css/example/BUILD.bazel) for processor and dependency configuration.

Use [css_bundle](defs.bzl) to collect styles from the application's Closure linking target:

```starlark
load("//css:defs.bzl", "css_bundle")

css_bundle(
    name = "styles",
    root = ":application_bundle",
    shared = [":shared.css"],
    globals = ["com.biz.ui.ApplicationDefaults"],
    css = "application.css",
    source_map = "application.css.map",
)
```

Load the generated CSS with the application and serve its source map alongside it. Maps include the original Java and shared CSS source content. The [browser test build](examples/save-button/src/test/js/react4j/css/example/BUILD.bazel) shows two bundles selecting different globals from the same dependencies.

Global roots use binary Java names; nested classes use `$`, such as `com.biz.ui.Main$Defaults`. Shared CSS files emit first in declared order, followed by globals. Globals are visited dependency-first, starting with declared roots and then component requirements. Each definition emits once: listing a dependency again as a later root does not move it. Components are visited in Java owner order. The `shared` and `components` cascade layers give component styles precedence over shared files and globals. All inputs reject `!important`, which would reverse that precedence.

All component styles in reachable build targets are included, even if their accessors are unused. Shared animations and global styles are included only when referenced by retained styles or, for globals, selected explicitly. Collection does not yet use Closure liveness.

Shared CSS files support style rules, `@media` and `@supports`, but cannot define keyframes. Put processed CSS sources in `src/main/css` and assets copied unchanged in `src/main/assets`. When packaging compiled style libraries, retain their generated `.css`, `.css.map` and `.css.json` resources.

For direct javac use, put the core annotation jar, JSpecify and application dependencies on the compilation classpath, and the standalone processor jar on the processor path. Parser and formatter dependencies are bundled with the processor.

Generated Java is formatted with Palantir by default. The JVM running javac needs the [formatter module exports](../docs/project_setup.md#configure-maven). Disable formatting with `-Areact4j.css.format_generated_source=false`.
