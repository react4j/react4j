# Native utility rules

This reference is for agents authoring rule libraries and developers selecting their utilities from Java. The compiler runs entirely on the JVM. Application code uses the existing CSS annotations; the language package is internal tooling embedded in the CSS processor jar.

Start a native input with `@utility-rules;`. Files without that marker retain the finite, flattened CSS behavior described in the [CSS guide](../README.md). Native syntax errors never trigger a finite-CSS fallback.

## Define a library and select utilities

```java
@UtilityCssLibrary(
    file = "app.utilities.css",
    externalProperties = {"cs1", "cs5"},
    externalClasses = {"iconStack"})
final class AppUtilities {}

@UtilityStyles(AppUtilities.class)
abstract class PanelStyles {
    static PanelStyles get() {
        return Styled_PanelStyles.get();
    }

    @UtilityCss("flex p-3 hover:bg-link")
    abstract String style();

    @UtilityCss("[&_th]:p-3 [&_td_div:not(.iconStack)]:w-[10%]")
    abstract String table();
}
```

Declare the package-relative file as an explicit CSS input using the existing `css_sources` rule. A literal Java text block is also supported for the library source. Utility requests use ordinary evaluated Java annotation constants, including inherited declarations. The [binding contract](../../docs/specs/css-bindings.md) governs interfaces, accessors and local component styles.

```css
@utility-rules;
@scale spacing .25rem;
@tokens space <length-percentage> { panel:1.25rem; }
@tokens color <color> { link:#3972aa; tint:rgba(var(--cs5),.05); }

@utility flex { display:flex; }
@utility "p-{size}" (size:scale(spacing)|tokens(space)|arbitrary(<length-percentage>)) {
  padding:param(size);
}
@utility "w-{size}" (size:scale(spacing)|arbitrary(<length-percentage>)) {
  width:param(size);
}
@utility "-ml-{size}" (size:scale(spacing)) { margin-left:calc(-1 * param(size)); }
@utility "bg-{tone}" (tone:tokens(color)|arbitrary(<color>)) { background-color:param(tone); }
@utility border-l { border-left-width:1px; border-left-style:solid; }
@variant hover { @media (hover:hover) { &:hover { @slot; } } }
```

`p-3` gives `.75rem`, `p-1.5` gives `.375rem`, `p-panel` gives `1.25rem`, and `p-[10%]` gives `10%`. Scale multipliers are nonnegative decimals with a leading digit; negative behavior is explicit in `-ml-2`. A library can expose property, ID and animation accessors through the existing annotations. `@CssStable` on a provider preserves all owned names, including subsequently requested candidates; a stable resource accessor preserves its selected owned symbol. Consumer stability affects its local markers.

Ordinary local CSS keeps precedence over utilities. No reset is implicit, and class-list order does not resolve conflicting utilities. Keep component-specific overrides in local CSS or distinct state getters.

## Grammar

```ebnf
library        = "@utility-rules", ";", { definition } ;
definition     = scale | tokens | utility | variant | support ;
scale          = "@scale", identifier, positive-scalar, ";" ;
tokens         = "@tokens", identifier, value-type, "{", { token-key, ":", css-value, ";" }, "}" ;
utility        = "@utility", head, "{", utility-template, "}" ;
variant        = "@variant", head, "{", variant-template, "}" ;
head           = literal-name | quoted-literal
               | quoted-pattern, "(", identifier, ":", sources, [ "within", interval ], ")" ;
sources        = source, { "|", source } ;
source         = "tokens(", identifier, ")" | "scale(", identifier, ")"
               | "bare(", value-type, ")" | "arbitrary(", value-type, ")" ;
interval       = "[", bound, ",", bound, "]" ;
bound          = numeric-literal | "*" ;
candidate      = { variant, ":" }, utility ;
```

A family has one named capture at the end, following `-` or `/`, with one matching parameter declaration. Argument forms have compatible types and disjoint spellings. Duplicate heads, overlapping sources, literal/family collisions and tokens hidden by a longer prefix fail even when unused. Matching chooses an exact literal or the longest family prefix; a bad argument never falls through to a shorter prefix.

Candidate lists split on HTML whitespace. Colons inside balanced brackets, functions or strings stay inside their value. Within brackets every unescaped `_` becomes a space, including inside strings; `\_` stays an underscore. For example, `content-['Save_changes']` supplies `'Save changes'`. Values cannot inject declarations or rules, and `!important`/important modifiers are rejected.

Supporting definitions are `@keyframes`, `@font-face`, `@property` and `:root` custom-property initialization. Ordinary element defaults belong in an explicitly selected global definition. Class hooks in selectors require `externalClasses` permission; asset URLs and imported symbols keep their [checked ownership rules](../../docs/specs/web-assets.md).

