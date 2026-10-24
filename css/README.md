# CSS-in-Java

Define component styles alongside Java views with `react4j.css.Css`. The annotation processor checks selectors against Java accessors and generates namespaced CSS. Annotation processing, CSS compilation and URL finalization run on the JVM.

Native `@utility-rules` inputs support checked families, typed values, variants, containers, starting styles and named anchors. Start with [using native utilities](../docs/native-utilities.md), then follow the [rule-author guide and reference](../docs/utility-rules.md) or [worked recipes](../docs/utility-recipes.md). Existing finite utility stylesheets remain supported.

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

Keep references to the generated binding class inside the style definition's forwarding `get()` method. The method must be static and non-final and return the style type. Use package access by default; make it public when a public style is consumed from another package. The generated singleton remains lazy, so no static field is needed in the authored class.

`style()` returns the root class token. Each modifier accessor returns both the root and its modifier token, so `success()` applies both sets of declarations. Every modifier must match a local selector such as `&.success`, and every local class selector must have an accessor. State pseudo-classes such as `:focus-visible` need no accessor.

CSS names derive from the Java package and class, including enclosing classes. The generated binding class is always package-access, including for public definitions. To keep styles in a view's source file, use a static nested class: `View.Styles` generates `View_Styled_Styles`. See [BrowserExample.java](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) for a complete rendering example.

Style definitions must be abstract, public or package-access, and stateless: no fields, explicit constructors or instance initializers. Extending another class, type parameters and sealed classes are unsupported. Nested definitions must be static with accessible enclosing types. Abstract accessors must resolve to an effective `String` signature without parameters, method type parameters or throws. Concrete and static helpers are allowed.

### Shared interfaces

Use ordinary Java interfaces to share a style contract. Each definition keeps its own CSS names:

```java
interface ActionStyle {
    String style();
    String success();

    default String forSuccess(boolean success) {
        return success ? success() : style();
    }
}

@Css("""
    color: white;
    &.success { background-color: green; }
    """)
abstract class PrimaryAction implements ActionStyle {
    static PrimaryAction get() {
        return Styled_PrimaryAction.get();
    }
}

@Css("""
    border: 1px solid;
    &.success { border-color: green; }
    """)
abstract class SecondaryAction implements ActionStyle {
    static SecondaryAction get() {
        return Styled_SecondaryAction.get();
    }
}
```

Accessors inherited from interfaces need no redeclaration and generate public overrides. Compatible peer declarations combine covariant returns and throws restrictions according to their Java contracts. Generic interfaces work when their substituted accessor signatures satisfy the rules above, such as `Styles<String>`. Interface constants and default, static and private helpers are allowed. A component still needs an abstract `style()`; redeclare a default `style()` as abstract to generate it.

Interfaces can also declare `@CssId`, `@CssAnimation` and `@CssCustomProperty` accessors, including for `@GlobalCss` and `@Keyframes` implementations. Names and CSS occurrences are checked in each consuming definition, including through separately compiled libraries. A standalone interface produces no CSS or bindings.

Symbol annotations follow the effective method declaration, using the same method collection as React4j. An inherited method needs no redeclaration. An override uses its own annotations and defaults; a plain override drops inherited symbol metadata, and an annotated override may change the symbol kind or name. Sibling declarations use the selected declaration without an additional annotation-conflict rule. Generated symbol accessors must be abstract. Unannotated concrete/default overrides are ordinary helpers; re-abstraction needs its own symbol annotation for typed symbols. Resource interfaces with only helpers need no Java binding.

## Utility libraries

For a runnable introduction, see the [utility CSS example](../examples/utility-css/README.md).
It combines a file-backed library, responsive utilities, explicit global defaults and a local component override in a stateful view.

Supply precompiled, flattened CSS through `@UtilityCssLibrary`, then select one library with `@UtilityStyles`:

```java
@UtilityCssLibrary("""
    .flex { display: flex; }
    .p-2 { padding: .5rem; }
    .text-red { color: red; }
    """)
class Utilities {}

@UtilityStyles(Utilities.class)
abstract class ButtonStyles {
    static ButtonStyles get() {
        return Styled_ButtonStyles.get();
    }

    @UtilityCss("flex p-2")
    abstract String style();

    @UtilityCss("text-red")
    abstract String warning();
}
```

