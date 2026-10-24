---
title: Use native utilities
---

Native utilities let you choose reusable styles through checked Java methods. A rule library defines what names such as `p-5` and `bg-accent` mean. Your style class selects them, and your view applies the returned class strings.

**The language does not include a standard utility library.** Every name comes from the provider you select. Agents can author that vocabulary using the [rule language](utility-rules.md); application developers use it through ordinary Java classes.

## Try the example

[Run the native utility example](/examples/native-utilities/).

Change the saved state, switch between wide and narrow containers, then open each Details panel. Each panel follows its own trigger. Tab reaches its close button; Escape dismisses it and returns focus. Reduced-motion preferences disable the entry transition.

To run the same application from a repository checkout:

```sh
tools/examples.sh serve native-utilities development
```

Open `http://127.0.0.1:3000`. Use `production` instead of `development` to try optimized output with compressed CSS names and unused styles removed. The [example source](https://github.com/react4j/react4j/tree/master/examples/native-utilities) contains the application, rule library and browser tests. Build prerequisites are in [project setup](project_setup.md).

## Select a provider and use its methods

The example's `Utilities` class names its package-relative source file:

```java
import react4j.css.UtilityCssLibrary;

@UtilityCssLibrary(file = "utilities.css")
final class Utilities {}
```

Here is its button style definition. Keep the generated factory inside your authored class; views call `Buttons.get()`.

```java
import react4j.css.Css;
import react4j.css.UtilityCss;
import react4j.css.UtilityStyles;

@UtilityStyles(Utilities.class)
@Css("""
    border-radius:.375rem;
    &:focus-visible { outline:3px solid #145c4b; outline-offset:3px; }
    """)
abstract class Buttons {
    static Buttons get() {
        return Styled_Buttons.get();
    }

    @UtilityCss("button px-4 py-2")
    abstract String style();

    @UtilityCss("bg-accent text-white hover:bg-hover")
    abstract String ready();

    @UtilityCss("bg-saved text-white")
    abstract String saved();
}
```

A view combines the base getter with the chosen state:

```java
var buttons = Buttons.get();
return DOM.button(
        new BtnProps().className(buttons.style(), saved ? buttons.saved() : buttons.ready()),
        saved ? "Saved" : "Save changes");
```

`ready()` and `saved()` supply their own utility lists. They do not inherit `style()`'s padding or button declarations. Compose getters explicitly as above, or put the complete list on a getter. Utility lists must be nonempty and cannot repeat a candidate.

The example nests these style classes in `Workspace`, so their generated factory names include that enclosing class. The public usage pattern is the same.

The local `@Css` radius overrides the utility's radius. Ordinary local component CSS has precedence over utility CSS. Changing class-list order does not decide which conflicting utility wins: choose one intended value, a distinct state getter or local CSS. Do not construct generated names yourself; optimized builds can shorten them.

## Understand the names you selected

These are definitions from the example's `utilities.css`:

```css
@utility-rules;
@scale spacing .25rem;
@tokens color <color> { accent:#145c4b; saved:#225b3f; }

@utility "p-{size}" (size:scale(spacing)|arbitrary(<length-percentage>)) {
  padding:param(size);
}
@utility "bg-{tone}" (tone:tokens(color)|arbitrary(<color>)) {
  background-color:param(tone);
}
```

| Request | Meaning in this library |
| --- | --- |
| `p-5` | Five spacing steps: `1.25rem` padding. |
| `p-137` | A valid new scale value; no class inventory needs to list it first. |
| `p-[10%]` | An explicit percentage value. |
| `bg-accent` | The named accent color. |
| `bg-[#145c4b]` | An explicit color value. |
| `bg-[12px]` | An error: a length cannot supply a color. |

A **family** is a rule with a value argument, such as `p-{size}`. A **variant** wraps a utility in a selector or condition. The example declares `hover`, the named container threshold `note-min-wide`, and `starting`. These are library choices, rather than reserved utility names.

## Containers and popovers need real elements

The example applies `frame` to an ancestor. That utility establishes the `note` size container. Its cards request `note-min-wide:row`, which switches their layout when that ancestor is wide enough. Merely naming a query does not create or retain a container element. The width button changes the ancestor, so you can see a container query respond without resizing the browser.

Each card also scopes an anchor name and contains both its trigger and panel. The browser's native popover attributes manage opening, dismissal and focus. The library supplies anchor association, placement, flip fallbacks, size limits and an explicit opacity transition. `starting:opacity-0` only supplies the starting value; it does not create a transition by itself.

These are native browser features. The example is verified in the repository's pinned Chromium; compilation does not guarantee support or a successful placement in every browser/layout. Keep viewport edges, oversized content, scrolling and keyboard behavior in your application's browser checks. See the [popover recipe](utility-recipes.md#scoped-popovers) for the role mapping and boundaries.

## Add your own source to a build

Put `utilities.css` beside its Java provider, then declare the file explicitly:

```starlark
load("@react4j//:defs.bzl", "css_sources")

css_sources(
    name = "style_inputs",
    srcs = ["utilities.css"],
)
```

Add `:style_inputs` and `@react4j//:css` to the owning J2CL target's dependencies, and `@react4j//:css_processor` to its plugins, as shown in the [example BUILD file](https://github.com/react4j/react4j/blob/master/examples/native-utilities/src/main/java/react4j/nativeutilities/BUILD.bazel). Paired JVM libraries use `:style_inputs_java`. Java annotation processing requires Java 21 or later; repository commands select the pinned toolchain.

A literal Java text block can supply rules instead of a file. Choose exactly one source form. No application scanner or generator watcher is involved; compilation runs on the JVM and the application receives ordinary CSS and JavaScript.

Resets and element defaults remain explicit. The example declares them with `@GlobalCss` and selects them through `@CssImport(Defaults.class)`. Selecting a utility provider alone adds no reset. The [CSS guide](https://github.com/react4j/react4j/blob/master/css/README.md) covers shared interfaces, inherited accessors, imports and other binding options.

## If you know Tailwind

| Familiar idea | Native utility rules |
| --- | --- |
| Spacing and color classes | Your provider declares scales, tokens and families; there is no built-in theme or class catalogue. |
| `hover:...` and arbitrary values | Supported when the provider declares the variant and argument form. Bracket underscores encode spaces. |
| Conflicting utility classes | Class-list order does not change precedence; no conflict-merging helper runs. |
| A base reset | Select explicit global CSS. |
| `@apply` | Write the reusable declarations directly in a utility. |
| `bg-black/20`, group/peer or plugins | No generic opacity modifier, synthesized group/peer vocabulary or executable plugin system. Named tokens, literal rules and checked selectors cover specific needs. |
| Finding classes in application files | Java annotations provide explicit, checked requests, including separately compiled consumers. |

This is a CSS-oriented JVM language, with no Tailwind compatibility promise. Continue with [authoring rules](utility-rules.md) or the [worked recipes](utility-recipes.md).
