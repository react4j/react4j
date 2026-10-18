# CSS-in-Java

Define component styles alongside Java views with `react4j.css.Css`. The annotation processor checks selectors against Java accessors and generates namespaced CSS. Processing and bundling run entirely on the JVM.

## Authoring

Declare an abstract style class with a literal CSS text block:

```java
import react4j.css.Css;
import react4j.css.CssCustomProperty;

@Css("""
    --on-accent: white;
    --focus: #06f;
    --success: #080;
    color: var(--on-accent);

    &:focus-visible {
      outline: 2px solid var(--focus);
    }

    &.success {
      background-color: var(--success);
    }
    """)
abstract class SaveButton {
    static SaveButton get() {
        return Styled_SaveButton.get();
    }

    abstract String style();

    abstract String success();

    @CssCustomProperty(name = "success")
    abstract String successProperty();
}
```

Access styles through the authored type:

```java
DOM.button(new BtnProps().className(SaveButton.get().success()), "Saved");
```

Keep references to the generated subclass inside the style definition's forwarding `get()` method. The method must be static and non-final and return the style type. Use package access by default; make it public when a public style is consumed from another package. The generated singleton remains lazy, so no static field is needed in the authored class.

`style()` returns the root class token. Each modifier accessor returns both the root and its modifier token, so `success()` applies both sets of declarations. Every modifier must match a local selector such as `&.success`, and every local class selector must have an accessor. State pseudo-classes such as `:focus-visible` need no accessor.

CSS names derive from the Java package and class, including enclosing classes. The generated subclass is always package-access, including for public definitions. To keep styles in a view's source file, use a static nested class: `View.Styles` generates `View_Styled_Styles`. See [BrowserExample.java](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) for a complete rendering example.

Style definitions must be abstract, public or package-access, and stateless: no fields, explicit constructors or instance initializers. Inheritance, interfaces, type parameters and sealed classes are unsupported. Nested definitions must be static with accessible enclosing types. Abstract accessors must return `String` and have no parameters, type parameters or throws. Concrete and static helpers are allowed.

## Supported CSS

Component styles support root declarations and nested selectors beginning with `&`, including attributes and same-element `:is`, `:not` and `:where`. Import another component to target its root in a checked descendant or sibling selector. `@media` and `@supports` blocks require explicit nested selectors. Apply one component's root to each styled element. When modifiers conflict, stylesheet order determines precedence, regardless of class-token order.

Raw descendant and sibling targets, global escapes, escaped selector or property identifiers, `:has`, `@container` and other at-rules are unsupported, except for the keyframes described below. CSS must be a literal Java text block. Validation checks syntax and selector/accessor agreement; it does not check property semantics or transform CSS for browser compatibility.

### Peer components

Name a component import locally and reference its root with `:css(alias)`:

```java
@Css("""
    color: white;
    """)
abstract class LabelStyles {
    static LabelStyles get() {
        return Styled_LabelStyles.get();
    }

    abstract String style();
}

@CssImport(name = "label", value = LabelStyles.class)
@CssImport(ApplicationDefaults.class)
@Css("""
    &:focus-visible + :css(label)[data-status] {
      color: green;
    }
    """)
abstract class ButtonStyles {
    static ButtonStyles get() {
        return Styled_ButtonStyles.get();
    }

    abstract String style();
}
```

Component selectors start with `&`. Each later compound after a descendant, child (`>`), adjacent sibling (`+`) or general sibling (`~`) combinator must start with `:css(alias)`. Qualify peer roots with attributes or pseudo-classes. Peer modifier classes and peer references inside component selector functions are unsupported. Component reference cycles are allowed.