Views call these accessors just like ordinary component accessors. Each returns the listed utility classes in order followed by its local root and, for a modifier, its local modifier marker. `warning()` does not include the utilities from `style()`; compose getters in Java when needed. Lists must be nonempty, contain distinct managed classes and match the supplied stylesheet. No conflict merging is performed.

The processor infers the class inventory from selectors, including referenced group or peer markers. Library classes have their own defining owner. `externalClasses` lists literal hooks that remain outside managed getter lists; append those hooks as ordinary strings in Java. A consumer's `@CssStable` controls its local markers, while the utility library controls its own names.

Flat utility inputs support media/supports conditions, layers, property registrations, keyframes, font faces, container queries and starting-style blocks, including nested groups. Empty `var()` fallbacks preserve their meaning. Native selector nesting and utility-generation directives must be compiled away before consumption.

Case-sensitive class-token selectors such as `[class~="active"]` share the checked class identity and are rewritten consistently. Supplied utility inputs reject whole-class-string, substring, order-sensitive, case-insensitive and namespaced class comparisons. Ordinary data and ARIA selectors keep their values.

Static `container-name` declarations and the name field of `container` define library-owned containers. Named `@container` queries use those identities, including through layers, media/supports and starting-style groups. List browser-owned ancestor names in `externalContainers`; an unknown ancestor name fails compilation. Querying a container does not retain the class that would create it. Dynamic container-name fields are unsupported.

Combine `@Css` with `@UtilityStyles` to add local declarations. A utility accessor can also have local rules, and it needs no dummy local selector. Ordinary local-only accessors retain their selector checks. Interfaces, effective annotation inheritance, helpers, factories, typed support accessors and peer-root imports follow the rules above. `@UtilityCss` metadata is replaced or removed by an overriding declaration's annotations.

Ordinary utility declarations must require a managed class on every selector-list or `:is`/`:where`/`:has` branch. Negation alone supplies no guard. Support definitions and custom-property initialization may be global. Supply page resets and element defaults separately with `@GlobalCss`, and select them explicitly through a bundle root or an import on the consuming style. Selecting a utility library alone does not activate a reset.

Library properties, registrations, variable reads, transition targets and property names in conditions share one scoped identity. Managed transition target names must appear directly in a transition declaration or its `var()` fallback; static variable definitions containing managed target names are rejected. Explicit external targets may remain in static variable definitions. Live utilities keep their required definitions and aliases; unused initialization and registrations can be removed. Assignments on a selected class remain part of that utility, including assignments that activate an imported provider. Browser variable evaluation is preserved, including alternative scopes, fallback values and cycles. Existing `@CssCustomProperty` getters can expose a library property, including one defined only by a registration, without making utility classes present.

Static utility variables may carry owned or imported animation and font names, including aliases, fallbacks and definitions in different scopes. These names are renamed at their original occurrences; `var()` remains for the browser to evaluate. Font families must be complete literals in one value. A carrier used both as a managed name and as other data, such as visible text or a style-query comparison, fails compilation. A registration that constrains a managed animation or font name with a literal syntax keyword is also rejected; use a universal or type-only syntax such as `"*"`, `"<custom-ident>"` or `"<string>"`. `externalAnimations` and `externalFonts` preserve explicit browser-owned names; external definitions remain available while their library is active.

List deliberately external variables in `externalProperties`, using names without `--`. Their spelling remains literal and they can be set through ordinary external CSS or `CssProps.customProperty("--name", value)`. An external property cannot also be selected as an owned import. Independent providers can reuse authored property names without sharing their values.

### File inputs

`@UtilityCssLibrary(file = "utilities.css")` and `@GlobalCss(file = "defaults.css")` select one package-relative file instead of an inline block. Declare exactly one source form. Paths must be canonical and relative, without traversal, backslashes, query strings or fragments.