## Values and bounds

| Type | Example argument | Notes |
| --- | --- | --- |
| `<integer>` | `-2`, `[calc(10px/2px)]` | Bare literals use signed decimal digits. |
| `<number>` | `.5`, `[clamp(0,var(--x),1)]` | Dimensional math must produce a number. |
| `<percentage>` | `25%` | A percentage is distinct from a number. |
| `<length>` | `2rem`, `[anchor-size(width)]` | Unitless zero is allowed outside math. |
| `<length-percentage>` | `[calc(100%_-_2rem)]` | Compatible length/percentage arithmetic. |
| `<angle>` | `45deg` | Degree, radian, grad and turn units. |
| `<time>` | `150ms`, `-.1s` | Rules choose whether negative values are allowed. |
| `<ratio>` | `16/9` | The slash belongs to one value. |
| `<string>` | `['Save_changes']` | One quoted CSS string. |
| `<color>` | `[#3972aa]`, `[rgba(var(--cs5),.05)]` | Named colors and supported CSS color functions. |
| `<box-shadow>` | `[0_-1px_5px_1px_rgba(var(--cs1),.2)]` | Checked length/color/inset structure. |
| `<image>` | `[linear-gradient(red,blue)]` | Checked URLs, gradients and image-set options. |
| `<position>` | `[right_1rem_bottom_2rem]` | One to four supported position components. |
| `<grid-tracks>` | `[200px_minmax(0,1fr)]` | Tracks, line names, repeat and subgrid. |
| `<an+b>` | `[n+3]` | Static selector input only. |

Bare sources admit integer, number, percentage, length, length-percentage, angle, time and ratio values. Compound values require tokens or brackets. CSS-wide keywords are outside these input types; expose them through literal utilities when needed. An image type does not turn `bg-*` into an inferred image family: use a distinct head such as `bg-image-*`.

Color functions include rgb/rgba, hsl/hsla, hwb, lab/lch, oklab/oklch and predefined color spaces through color(). Numeric math supports calc/min/max/clamp with dimensional sums, products and quotients. Gradient direction, shape, interpolation and stops are checked; image-set accepts checked image/string sources, resolution and type descriptors. Grid tracks include minmax, fit-content, line names and checked repeat structure. Unsupported functions fail explicitly.

Declaration-value types retain unresolved `var()` substitution. A channel list such as `rgba(var(--cs5),.05)` stays a channel list. A browser-owned variable may still compute to an invalid value: type checks do not evaluate browser state or implement every CSS property's value grammar.

```css
@scale duration 1ms;
@utility "duration-{v}" (v:scale(duration)|arbitrary(<time>) within [0ms,*]) {
  transition-duration:param(v);
}
@utility "opacity-{v}" (v:bare(<number>)|arbitrary(<number>) within [0,1]) {
  opacity:param(v);
}
@utility "cols-{v}" (v:arbitrary(<grid-tracks>)) { grid-template-columns:param(v); }
@utility "aspect-{v}" (v:bare(<ratio>)) { aspect-ratio:param(v); }
```

Bounds are inclusive literal endpoints; `*` is unbounded and at least one endpoint must be bounded. They apply to integer, number, percentage, length, length-percentage, angle and time inputs. Literal/scale comparisons use exact decimal arithmetic and fixed-ratio unit conversions. Relative units, variables and unevaluated CSS math may leave a check `browser-dependent`; the expansion trace records that result. Bounds constrain the input, not expressions the template later constructs, and add no clamp or runtime evaluator.

## Selectors and variants

```css
@variant th { & th { @slot; } }
@variant not-first { &:not(:first-child) { @slot; } }
@variant "nth-{position}" (position:arbitrary(<an+b>)) {
  &:nth-child(param(position)) { @slot; }
}
```

`th:hover:bg-link` styles a hovered `th`; `hover:th:bg-link` styles `th` descendants of the hovered target. The first written variant stays outermost. `[&_th]:nth-[n+3]:border-l` is a guarded selector transformation on the element receiving the generated class.

A variant has exactly one `@slot;`, surrounded only by checked selector, media, supports, container or starting-style wrappers. It contributes no declarations. Utilities can contain declarations and those wrappers, but no slot. Each nested selector has one positive `&` outside functions in its first compound; rule-level lists and additional guards are rejected. Selector functions can contain lists. Holes are permitted only in complete typed declaration values, the nth-child argument for an+b, or the size thresholds below.

