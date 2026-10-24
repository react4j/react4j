---
title: Utility rule recipes
---

These portable examples cover common table, panel and popover styles. Start with [using native utilities](native-utilities.md), then use the [language reference](utility-rules.md) when you need the exact argument and selector rules.

## A small recipe library

Save this complete source as `recipes.css` beside its Java provider. It defines every candidate used in the table and shadow examples below:

```css
@utility-rules;
@scale spacing .25rem;
@tokens color <color> {
  table-tint:rgba(var(--cs5),.05);
  overlay:rgb(0 0 0 / .2);
}
@tokens shadow <box-shadow> {
  footer:0 -1px 5px 1px rgba(var(--cs1),.2);
}
@utility "p-{size}" (size:scale(spacing)|arbitrary(<length-percentage>)) {
  padding:param(size);
}
@utility "w-{size}" (size:arbitrary(<length-percentage>)) { width:param(size); }
@utility w-full { width:100%; }
@utility "bg-{tone}" (tone:tokens(color)|arbitrary(<color>)) { background-color:param(tone); }
@utility "shadow-{value}" (value:tokens(shadow)|arbitrary(<box-shadow>)) { box-shadow:param(value); }
@utility "-ml-{size}" (size:scale(spacing)) { margin-left:calc(-1 * param(size)); }
@utility border-l { border-left-width:1px; border-left-style:solid; }
@utility sticky { position:sticky; }
@utility bottom-0 { bottom:0; }
@utility fill-panel {
  display:flex;
  flex-grow:1;
  width:100%;
  flex-direction:column;
  min-height:0;
  position:relative;
}
@variant th { & th { @slot; } }
@variant not-first { &:not(:first-child) { @slot; } }
@variant "nth-{position}" (position:arbitrary(<an+b>)) {
  &:nth-child(param(position)) { @slot; }
}
```

The provider makes its external hooks explicit:

```java
@UtilityCssLibrary(
    file = "recipes.css",
    externalProperties = {"cs1", "cs5"},
    externalClasses = {"iconStack"})
final class RecipeUtilities {}
```

Declare the file with `css_sources` as in the [setup walkthrough](native-utilities.md#add-your-own-source-to-a-build). The surrounding application must supply `--cs1` and `--cs5` in a compatible theme scope. These examples expect RGB channel lists such as `30, 40, 50`, rather than a complete `rgb(...)` value. The compiler preserves the variable substitution; it cannot prove the browser's eventual theme value is valid.

## Style table descendants from the table

Apply a getter with these candidates to the table element:

| Candidate | Effect |
| --- | --- |
| `[&_th]:p-3` | Give each header `.75rem` padding. |
| `[&_th]:bg-table-tint` | Use the named theme-backed header tint. |
| `[&_th]:not-first:border-l` | Add a left border to headers after the first. |
| `[&_th]:nth-[n+3]:border-l` | Add a left border from the third header onward. |
| `[&_th:not(:first-child):not(:nth-child(2))]:p-3` | Pad headers other than the first two. |
| `[&_td_div:not(.iconStack)]:w-full` | Make matching inner divs full-width, preserving the external `iconStack` hook. |

For example:

```java
@UtilityStyles(RecipeUtilities.class)
abstract class TableStyles {
    static TableStyles get() {
        return Styled_TableStyles.get();
    }

    @UtilityCss("[&_th]:p-3 [&_th]:bg-table-tint [&_th]:nth-[n+3]:border-l")
    abstract String style();
}
```

`&` means the element receiving the generated class. `_` becomes a space inside brackets, so `[&_th]` means that element's `th` descendants. No extra class is required on every header. `.iconStack` stays a literal external hook; append it explicitly where your component needs it.

Variant order has meaning. `th:not-first:border-l` selects header descendants other than the first child. Reversing the variants would first test the element carrying the utility class, then select its headers.

## Shadows, negative values and reusable panels

Both of these footer requests select the same intended shadow value:

```java
@UtilityCss("sticky bottom-0 shadow-footer")
abstract String footer();

@UtilityCss("sticky bottom-0 shadow-[0_-1px_5px_1px_rgba(var(--cs1),.2)]")
abstract String explicitFooter();
```

They keep distinct candidate identities. Named tokens make repeated values easier to change and review; bracketed values are useful for one-off requests. `-ml-2` means a negative half-rem margin because the library defines that negative operation explicitly. It does not make every scale accept negative multipliers.

`bg-overlay` replaces the common intention behind `bg-black/20`. There is no generic slash-opacity modifier. A library could instead declare the exact quoted literal `"bg-black/20"`; that would define one name, rather than a modifier system.

`fill-panel` keeps the six explicit declarations together. This replaces a utility-to-utility `@apply` recipe without introducing another macro language. Page-wide element rules still belong in selected globals; component-specific state selectors belong with the component.

## Scoped popovers

The runnable example supplies the complete [CSS rules](https://github.com/react4j/react4j/blob/master/examples/native-utilities/src/main/java/react4j/nativeutilities/utilities.css) and [Java component](https://github.com/react4j/react4j/blob/master/examples/native-utilities/src/main/java/react4j/nativeutilities/Workspace.java). Its roles map as follows:

| Element | Utility selection | Responsibility |
| --- | --- | --- |
| Card containing its trigger and panel | `card` | Applies `anchor-scope:--details` for that instance. |
| Details button | Base button styles plus `trigger` | Applies `anchor-name:--details` and names the panel through `popovertarget`. |
| Panel | `panel` plus color, spacing and motion utilities | Uses `position-anchor:--details`, explicit placement/flips/size limits, and native `popover="auto"`. |
| Close button | Base button styles | Uses the same target ID with `popovertargetaction="hide"`. |

The example's getter for the panel is:

```java
@UtilityCss("panel flex-col gap-4 p-5 bg-white text-ink transition-opacity opacity-1 starting:opacity-0 reduce-motion:transition-none")
abstract String panel();
```

The panel utility makes an open popover a flex container. The explicit opacity transition gives the starting style something to animate; reduced motion removes it. Closing is immediate in this recipe: animating a popover's exit needs additional authored display/overlay behavior.

The two cards share one checked anchor identity. Each card's DOM scope makes the browser resolve its own trigger. Entering the native top layer does not move the panel out of that DOM subtree. A portal rendered elsewhere needs a different association design.

HTML IDs, invoker attributes, labels and focus behavior remain component responsibilities. Native auto popovers supply light dismissal and Escape behavior in the example. They are non-modal: they do not trap keyboard focus or make the rest of the page inactive.

Built-in flip tactics need no named position-try resource, and viewport size limits let oversized content scroll. No fallback guarantees that every layout can fit. Exercise repeated instances, viewport edges, scrolling ancestors, long content, zoom, writing modes and keyboard behavior in your supported browsers.

## Default anchors in typed values

A component that already supplies its anchor association can expose these families:

```css
@utility "left-{value}" (value:arbitrary(<length-percentage>)) { left:param(value); }
@utility "min-w-{value}" (value:arbitrary(<length>)) { min-width:param(value); }
```

`left-[anchor(left)]` and `min-w-[anchor-size(width)]` use that default anchor. They do not establish a target or repair an incorrect association. `anchor()` cannot supply padding, so a request such as `p-[anchor(left)]` fails even though the function returns a length in a supported inset context.