Declare files beside their package sources with the public `css_sources` rule:

```starlark
load("@react4j//:defs.bzl", "css_sources")

css_sources(
    name = "style_inputs",
    srcs = ["utilities.css", "defaults.css"],
)
```

Add `:style_inputs` to a native J2CL library's dependencies and `:style_inputs_java` to the paired JVM library's dependencies. The rule infers the Java package from `src/main/java`, `src/test/java`, `src/main/css` or `src/test/css`. Every source is explicit; consumers inherit its resource jar through ordinary dependencies. It includes the source bytes and an automatically derived class catalogue available when provider header jars omit generated CSS resources. Authored CSS paths and contents remain in source maps.

For standalone javac, package the same inputs with the processor jar's JVM tool, then add the resulting jar to the provider and consumer compilation classpaths:

```sh
java -cp css-processor.jar react4j.css.processor.CssInputJar \
  style-inputs.jar demo/utilities.css path/to/utilities.css \
  demo/defaults.css path/to/defaults.css
```

Each pair specifies the logical package-relative source path and the actual file. Keep this input jar alongside the provider artifact for downstream compilation. Java processing does not invoke a utility generator.

The [browser example](examples/save-button/src/main/java/react4j/css/example/BrowserExample.java) combines inline utilities with a [file-backed provider](examples/save-button/src/main/java/react4j/css/example/UtilityLibraryB.java) compiled as a separate dependency and an explicit [global file](examples/save-button/src/main/css/react4j/css/example/utility-defaults.css). Its inherited utility interface uses the same ordinary Java helper pattern as component styles. The [packaged asset example](examples/save-button/src/main/java/react4j/css/example/AssetsExample.java) exercises utility font/animation aliases and finalized font files.

## Supported CSS

Component styles support root declarations and nested selectors beginning with `&`, including attributes and same-element `:is`, `:not` and `:where`. Import another component to target its root in a checked descendant or sibling selector. `@media` and `@supports` blocks require explicit nested selectors. Apply one component's root to each styled element. When modifiers conflict, stylesheet order determines precedence, regardless of class-token order.

In component CSS, raw descendant and sibling targets, global escapes, escaped selector or property identifiers, `:has`, `@container` and other at-rules are unsupported, except for outer-level keyframes and font faces described below. Component CSS must be a literal Java text block. Validation checks syntax and selector/accessor agreement; it does not check property semantics or transform CSS for browser compatibility.

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

The referenced type's annotation determines the import kind: named `@Css` imports supply peer roots, named `@Keyframes` imports supply animation names, and unnamed `@GlobalCss` imports include global rules. Component and animation aliases share a namespace within the importing definition. Property, ID, font and asset aliases each use a separate namespace. Named imports are checked even when unused; a selector or animation reference creates the dependency.

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

Readable keyframe names derive from the Java owner and authored animation name. Optimized builds apply the link’s naming policy. No Java accessor is needed for an animation name. Duplicate definitions and unknown animation names are errors. [StepButton.java](../examples/drumloop/src/main/java/react4j/drumloop/views/StepButton.java) shows an animation colocated with its view.

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

Selectors retain their authored names except for checked component references and bound IDs. Global styles support style rules, nesting, media/supports conditions, layers, property registrations, container queries and starting-style blocks. Conditional blocks require explicit selectors even inside a style rule, for example `body { @media screen { & { color: red; } } }`. Import shared animations and component roots with `@CssImport`; global selectors may use `:css(alias)` to reference those roots. Outer-level `@font-face` rules declare owned fonts as described below. Inline keyframe definitions and unrecognized at-rules are unsupported; font faces cannot be nested in selectors.

Select application globals through the bundle's `globals` attribute below. Add `@CssImport(ApplicationDefaults.class)` to a component or another global definition that requires those defaults. Global imports work across separately compiled libraries; duplicate imports, self-inclusion, names on global imports and incompatible types are errors. The bundler rejects cycles among global inclusion edges.