Expanded candidates sort by variant count, written variant-rank sequence, utility definition order and full candidate spelling in Unicode code-point order. Arbitrary selectors rank after named variants and use decoded selector spelling as a secondary key. This ordering preserves browser specificity and authored declaration order; it does not merge conflicts.

## Containers and starting styles

```css
@tokens thresholds <length> { small:24rem; large:40rem; }
@utility card-container { container:card / inline-size; --density:cozy; }
@utility density-compact { --density:compact; }
@utility scroll-container { container-type:scroll-state; }

@variant "card-min-{v}" (v:tokens(thresholds)|arbitrary(<length>) within [0,*]) {
  @container card (inline-size >= param(v)) { @slot; }
}
@variant compact { @container card style(--density:compact) { @slot; } }
@variant stuck-top { @container scroll-state(stuck:top) { @slot; } }
@variant scrollable-down { @container scroll-state(scrollable:bottom) { @slot; } }
@variant starting { @starting-style { @slot; } }
@utility transition-opacity { transition:opacity 150ms; }
@utility opacity-1 { opacity:1; }
@utility opacity-0 { opacity:0; }
```

`card-min-small:p-3` uses the eligible named ancestor; `compact:p-3` tests its custom property. Size, custom-property style and scroll-state conditions admit boolean combinations and nesting. Scroll-state features are stuck, snapped, scrollable and scrolled with their standard values. Threshold holes must fill a complete dimension length or aspect ratio value. Names, feature/property names, operators, whole conditions, style tests and scroll-state tests stay static. Percentages are not width thresholds. One container query is allowed per wrapper; nested queries remain separate.