The referenced type's annotation determines the import kind: named `@Css` imports supply peer roots, named `@Keyframes` imports supply animation names, and unnamed `@GlobalCss` imports include global rules. Component and animation aliases share a namespace within the importing definition. Property aliases use a separate namespace. Named imports are checked even when unused; a selector or animation reference creates the dependency.

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
    static HeartStyles get() {
        return Styled_HeartStyles.get();
    }

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
    static StatusStyles get() {
        return Styled_StatusStyles.get();
    }

    abstract String style();
}
```

The shared animation's name derives from its Java class. Multiple consumers emit one definition. Imports work across separately compiled libraries and are checked even when unused; animation declarations and accessors retain their definitions. Repeat `@CssImport` to import more names. Aliases must be distinct and cannot conflict with local keyframe definitions. Use `animation-name` to avoid shorthand ambiguity when an alias is also a timing or direction keyword.

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

Selectors retain their authored names except for checked component references and bound IDs. Global styles support style rules, nesting, `@media` and `@supports`. Conditional blocks require explicit selectors even inside a style rule, for example `body { @media screen { & { color: red; } } }`. Import shared animations and component roots with `@CssImport`; global selectors may use `:css(alias)` to reference those roots. Inline keyframe definitions and other at-rules, including `@font-face`, are unsupported.

Select application globals through the bundle's `globals` attribute below. Add `@CssImport(ApplicationDefaults.class)` to a component or another global definition that requires those defaults. Global imports work across separately compiled libraries; duplicate imports, self-inclusion, names on global imports and incompatible types are errors. The bundler rejects cycles among global inclusion edges.

Keyframe and global declaration classes may be concrete or abstract, public or package-access. Inheritance, interfaces, type parameters and sealed classes are unsupported. Nested declarations must be static with accessible enclosing types. They must not declare fields, explicit constructors or initializers. Concrete and static helpers are allowed. Abstract methods must be annotated symbol accessors as described below. Definitions with accessors must be abstract and generate lazy Java bindings; definitions without accessors produce resources only.

### CSS names in Java

Use annotated abstract `String` methods to expose names from `@Css`, `@GlobalCss` or `@Keyframes` definitions:

| Annotation | CSS occurrence | Returned value |
| --- | --- | --- |
| `@CssId` | A hash selector or a pure fragment URL such as `url(#mask)` | The scoped ID without `#` |
| `@CssAnimation` | A local keyframe or an imported animation alias | The resolved animation name |
| `@CssCustomProperty` | A custom-property declaration or `var()` reference | The resolved name including `--` |

Names default to the method name, stripping a getter prefix: `getAccent()` becomes `accent`, `getURL()` becomes `uRL`, and `accent()` stays `accent`. No suffix stripping or camel-to-kebab conversion occurs. Override the name when needed:

```java
@CssId(name = "control")
abstract String controlId();

@CssAnimation(name = "pulse")
abstract String pulseName();

@CssCustomProperty(name = "accent-color")
abstract String accentProperty();
```

Explicit ID names omit `#`; property names omit `--`. Each accessor must match a CSS occurrence or, for animations, a checked import. Duplicate exports and conflicting annotations are errors. `style()` remains an unannotated class accessor on components.

Bound IDs are rewritten in hash selectors and same-document fragment URLs, including nested expressions. Strings and asset URLs such as `icons.svg#mask` retain their text. Attribute selectors that reference bound IDs are rejected. An ID is static per definition; repeated component instances that need distinct DOM IDs must use separate instance IDs.

On a `@Keyframes` class, leave the animation annotation's `name` unset to expose that definition's own class-derived name:

```java
@Keyframes("""
    from { opacity: 0.4; }
    to { opacity: 1; }
    """)
abstract class SharedPulse {
    static SharedPulse get() {
        return Styled_SharedPulse.get();
    }

    @CssAnimation
    abstract String name();
}

// Use the checked name with the ordinary DOM style API.
new CssProps().animationName(SharedPulse.get().name()).animationDuration("0.8s");
```

An animation accessor retains an imported animation even when no CSS declaration references it.

### Custom properties and runtime values

All custom properties in Java CSS definitions belong to that definition by default, including unannotated declarations and names used only through `var()`. Set runtime values with a checked accessor:

```java
new BtnProps()
        .className(SaveButton.get().success())
        .style(new CssProps().customProperty(SaveButton.get().successProperty(), "#04c"));
```

Values are CSS text, including units. Passing null removes a previously rendered value.

Import a property to share its identity across definitions. `property` selects the source name without `--`; optional `name` supplies a local alias:

```java
@GlobalCss("""
    :root { --accent: #06f; }
    """)
final class Palette {}

@CssImport(value = Palette.class, property = "accent", name = "ink")
@Css("""
    color: var(--ink);
    """)
abstract class ThemedLabel {
    static ThemedLabel get() {
        return Styled_ThemedLabel.get();
    }

    abstract String style();

    @CssCustomProperty(name = "ink")
    abstract String inkProperty();
}

@CssImport(value = Palette.class, property = "accent", name = "ink")
@GlobalCss("""
    :root { --ink: #09c; }
    """)
final class DarkPalette {}
```

Both aliases resolve to Palette's property. Select `DarkPalette` as a global bundle root to apply its override. Imported property declarations and `var()` references use the same resolved name, so CSS inheritance and theme overrides work across style definitions. Providers need an actual property occurrence but no Java accessor. Imports work through separately compiled libraries and transitive aliases. Used imports retain their providers; unused imports are checked and omitted from dependencies. Ownership cycles without a defining symbol are errors.

Raw shared stylesheets retain literal property names and do not participate in this ownership model. Fixed-name property bridges to external CSS are unsupported.

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

Global roots use binary Java names; nested classes use `$`, such as `com.biz.ui.Main$Defaults`. Shared CSS files emit first in declared order, followed by globals. Globals emit dependency-first where dependencies are acyclic. Explicit global inclusions always precede their importer, including through property cycles. Declared roots establish the first visits, followed by other required definitions. Each definition emits once: listing a dependency again as a later root does not move it. Components are visited in Java owner order. The `shared` and `components` cascade layers give component styles precedence over shared files and globals. All inputs reject `!important`, which would reverse that precedence.

All component styles and resource definitions with generated Java bindings in reachable build targets are included, even if their accessors are unused. Other animations and global styles are included when referenced by retained definitions or, for globals, selected explicitly. Collection follows Bazel reachability and does not use Closure liveness.

Shared CSS files support style rules, `@media` and `@supports`, but cannot define keyframes. Put processed CSS sources in `src/main/css` and assets copied unchanged in `src/main/assets`. When packaging compiled style libraries, retain their generated `.css`, `.css.map` and `.css.json` resources.

For direct javac use, put the core annotation jar, JSpecify and application dependencies on the compilation classpath, and the standalone processor jar on the processor path. Parser and formatter dependencies are bundled with the processor.

Generated Java is formatted with Palantir by default. The JVM running javac needs the [formatter module exports](../docs/project_setup.md#configure-maven). Disable formatting with `-Areact4j.css.format_generated_source=false`.