Keyframe, global and utility-library declaration classes may be concrete or abstract, public or package-access. Extending another class, type parameters and sealed classes are unsupported. Nested declarations must be static with accessible enclosing types. They must not declare fields, explicit constructors or initializers. Concrete and static helpers are allowed. Abstract methods must be annotated symbol accessors as described below. Definitions with accessors must be abstract and generate lazy Java bindings; definitions without accessors produce resources only.

### CSS names in Java

Use annotated abstract `String` methods to expose names from `@Css`, `@GlobalCss` or `@Keyframes` definitions:

| Annotation | CSS occurrence | Returned value |
| --- | --- | --- |
| `@CssId` | A hash selector or a pure fragment URL such as `url(#mask)` | The resolved ID without `#` |
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

Explicit ID names omit `#`; property names omit `--`. Each accessor must match a CSS occurrence or, for IDs and animations, a checked import. Duplicate exports and conflicting annotations are errors. `style()` exposes the component root class and cannot carry a typed-symbol annotation.

Bound IDs are rewritten in hash selectors and same-document fragment URLs, including nested expressions. Fragments on checked file URLs retain their authored spelling. Attribute selectors that reference bound IDs are rejected. An ID is static per definition; repeated component instances that need distinct DOM IDs must use separate instance IDs.

Share an ID with `@CssImport(value = Owner.class, id = "control", name = "local")`. The local alias defaults to the selected ID. All three definition kinds can consume selected IDs in selectors, fragment URLs or `@CssId` accessors. Providers may re-export imported aliases, including from separately compiled libraries. An import needs a CSS occurrence or an accessor; `id`, `property` and `font` selections are mutually exclusive.

Every hash selector in `@GlobalCss` requires a local `@CssId` accessor or selected ID import. A surviving importing accessor activates its provider chain and their unconditional global inclusions. A selector or fragment reference alone neither activates an ID provider nor proves that its DOM element exists. Stable IDs with the same spelling can match across already active styles.

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

### Stable names

Use `@CssStable` on a CSS definition to preserve all names it owns, including names without Java accessors. On an abstract accessor, it preserves that symbol; a modifier accessor preserves only its modifier token, while `style()` controls the root. Stable component names and standalone keyframe names keep their scoped spelling. Stable IDs and properties owned by `@GlobalCss` use their authored spelling in every build mode, including JVM accessor values.

Imports keep their defining owner's policy. Mark the provider to preserve an imported name; an explicit `@CssStable` importing accessor must agree with that provider. Marking a consuming definition covers its owned names only. Stability follows the effective declaration like other symbol annotations and does not retain otherwise unused CSS.

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

Both aliases resolve to Palette's property. Select `DarkPalette` as a global bundle root to apply its override. Imported property declarations and `var()` references use the same resolved name, so CSS inheritance and theme overrides work across style definitions. Providers need an actual property occurrence but no Java accessor. Imports work through separately compiled libraries and transitive aliases. Each property import must have a matching declaration or `var()` reference in the importing definition and retains its provider. An unused property import is a compilation error. Ownership cycles without a defining symbol are errors.

Raw shared stylesheets retain literal property names. Use a stable global property to share an authored property name with external CSS.

## Files and fonts

Declare package-relative file names with `@Assets` and `@Asset`. Keep the declarations alongside the component and use the ordinary React4j image API:

```java
@Assets
abstract static class Media {
    static Media get() {
        return Card_Assets_Media.get();
    }

    @Asset("card/icon.svg")
    abstract String icon();

    @Asset("card/photo.png")
    abstract String photo();

    @Asset("card/heading.woff2")
    abstract String headingFont();
}

// Inside Card's render method:
DOM.img(new ImgProps().src(Media.get().icon()).alt("Saved"));
DOM.img(new ImgProps().src(Media.get().photo()).alt("Preview"));
```

For `com.biz.ui.Card`, the corresponding source layout is:

```text
src/main/java/com/biz/ui/Card.java
src/main/java/com/biz/ui/BUILD.bazel
src/main/assets/com/biz/ui/BUILD.bazel
src/main/assets/com/biz/ui/card/icon.svg
src/main/assets/com/biz/ui/card/photo.png
src/main/assets/com/biz/ui/card/heading.woff2
```