The compiler registers static container names from all templates, so a query remains valid without requesting its setup utility. It neither creates an ancestor nor retains that setup class. Apply containment, sticky positioning, overflow and custom properties explicitly. Browser-owned ancestors require `externalContainers`; style properties need owned/imported/external property permission. These follow [container query semantics](https://www.w3.org/TR/css-conditional-5/#container-rule), including nearest eligible ancestor selection.

```java
@UtilityCss("transition-opacity opacity-1 card-min-small:starting:opacity-0")
abstract String entering();
```

Starting style takes no condition and adds no transition automatically. It remains inside the named query in this example. Browser entry/exit, display behavior and support remain application/browser concerns.

## Named anchors and popovers

```css
@utility popover-scope { anchor-scope:--trigger; }
@utility popover-trigger { anchor-name:--trigger; }
@utility popover-panel {
  position:fixed;
  position-anchor:--trigger;
  inset:auto;
  margin:0;
  padding:0;
  border:0;
  position-area:block-end span-inline-end;
  position-try-fallbacks:flip-block,flip-inline,flip-block flip-inline;
  min-inline-size:0;
  max-inline-size:calc(100dvw - 2rem);
  max-block-size:calc(100dvh - 2rem);
  overflow:auto;
}
@utility popover-match-width { inline-size:anchor-size(width); }
```

Apply scope to a wrapper containing both trigger and panel, trigger to the control and panel/match-width to the popover. The panel can enter the native top layer while retaining that DOM scope. A panel rendered elsewhere through a portal needs a different association design. HTML IDs, opening/closing and focus management remain component responsibilities.

Anchors have a separate naming namespace from custom properties. Static declarations from all templates establish owned identities; position-anchor, named anchor-scope lists, anchor() and anchor-size() references bind consistently, including nested math and fallbacks. External anchor hooks use full spelling: `externalAnchors={"--host-trigger"}`. `externalProperties={"host-trigger"}` has a different meaning. Declaring a hook neither creates a target nor proves it exists.

Name/side/size substitutions are static: no name holes or var-supplied selectors, including visible custom-property carriers. Anchor sides may use literal percentages. Supported inset properties admit anchor(); sizing, inset and margin properties admit anchor-size(). The compiler checks literal and substituted contexts, including fallback expressions. Default-anchor inputs such as `left-[anchor(left)]` and `min-w-[anchor-size(width)]` remain possible when the library defines those typed families.

The [browser fixture](../examples/save-button/src/main/java/react4j/css/example/NativeExample.java) and [tests](../examples/save-button/src/test/java/react4j/css/example/CssBrowserTest.java) exercise repeated scopes, default anchor sizing, a bottom-edge flip, explicit oversized-content limits and scrolling, in development and optimized output. Syntax and binding follow [CSS anchor positioning](https://www.w3.org/TR/css-anchor-position-1/); browser support remains separate. No fallback is guaranteed to fit every layout. Check all edges, scrolling ancestors, zoom, writing modes and focus behavior before adopting a recipe. This work does not migrate Rose or establish that its existing popover bugs are fixed.

## Authoring commands and diagnostics

The shipped CSS processor jar exposes the JVM main class `react4j.css.processor.UtilityRules`:

```sh
java -cp css-processor.jar react4j.css.processor.UtilityRules validate app.utilities.css
java -cp css-processor.jar react4j.css.processor.UtilityRules describe app.utilities.css provider.css.rules.json
java -cp css-processor.jar react4j.css.processor.UtilityRules expand provider.css.rules.json p-3 'hover:bg-[red]'
java -cp css-processor.jar react4j.css.processor.UtilityRules --text expand provider.css.rules.json flxe
```

The optional checked provider snapshot supplies ownership, imports, hooks and stability from Java compilation. Validation without one reports `prepared` and pending ownership checks; it does not claim a checked library. Checked validation includes a reusable descriptor under `library`. Description adds tokens, scales, families, wrappers, bounds and origins. Expansion returns CSS/maps, defining-owner identities, ordering keys, traces, deferred checks and every batch error while retaining successful peers. JSON is default; `--text` selects human output. Exit codes are 0 for success, 1 for rule/candidate failures and 2 for input/usage/descriptor failures.

For repository development, use `bazel run //css/processor/src/main/java/react4j/css/processor:utility_rules -- ...` with absolute input paths, such as `"$PWD/app.utilities.css"`; Bazel launches from runfiles. No application scanner, watcher or extra executable is needed.

| Code | Meaning/action |
| --- | --- |
| `U_UNKNOWN_UTILITY`, `U_UNKNOWN_VARIANT` | Check suggested declared names. Suggestions never change the result. |
| `U_ARGUMENT`, `U_VALUE_TYPE` | Inspect the family, accepted sources, named tokens and decoded payload span. |
| `U_VALUE_RANGE`, `U_RANGE_BOUND` | Correct the requested value or the provider's compatible inclusive bounds. |
| `U_VALUE_CONTEXT`, `U_ANCHOR_FUNCTION` | Use anchor functions in admitted declaration contexts with static selectors and valid fallbacks. |
| `U_CONTAINER_CONDITION`, `U_STARTING_STYLE` | Correct the condition grammar or remove a starting-style condition. |
| `U_CONTAINER_NAME`, `U_ANCHOR_NAME`, `U_DYNAMIC_NAME` | Declare a static owned/external name; setup remains explicit. |
| `U_EXTERNAL_PROPERTY`, `U_EXTERNAL_CLASS`, `U_ASSET_REFERENCE` | Supply the relevant explicit hook/import rather than bypassing ownership. |
| `U_AMBIGUOUS_RULE`, `U_SOURCE_TYPE`, `U_PARAMETER`, `U_SLOT` | Correct conflicting definitions, source types, parameter positions or slot shape. |
| `U_SELECTOR_GUARD`, `U_IMPORTANT` | Keep a positive first-compound guard and ordinary local precedence. |
| `U_SYNTAX`, `U_CANDIDATE_SYNTAX` | Correct provider or candidate delimiters/strings. |
| `U_INPUT`, `U_USAGE`, `U_DESCRIPTOR` | Check explicit files, command arguments and descriptor shape/version. |

Diagnostics include available primary and related token/definition origins. Literal Java requests recover payload spans; constants/concatenations report their expression and decoded offset. [Exact identifier-token source mapping](../../docs/deferred/css-identifier-token-mapping.md) remains outside the promise.

## Delivery and boundaries

The pure facade is `react4j.css.utility.language.UtilityLanguage`; its source/build package owns grammar, matching, values, composition and feedback. Java annotation orchestration and shared symbol/delivery infrastructure stay outside it. The existing processor distribution shades the module; it adds no application-facing runtime or Maven artifact.

Explicit native file packaging produces a prepared `.rules.json` sidecar. Provider compilation publishes a checked `.css.rules.json` descriptor plus support CSS and full owned-name identities. Consumers publish `.css.fragments.json` contributions for their explicit requests. Independently compiled consumers can request unseen values; collection deduplicates matching contributions and rejects provider fingerprint/content conflicts. Linker and bundler consume generic CSS metadata without depending on the language package. Development includes requested expansions; optimized output prunes through surviving getters. Unrequested infinite families are never enumerated.

The [native contract](../../docs/specs/native-utility-language.md) is the durable behavior owner. There is no Tailwind compatibility promise, generic regex/multi-capture system, apply macro graph, generic slash modifier, arbitrary property/at-rule variant, executable plugin or synthesized group/peer vocabulary. Named position-try definitions/bindings and dynamic owned names are outside this grammar. A literal utility can still express ordinary CSS values and explicit browser behavior.