Asset declarations follow the same stateless class, nested naming and effective-interface-method rules as style definitions. Every abstract asset accessor needs `@Asset` and returns a parameterless `String`. Names are relative to the Java package, with `/` separators; absolute paths, traversal, queries and fragments are invalid. Java accessors expose finalized URLs in the browser and readable logical references on the JVM. Imports validate declarations even when optimization later removes their use.

Declare files explicitly beside their asset sources:

```starlark
load("@react4j//:defs.bzl", "web_assets")

web_assets(
    name = "media",
    srcs = ["card/icon.svg", "card/photo.png", "card/heading.woff2"],
    visibility = ["//src/main/java/com/biz/ui:__pkg__"],
)
```

Add this catalogue to the owning native `j2cl_library`'s `deps`. The catalogue derives logical paths from the package's `src/main/assets/` (or `src/test/assets/`) location. Files in that Bazel package retain their relative subdirectories; a source supplied by another package uses its basename. Explicit generated files work too: preprocess first and list the result in `web_assets`, as the [SVG example](examples/save-button/src/main/assets/react4j/css/example/BUILD.bazel) does. Filesystem scanning and Java string interpolation are unnecessary.

### CSS URLs and owned fonts

Import an asset type and refer to its accessors inside CSS:

```java
@CssImport(value = Media.class, name = "media")
@Css("""
    @font-face {
      font-family: heading;
      src: url("asset:media.headingFont") format("woff2");
      font-weight: 400;
      font-style: normal;
      font-display: swap;
    }
    font-family: heading, sans-serif;
    background-image: url("asset:media.icon");
    """)
abstract static class Styles {
    static Styles get() {
        return Card_Styled_Styles.get();
    }

    abstract String style();
}
```

Declare `@font-face` at the outer level of `@Css`. Checked global and utility inputs also allow faces inside supported conditional or layer groups. Family names belong to their definition and are scoped and compressed with other CSS names. Multiple weights and styles share one family; retained family use keeps all its faces. Unused faces and their files are removed. Font-family matching uses Unicode case folding, without Unicode or quoted-whitespace normalization.

Share a family through a selected import, for example `@CssImport(value = Fonts.class, font = "righteous", name = "heading")`, then use `font-family: heading, cursive`. The provider owns the name and faces; the importing style keeps that provider only while its family usage survives. [Drumloop's shared family](../examples/drumloop/src/main/java/react4j/drumloop/views/Fonts.java) serves four components this way. Family imports can also pass through separately compiled definitions and aliases.

`font-family` lists, common `font` shorthand, `var()` fallbacks and ordinary face descriptors are parsed as CSS. Ambiguous dynamic shorthands that mix unresolved runtime values with managed family names are rejected; use a separate `font-family` declaration with a checked fallback. Component and global definitions reject managed family names hidden inside custom-property values; utility libraries support the finite static carriers described above. Validation checks definitions across selectors and conditional rules; variables supplying only weight, size or line-height remain supported.

Checked URLs also work in `image-set()` strings and preserve query/fragment suffixes, such as `url("asset:media.icon#mark")`. External HTTP(S), protocol-relative, data and blob URLs remain external; `url(#id)` remains a same-document reference. Other local file URLs must use a checked asset import. Asset imports alone do not retain every file.

### Application files and downstream delivery

The application finalizer combines surviving Java getters with references in every selected CSS variant. Only their files and explicit application assets are delivered. Optimized builds prune dead getters, rules and font faces; development builds conservatively retain available Java bindings. The [packaged example](examples/save-button/src/main/java/react4j/css/example/AssetsExample.java) demonstrates Java images, a CSS-only background, a font and an unused asset.

URLs use a digest of the processed, uncompressed bytes and a media-type extension. Compatible identical files deduplicate. Java URLs resolve relative to the external classic application script, including under nested document routes; CSS URLs resolve relative to the deployed stylesheet. Serve the generated application tree intact. JavaScript and CSS source maps preserve their Java origins after URL finalization.

Per-file preparation computes content metadata independently. Asset byte changes rerun preparation and final URL assembly while Java, J2CL and Closure compilation remain cached. Metadata preparation may run for unused candidates on their first build, even though those candidates are excluded from delivery.

`browser_application` combines finalized styles with explicit application files:

```starlark
browser_application(
    name = "application",
    entry_point = "com.biz.ui.Main",
    deps = [":entry"],
    html = "public/index.html",
    styles = {":styles": "css/application.css"},
    assets = {"public/favicon.ico": "favicon.ico"},
)
```

Set `runtime_mode = "development"` or `"production"` independently of `compilation_level = "BUNDLE"` or `"ADVANCED"`. For example, a diagnostic build can combine development React with ADVANCED optimization. Omitted settings use development React/BUNDLE normally and production React/ADVANCED under `-c opt`.

`defs` accepts Closure `--define=name=value` flags. Explicit values override the selected framework defaults in both compiled code and BUNDLE runtime configuration; quote string values. `include_source_maps = False` withholds generated JS/CSS maps and removes their final map comments from the delivered files. Internal maps remain available for linking and asset finalization. Map publication defaults to `True` in every mode.

Omit `html` when downstream tooling owns launch documents. An HTML-free BUNDLE application includes `config.js`; load it before `app.js`, alongside the supplied `react.js` and `react-dom.js`. With an HTML template, `@@DEFINES@@` expands to the same effective configuration.

The target's default output is the application directory. Its build-only `application.manifest.json` inventories every delivered file with its logical source, accessor, URL, SHA-256 digest, size, MIME type and retention reason, plus pruned candidates. The manifest stays outside the application directory. Expose it to a downstream Bazel action through its output group:

```starlark
filegroup(
    name = "application_manifest",
    srcs = [":application"],
    output_group = "manifest",
)
```

Downstream rules can take `:application` as a directory input and `:application_manifest` as a file input. They can also load `BrowserApplicationInfo` from `@react4j//:defs.bzl` and read its `directory` and `manifest` fields directly. The manifest's `assets` entries enumerate the delivered files: each `url` is a path relative to the directory, with `sha256`, `size` and `mediaType` describing its bytes. Multiple logical assets can share one URL. The `pruned` list records declared assets excluded by linking.

React4j emits uncompressed files. Downstream applications choose which retained files to compress, how to serve them and which URLs to precache or fetch on demand. Apply those policies after linking to avoid processing discarded candidates. See the [asset contract](../docs/specs/web-assets.md) for the delivery guarantees.

## Build integration

Apply `@react4j//:css_processor` to the native J2CL target that owns the definitions and add `@react4j//:css` to its dependencies. Browser clients configure their own compiler checks through J2CL; no separate Java library is required.

CSS and browser builds request the existing javac outputs of transitive J2CL libraries as Bazel validations. For strict javac linting, include `--add-reads=java.base=ALL-UNNAMED` in the native J2CL target's `javacopts`: J2CL's emulated `java.base` refers to annotation types on the compilation classpath. The repository's [example checker settings](../examples/checks.bzl) include this option.

Use [css_js_binary and css_bundle](../defs.bzl) to link JavaScript and collect its styles. The browser application and test macros already use this integration:

```starlark
load("@react4j//:defs.bzl", "css_bundle", "css_js_binary", "web_bundle")

css_js_binary(
    name = "application_bundle",
    deps = [":application"],
    entry_points = ["com.biz.ui.Main"],
    compilation_level = "ADVANCED",
)

css_bundle(
    name = "styles",
    root = ":application_bundle",
    shared = [":shared.css"],
    globals = ["com.biz.ui.ApplicationDefaults"],
    css = "application.css",
    source_map = "application.css.map",
)

web_bundle(
    name = "web",
    root = ":application_bundle",
    styles = {":styles": "css/application.css"},
)
```

Deploy the finalized JavaScript as `app.js` and its map as `app.js.map` at the application root, alongside the retained asset tree and CSS destinations from `web_bundle`; raw linked outputs contain intermediate asset references. `browser_application` performs this finalization and assembles the complete tree automatically. Load the generated CSS and serve its source map alongside it. Maps include the original Java and shared CSS source content. The [browser test build](examples/save-button/src/test/js/react4j/css/example/BUILD.bazel) shows several CSS variants sharing one JavaScript link.

Global roots use binary Java names; nested classes use `$`, such as `com.biz.ui.Main$Defaults`. Shared CSS files emit first in declared order, followed by globals. Globals emit dependency-first where dependencies are acyclic. Explicit global inclusions always precede their importer, including through property cycles. Declared roots establish the first visits, followed by other required definitions. Each definition emits once: listing a dependency again as a later root does not move it. Components are visited in Java owner order. The outer cascade order is `shared`, `utilities`, `components`, so ordinary component declarations take precedence over utility and global declarations. Each utility library has a private enclosing layer that preserves its own internal layer order. Checked and raw global inputs permit standard `!important` declarations; component and utility definitions reject them. Important global declarations can therefore override ordinary component declarations explicitly.

With `ADVANCED` compilation, CSS follows accessor values that survive JavaScript optimization, including through inlined helpers and interface calls. Unused owners, impossible selectors and resources required only by removed rules are dropped. A modifier value keeps its root class live. Any live checked accessor activates its author's unconditional global inclusions; retained declarations also keep their animation and property providers, including intermediate overrides. Complex selectors remain whenever their removal could change matching or specificity. Generated class, ID, animation and custom-property names are compressed by default, and source maps retain authored locations after pruning and renaming.

Configure naming through ordinary `css_js_binary` Closure `defs`. Use `--define=react4j.css.compress_names=false` to keep readable names while retaining optimized pruning. An optional quoted string, `--define=react4j.css.name_prefix='app-'`, prefixes compressed names for links that share a page. Assignments are deterministic for identical inputs and shared by every CSS variant of the link; they may change between releases. Development, `BUNDLE`, unlinked libraries and JVM accessors keep readable names.

Shared CSS keeps its authored names. Additional raw names in a CSS variant that collide with a compressed name fail bundling; the variant cannot change the link's assignments. Literal class/ID attribute references are reserved too. Broader attribute comparisons fail if shortening a name would make them start matching it. Mark the defining symbol stable or choose a distinct link prefix. Custom properties retain their leading `--` when compressed.

The link's `.css-live.json` report is also the reverse-name artifact. It lists every candidate, including unused and CSS-only names, with its canonical owner, kind, authored name, readable name, compiled name, stability and getters. Keep it with build/debugging artifacts; browser assets do not need it.

Generated names used in an optimized link must originate from checked accessors compiled within that link. Class-list composition and concatenation of accessor values work. Hardcoding or reconstructing generated names outside accessors, including in raw shared CSS, is unsupported. A peer selector or `url(#id)` does not establish that the referenced element exists. See the [liveness contract](../docs/specs/css-liveness.md) for the retention rules.

`BUNDLE` compilation and `css_bundle` roots that are unlinked Closure libraries retain all component styles and resource definitions with generated Java bindings, plus their dependencies and explicitly selected globals. Other animations and globals remain excluded until required. Complete links produce validated liveness evidence; missing or stale evidence fails the build.

Shared CSS files support style rules, `@media` and `@supports`, but cannot define keyframes. Put processed CSS sources in `src/main/css` and assets copied unchanged in `src/main/assets`. When packaging compiled style libraries, retain their generated `.css`, `.css.map`, `.css.json` and `.assets.json` resources. Keep generated binding Java beside its J2CL companion maps, or embed it in their `sourcesContent`, so optimized JavaScript maps can include the complete generated source. [Source maps](../docs/specs/css-source-maps.md) preserve generated Java origins for JavaScript and authored Java origins for CSS.

For direct javac use, put the core annotation jar, JSpecify and application dependencies on the compilation classpath, and the standalone processor jar on the processor path. Parser and formatter dependencies are bundled with the processor.

Generated Java is formatted with Palantir by default. The JVM running javac needs the [formatter module exports](../docs/project_setup.md#configure-maven). Disable formatting with `-Areact4j.css.format_generated_source=false`.
