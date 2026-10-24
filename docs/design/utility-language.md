# JVM utility language design

This design is for rule-library authors and compiler maintainers reviewing a JVM-only utility compiler. Agents author declarative rules; application developers select them through checked utility classes. The agreed scope includes typed values, numeric bounds, container queries, starting styles and named anchors. The grammar, APIs and package below are proposed for review; none is implemented by this document.

The current [Utility CSS library](../glossary/README.md#utility-css-library) consumes finite, flattened CSS. This design replaces that restriction for native rule inputs while preserving finite CSS as a useful input in its own right. It would require changes to the [utility contract](../specs/utility-css.md), [bindings](../specs/css-bindings.md), [liveness](../specs/css-liveness.md), and [publisher-only generation decision](../adr/0004-consume-finite-utility-libraries.md) before implementation. Those documents continue to describe the existing implementation.

## Two authoring interfaces

An agent defines the vocabulary: tokens, numeric scales, utility families, selector transformations and conditions. The compiler provides validation and expansion traces. An application author selects that vocabulary through `@UtilityCss`, with useful diagnostics when a candidate is invalid.

The proposed format is CSS with a few declarative constructs. Compared with the alternatives:

| Rule-author interface | Representative definition | Responsibility and cost |
| --- | --- | --- |
| CSS-oriented rules, recommended | `@utility "p-{size}" (size: scale(spacing)) { padding: param(size); }` | The JVM compiler parses and expands data. CSS, accepted arguments and naming stay together. A small additional grammar is required. |
| Java builders or callbacks | Java code registering a matcher and an expansion | Java handles authoring syntax and can express any algorithm. Publishing needs executable author code or a separate descriptor-generation action; effects, introspection and callback semantics enlarge the contract. |
| Structured JSON descriptors | A record containing a prefix, argument source and declaration templates | A schema checks the outer structure, but CSS becomes escaped strings or a second AST vocabulary. Selector composition and CSS validation still need a compiler. |

Each option could run entirely on the JVM. CSS-oriented rules give agents direct access to the notation they are modifying, and the same definition can drive class documentation, diagnostics and expansion. There is no Tailwind compatibility promise.

## Source boundary and Java use

A native rule source starts with `@utility-rules;`, after optional whitespace and CSS comments. The marker selects this grammar before parsing; a syntax error never triggers a retry as another input language. A source without the marker remains an ordinary finite CSS input. Native files express class rules with `@utility`; they do not mix in ordinary authored `.class` rules.

Retain the existing Java source-selection and ownership options:

```java
@UtilityCssLibrary(
    file = "rose.utilities.css",
    externalProperties = {"cs1", "cs5"},
    externalClasses = {"iconStack"})
final class RoseUtilities {}

@UtilityStyles(RoseUtilities.class)
abstract class PanelStyles {
    static PanelStyles get() {
        return Styled_PanelStyles.get();
    }

    @UtilityCss("flex flex-col gap-1 p-3 basis-130")
    abstract String style();
}
```

The file is an explicit, package-relative input. The existing inline text-block form could carry the same grammar. There is one self-contained rule source per provider in the first grammar; recursive imports, theme overlays and executable plugins are outside this proposal.

For native sources, external declarations describe permitted browser-owned hooks. A provider may declare a hook that is first used by a downstream candidate; it cannot require that all such uses already occur in the provider source. This differs from validating an external hook against a finite selector inventory.

## A separate language module

Place native language code in the proposed Java package `react4j.css.utility.language`, under `css/utility-language/src/main/java/react4j/css/utility/language/`. Give it one package-local Bazel `java_library` declaration with explicit sources and visibility limited to the processor, authoring entry point and tests. It is an internal tooling module, not an application API. These paths and targets do not yet exist.

The module owns native grammar, value recognizers, numeric sources and bounds, template validation, candidate matching, variant composition, deterministic ordering, descriptor encoding and language diagnostics. It returns origin-bearing flattened CSS and generic symbol/dependency information. Callers do not manipulate its parse tree or repeat its matching rules.

The shared caller contract is:

| Operation | Input | Result and failure boundary |
| --- | --- | --- |
| Prepare | Source text and a logical source identity with offsets | Immutable prepared rules, or all independent source diagnostics. Parse and validate definitions without Java annotations; ownership checks requiring provider bindings remain pending. |
| Bind / validate | Prepared rules plus a plain snapshot of provider owner, permitted external names and selected symbol/asset imports | An immutable checked library descriptor, or diagnostics with related definition locations. Validate every definition, including unused ones, before publishing a descriptor. |
| Describe | A checked descriptor | Declared literal names, families, tokens, types, numeric bounds and variants. Infinite domains remain descriptions, not enumerations. |
| Expand | A checked descriptor and an explicit batch of decoded candidates with request origins | Independent candidate results: flattened CSS, canonical candidate identity, ordering key, generic symbol occurrences/dependencies and an expansion trace, or structured diagnostics. A failed candidate does not suppress other candidate errors; Java compilation still fails if any candidate fails. |

Only checked descriptors can be expanded. Owner identity and import selections come from the caller; the module constructs its own parser and expansion machinery. Inputs and descriptors are immutable. Reusing a descriptor never retains candidates from an earlier batch, and output depends only on the supplied descriptor and current requests. Preparation validates finite definitions and indexes names; description never enumerates infinite domains. Expansion work follows the requested batch and emitted CSS, not the number of possible classes. No operation reads files, scans source directories, invokes a process or emits Java compiler messages. Descriptor bytes and semantics versions are validated on loading; malformed or unsupported versions fail explicitly. Ordinary input failures use diagnostic results; I/O and Java element locations belong to the caller. Resource exhaustion remains a host failure, not an unknown-utility diagnostic.

Annotation processing loads the source, evaluates Java annotation values, supplies binding snapshots and renders returned diagnostics at Java locations. An authoring command uses the same facade for validation, description and expansion. File packaging uses preparation to replace the finite class-list sidecar with a native prepared-rule sidecar; provider compilation binds and validates it before publishing an authoritative descriptor. A prepared sidecar alone is not evidence that provider ownership checks passed.

The dependency direction is `processor / authoring entry point -> utility.language -> css.internal`. The bundler depends on shared CSS metadata, not on the language grammar. Keep generic CSS parsing, name binding, symbol identities, source origins, URL checks and delivery metadata in shared CSS infrastructure. Annotation-free ownership logic currently concentrated in the processor will need to become reusable there; the language package must not depend back on `CssCompiler`, javac elements or annotation processing. This is a focused integration obligation, not a plan to move the whole CSS compiler.

Three interface shapes were considered against the same deterministic expansion, ownership, origin and error requirements:

| Shape | Representative caller and ownership | Hidden behavior and tradeoff |
| --- | --- | --- |
| Embedded facade, selected | Processor supplies source/bindings, then requests explicit expansion batches. The authoring command calls the same operations. Callers own source loading; the module owns parsing and expansion. | Hides grammar, phase ordering within preparation/binding, matching and composition behind immutable results. Gives both callers the same diagnostics with no additional execution seam. |
| Public compiler phases and parse tree | Processor and authoring command each call parse, type-check, match, compose and lower, carrying intermediate trees and errors between phases. Callers own orchestration and caching. | Hides individual algorithms but exposes their sequencing and representations. Grammar changes spread to callers, reducing locality without a current caller that needs custom phases. |
| JVM batch process and file protocol | Processor launches a tool with source/descriptor/candidate files, reads expansion records and translates exit status and stderr. Assembly selects the executable; callers own files and process lifecycle. | Hides the complete compiler but adds I/O, process failures, protocol synchronization and toolchain selection. It remains JVM-only, but neither current caller needs a separate process. |

The facade earns its boundary by hiding the language's interacting rules. Removing it would spread typed matching, selector composition, order and diagnostics into both callers. A callback/plugin interface would add a seam with no independently useful alternative implementation; it is unnecessary.

Separate source and build structure does not imply a separate release or runtime service. Package the language into the existing JVM processor/tool distribution, with the current release tool owning any relocation needed for shared CSS classes. Do not introduce a new Maven dependency, process, browser payload or independent version lifecycle merely to obtain a package boundary. Descriptor semantics are versioned within the toolchain.

The boundary is deliberately incomplete at delivery metadata: candidate fragments, registered name universes and the new anchor symbol kind must also reach shared catalogues, naming and liveness. Those are cross-cutting CSS contracts, not reasons to put language parsing into the bundler. Future tests should call the facade for language behavior and use processor/link integration tests for binding snapshots, source maps, separately compiled consumers and surviving getters.

## Spacing: one scale, explicit families

```css
@utility-rules;

@scale spacing .25rem;

@tokens space <length-percentage> {
  panel: 1.25rem;
}

@utility flex { display: flex; }
@utility flex-col { flex-direction: column; }

@utility "p-{size}" (size: scale(spacing) | tokens(space) | arbitrary(<length-percentage>)) {
  padding: param(size);
}

@utility "px-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) {
  padding-inline: param(size);
}

@utility "gap-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) {
  gap: param(size);
}

@utility "basis-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) {
  flex-basis: param(size);
}

@utility "w-{size}" (size: arbitrary(<length-percentage>)) {
  width: param(size);
}

@utility w-full { width: 100%; }

@utility "-m-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) {
  margin: calc(-1 * param(size));
}

@utility "-ml-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) {
  margin-left: calc(-1 * param(size));
}

@utility "-my-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) {
  margin-block: calc(-1 * param(size));
}
```

These are complete definitions for the spacing examples below. They are not an inventory of every spacing utility a shipped library should contain.

| Candidate | Expansion before selector naming |
| --- | --- |
| `p-3` | `padding: .75rem` |
| `p-1.5` | `padding: .375rem` |
| `p-panel` | `padding: 1.25rem` |
| `basis-130` | `flex-basis: 32.5rem` |
| `w-[10%]` | `width: 10%` |
| `px-[1rem]` | `padding-inline: 1rem` |
| `-ml-2` | `margin-left: calc(-1 * .5rem)` |
| `-my-[0.25em]` | `margin-block: calc(-1 * 0.25em)` |

`scale(spacing)` accepts a nonnegative decimal multiplier. Its positive literal length step makes the result a length; multiplication uses exact decimal arithmetic and retains its unit. Scales can also have positive literal number, percentage, angle or time steps. There is no implicit spacing scale built into class names. `basis-130` is valid without enumerating 130 tokens, and `p-5` is valid under this definition. A missing-token error applies to a token-only family, not this numeric family.

Named tokens are compile-time CSS values. They do not automatically become CSS custom properties. Token and scale references must be declared in the same provider. The first grammar has no token-to-token references or user-defined evaluation functions.

Leading minus has no global meaning. The `-m-`, `-ml-` and `-my-` families above explicitly implement it. A library can expose negative margins without accidentally exposing negative padding through a numeric scale.

## Colors and runtime theme values

These definitions extend the same native source:

```css
@tokens color <color> {
  white: #fff;
  gray-200: #e5e7eb;
  link: #3972aa;
  link-strong: #2e5b88;
  ink: rgb(var(--cs1));
  table-tint: rgba(var(--cs5), .05);
  overlay: rgb(0 0 0 / 20%);
}

@utility "bg-{tone}" (tone: tokens(color) | arbitrary(<color>)) {
  background-color: param(tone);
}

@utility "text-{tone}" (tone: tokens(color) | arbitrary(<color>)) {
  color: param(tone);
}

@utility text-sm {
  font-size: .875rem;
  line-height: 1.25rem;
}

@utility "border-{tone}" (tone: tokens(color) | arbitrary(<color>)) {
  border-color: param(tone);
}

@utility border-l {
  border-left-width: 1px;
  border-left-style: solid;
}
```

`text-link` and `text-[#3972aa]` both set the color. `text-sm` is a separately declared literal utility. There is no general inference that `text-*` means typography or that `bg-*` means a background image. A library introducing another value kind should use a distinct family such as `text-size-*` or `bg-image-*`.

`tokens(color)` supplies named values; `arbitrary(<color>)` supplies bracketed values. Thus `bg-#3972aa` is an invalid argument, `bg-[12px]` has the wrong value type, and `bg-missing` does not fall through to raw CSS.

Rose uses `rgba(var(--cs5),0.05)` and shadows containing `rgba(var(--cs1),0.2)`. These expressions consume channel lists. The compiler must preserve the expression and browser variable lookup, rather than replacing it with `var(--cs5)` or treating the channel variable as a complete color. Their external ownership is explicit in the provider declaration.

Types constrain candidate inputs, not all CSS semantics. A `<color>` accepts supported color literals/functions and an unresolved `var()` in a color position. It rejects a known length such as `12px`; it cannot prove that a browser-owned variable will resolve to a color. The same limit applies to lengths, ranges and shadow values. Ordinary CSS declarations remain subject to the existing syntax and ownership checks; this design does not promise a complete CSS property-value checker. [CSS variable substitution](https://www.w3.org/TR/css-variables-1/#using-variables) remains browser behavior.

## Broader values and compact arguments

A single parameter can contain a complete CSS value. A grid track list or image function does not need several captures, and its internal commas or slashes do not introduce utility modifiers.

```css
@scale duration 1ms;
@scale rotation 1deg;

@utility "z-{value}" (value: bare(<integer>) | arbitrary(<integer>)) {
  z-index: param(value);
}

@utility "opacity-{value}" (value: bare(<number>) | arbitrary(<number>) within [0, 1]) {
  opacity: param(value);
}

@utility "duration-{value}" (value: scale(duration) | arbitrary(<time>) within [0ms, *]) {
  transition-duration: param(value);
}

@utility "delay-{value}" (value: arbitrary(<time>)) {
  transition-delay: param(value);
}

@utility "rotate-{value}" (value: scale(rotation) | arbitrary(<angle>)) {
  rotate: param(value);
}

@utility "aspect-{value}" (value: bare(<ratio>) | arbitrary(<ratio>)) {
  aspect-ratio: param(value);
}

@utility "content-{value}" (value: arbitrary(<string>)) {
  content: param(value);
}

@utility "bg-image-{value}" (value: arbitrary(<image>)) {
  background-image: param(value);
}

@utility "bg-position-{value}" (value: arbitrary(<position>)) {
  background-position: param(value);
}

@utility "grid-cols-{value}" (value: arbitrary(<grid-tracks>)) {
  grid-template-columns: param(value);
}
```

| Candidate | Expansion or diagnostic |
| --- | --- |
| `z-2` | `z-index: 2` |
| `opacity-0.7` | `opacity: 0.7` |
| `opacity-1.5` | `U_VALUE_RANGE`: outside the library's chosen interval `[0, 1]`; CSS itself permits clamping opacity. |
| `duration-150` | `transition-duration: 150ms` |
| `duration-0.5` | `transition-duration: 0.5ms`; the rule defines milliseconds, not seconds. |
| `duration-[-1ms]` | `U_VALUE_RANGE`: a known negative duration is outside `[0ms, *]`. |
| `delay-[-100ms]` | `transition-delay: -100ms`; a negative delay is meaningful CSS. |
| `rotate-45` | `rotate: 45deg` |
| `aspect-16/9` | `aspect-ratio: 16 / 9`; the whole ratio is one argument. |
| `content-['Save_changes']` | `content: 'Save changes'`; apply it to a suitable element or explicit pseudo-element selector. |
| `bg-image-[linear-gradient(90deg,#fff,#000)]` | One `background-image` value. |
| `bg-position-[right_1rem_bottom_2rem]` | `background-position: right 1rem bottom 2rem` |
| `grid-cols-[200px_minmax(0,1fr)]` | `grid-template-columns: 200px minmax(0,1fr)` |

`bare(type)` accepts one literal scalar numeric value or ratio without brackets. Allowed bare types are `<integer>`, `<number>`, `<percentage>`, `<length>`, `<angle>`, `<time>` and `<ratio>`. It performs no unit conversion or name inference. Numeric literals use CSS numeric token syntax; integers require a signed sequence of decimal digits. A ratio is one nonnegative number with an optional `/` and second nonnegative number, with the omitted denominator equal to one. Functions, variables and compound values require `arbitrary(type)` brackets. A signed bare argument is still an argument of its declared family, not a global negative-class modifier.

A scale accepts the unsigned decimal spelling already defined for spacing. Its step is one positive literal number, percentage, length, angle or time; it cannot be a function, variable or ratio. The output type follows that step. Thus `duration-150` and `rotate-45` get their units from these definitions. Do not broaden a numeric scale to produce integers by rounding; use `bare(<integer>)` for integers.

An optional `within [lower, upper]` applies to the resolved parameter after token, bare or scale conversion and before substitution. Endpoints are inclusive; `*` means unbounded. Bounds are literal numeric values compatible with the common parameter type; unitless zero is compatible with any dimension. At least one endpoint must be bounded, and a statically inverted interval is a provider error. Intervals apply to numbers, integers, percentages, lengths, length/percentages, angles and times; they do not constrain colors, ratios or compound values. The bound limits an input, not an arbitrary expression the template later constructs from that input.

Check statically comparable literal/scale values using exact decimal arithmetic and fixed-ratio unit conversions. Values depending on font metrics, layout, browser variables or unevaluated CSS math cannot always be compared at compilation. Preserve them and report the unresolved bound check in the expansion trace; do not inject a clamp, fallback or runtime code. A range is therefore an early error for known bad inputs, not a promise about every computed browser value. Library authors choose limits for each rule: the [transition specification](https://www.w3.org/TR/css-transitions-1/#transition-duration-property) gives duration and delay different ranges.

## Variants: applicability without hidden declarations

```css
@variant hover {
  @media (hover: hover) {
    &:hover { @slot; }
  }
}

@variant focus-visible {
  &:focus-visible { @slot; }
}

@variant sm {
  @media (min-width: 40rem) { @slot; }
}

@variant not-first {
  &:not(:first-child) { @slot; }
}

@variant "nth-{position}" (position: arbitrary(<an+b>)) {
  &:nth-child(param(position)) { @slot; }
}

@variant th {
  & th { @slot; }
}

@variant "*" {
  & > * { @slot; }
}
```

Each variant contains exactly one `@slot`, surrounded only by checked selector, `@media`, `@supports`, `@container` or `@starting-style` wrappers. It introduces no declarations. A utility can contain declarations, selectors and those same wrappers, but no slot. Nested selector rules have one selector, not a selector list; functions such as `:is(...)` may still contain lists. Sibling rules in a utility are permitted.

Selector wrappers have exactly one positive `&`, outside selector functions, in their first compound. For example, `& th` and `&:not(:first-child)` are valid; `body`, `:not(&)` and `& + &` are not. This bounded subset covers the selected Rose cases and keeps the generated class as an explicit guard. It does not claim all native CSS nesting syntax. Flattening must preserve selector meaning and specificity; the [CSS nesting specification](https://www.w3.org/TR/css-nesting-1/#nest-selector) supplies the reference semantics.

An outer wrapper's full selector replaces `&` in the next inner wrapper. Written order is outermost first:

| Candidate | Symbolic selector and conditions |
| --- | --- |
| `sm:hover:bg-link` | `@media (min-width: 40rem)` then `@media (hover: hover)`, selector `.C:hover` |
| `th:hover:bg-link` | `@media (hover: hover)`, selector `.C th:hover` |
| `hover:th:bg-link` | `@media (hover: hover)`, selector `.C:hover th` |
| `th:not-first:border-l` | `.C th:not(:first-child)` |
| `[&_th]:nth-[n+3]:border-l` | `.C th:nth-child(n+3)` |

Here `.C` is the unique managed class for the complete candidate, not a literal class emitted by the compiler. A variant chain is not sorted or simplified. `hover:th` and `th:hover` express different requests.

Bracketed selector variants are part of the candidate language. `[&_th]` decodes to the checked wrapper `& th`, with the same restrictions as a named selector variant. This preserves Rose's existing descendant forms while allowing a library to offer the shorter `th` name. Arbitrary at-rule variants are excluded from the first grammar; named variants already express conditions.

Referenced classes and IDs other than `&` must resolve through existing ownership declarations. For this first grammar, literal class hooks in selector templates and arbitrary selectors must be declared in `externalClasses`; they are not inferred as newly owned utility classes. Consequently, `[&_td_div:not(.iconStack)]:w-full` keeps the literal `.iconStack` hook only when that hook is explicitly external. Generic group/peer markers and typed references to other owned classes need a separate design.

## Container queries

Support size, custom-property style and scroll-state queries through named variant definitions. A family can accept a checked threshold without accepting an arbitrary condition or arbitrary container name:

```css
@tokens container-size <length> {
  sm: 24rem;
  lg: 40rem;
}

@utility card-container {
  container-name: card;
  container-type: inline-size;
}

@utility density-compact { --density: compact; }
@utility scroll-container { container-type: scroll-state; }

@variant "cq-min-{size}" (size: tokens(container-size) | arbitrary(<length>) within [0, *]) {
  @container (inline-size >= param(size)) { @slot; }
}

@variant "card-min-{size}" (size: tokens(container-size) | arbitrary(<length>) within [0, *]) {
  @container card (inline-size >= param(size)) { @slot; }
}

@variant card-compact {
  @container card style(--density: compact) { @slot; }
}

@variant stuck-top {
  @container scroll-state(stuck: top) { @slot; }
}

@variant scrollable-down {
  @container scroll-state(scrollable: bottom) { @slot; }
}
```

| Candidate | Applicability |
| --- | --- |
| `cq-min-sm:p-3` | Eligible ancestor's inline size is at least `24rem`. |
| `cq-min-[30rem]:p-3` | Eligible ancestor's inline size is at least `30rem`. |
| `card-min-lg:flex` | Eligible ancestor named `card` has inline size at least `40rem`. |
| `card-compact:p-3` | Eligible ancestor named `card` has the queried custom-property value. |
| `stuck-top:shadow-footer` | An eligible scroll-state ancestor is stuck at its top edge. |
| `scrollable-down:opacity-1` | An eligible scroll-state ancestor can scroll toward its bottom edge. |

These follow [CSS container query semantics](https://www.w3.org/TR/css-conditional-5/#container-rule): the browser selects the nearest eligible ancestor for each query, with a name filtering that selection. A query styles descendants rather than changing the queried container's own ordinary style. Size and scroll-state queries require the appropriate `container-type`; custom-property style queries do not require size containment. Sticky positioning, scrolling and custom-property assignment remain explicit CSS. If one element needs both size and scroll-state queries, a library can declare `container-type: inline-size scroll-state`; combining two conflicting type utilities does not merge them.

The first condition grammar admits boolean combinations and nesting of standard size features, custom-property `style()` tests, and `scroll-state()` features `stuck`, `snapped`, `scrollable` and `scrolled`, with the values defined by the [scroll-state feature grammar](https://www.w3.org/TR/css-conditional-5/#scroll-state-container). General style-property queries and unknown query functions are outside this grammar. This syntax support is not a browser compatibility guarantee.

Permit a `param(...)` hole only as a complete size-feature value with the matching numeric type: lengths for dimensions and ratios for `aspect-ratio`. Container names, feature/property names, comparison operators and whole conditions are static. A percent is not a width threshold. Style and scroll-state conditions are literal in this version. For a fixed range, a rule author can write `@container card (24rem <= inline-size < 40rem)`. The feature and axis are authored CSS, not inferred from a class prefix.

Register static owned container names from every utility template before expanding consumer candidates. Querying `card` is valid even if the consumer never requests `card-container`; the query neither instantiates nor retains that utility. The application must establish the ancestor. Named queries into browser-owned CSS require `externalContainers`, which may declare a hook first used downstream. Custom properties in style tests retain their existing property ownership rules. Dynamic names and name holes are errors.

Retain each nested query independently and in written order. Replacing two nested queries with one combined condition can change which ancestor is selected. The same preservation applies when containers are composed with media, supports, selectors or starting-style wrappers. This proposal has one query per `@container` wrapper; comma-separated queries targeting several containers are outside its condition subset.

## Starting-style variants

```css
@variant starting {
  @starting-style { @slot; }
}

@utility transition-opacity { transition-property: opacity; }
```

With the opacity and duration families above, a checked accessor can request:

```java
@UtilityCss("transition-opacity duration-150 opacity-1 starting:opacity-0")
abstract String entering();
```

The starting candidate expands to `@starting-style { .C { opacity: 0; } }`; the ordinary candidates supply the transition and final opacity. Starting style supplies the browser's initial transition values under the [normal starting-style cascade](https://www.w3.org/TR/css-transitions-2/#defining-before-change-style). It does not add transitions, hide the element, animate exit, create pseudo-element content or rewrite `display` automatically.

`@starting-style` takes no condition. Keep its location in the wrapper chain: `card-min-lg:starting:opacity-0` nests starting style inside that named query. Condition and selector validation still applies around its single slot. The compiler emits native CSS; browser transition behavior and support remain browser concerns.

## Checked named anchors

Support both `anchor(left)` / `anchor-size(width)` and explicit references such as `anchor(--trigger left)`. An omitted name uses the browser's default anchor, which can be supplied by HTML association or existing inline `position-anchor` styles. Supporting the name-free expression does not require that the browser created the association implicitly.

Anchors have a distinct proposed symbol kind, identified by provider owner plus `ANCHOR` plus authored name. They are not custom properties despite their `--` prefix. Collect static `anchor-name` declarations from all templates before expansion. A declaration owns its names unless explicitly listed as external; repeated declarations share the same provider-owned identity. Bind and consistently rewrite references in `position-anchor`, named `anchor-scope` lists, `anchor()` and `anchor-size()`, including nested math and fallbacks. Keywords such as `auto`, `none` and `all`, and omitted names, retain CSS meaning.

Propose an `externalAnchors` option on the utility-library annotation for names owned by browser/application CSS. Its entries use full anchor spelling, for example `externalAnchors = {"--host-trigger"}`; `externalProperties = {"host-trigger"}` describes a different namespace. As with other native external hooks, an anchor may first be referenced by a downstream candidate. An external declaration preserves the name; it does not create an anchor or assert that a matching element exists.

Name-bearing positions must contain static literal names. Reject parameter holes and `var()` supplying anchor names, including names introduced through statically visible custom-property carriers. A name hole in `anchor-name` cannot invent an owned symbol at consumer expansion time. Offset expressions and fallback lengths may use supported browser expressions without becoming name substitutions. The first grammar requires a static side/size keyword or literal percentage in anchor-function selector positions; unresolved name/side selection is not guessed from a `var()`.

`anchor()` is admitted in length/percentage inputs only for supported inset properties (`top`, `right`, `bottom`, `left` and their logical/inset forms). `anchor-size()` is admitted in length inputs for sizing, inset and margin properties as defined by [anchor positioning](https://www.w3.org/TR/css-anchor-position-1/#anchor-size-fn). Check these contextual restrictions when substituting into a known declaration, including nested math, and in literal template declarations. Reject an anchor-bearing parameter used in an unrelated declaration or a custom-property assignment that hides its use context. This focused check does not imply complete property-value validation; runtime variable substitution can still invalidate a value.

Valid named references do not retain or instantiate the utility declaring an anchor. Applying trigger and panel classes remains explicit, as does applying an instance scope. Compiler naming separates providers; browser [anchor scope](https://www.w3.org/TR/css-anchor-position-1/#anchor-scope) separates rendered copies of a component. Two copies using the same checked anchor name still require appropriate DOM scope. Neither compile-time binding nor source maps prove that the intended target exists or is usable when rendered.

This extension needs anchor occurrences in shared naming, compression, reservations, liveness reports and source maps, plus a metadata version change. It does not introduce a Java anchor-name accessor or generate per-instance names. Existing application-side names can continue to establish a default anchor outside this language. Custom named `@position-try` definitions have their own namespace and are not included merely because anchors are supported.

## Candidate and definition grammar

The following extension grammar delegates CSS values, selectors, conditions and supporting definitions to parsed CSS components, rather than text captured by a regular expression. The lexical and template constraints below are part of the grammar. Whitespace and CSS comments may separate grammar tokens; whitespace inside a class candidate is not permitted. The actual source starts with the marker; later examples in this document are fragments of that source.

```ebnf
library        = "@utility-rules", ";", { definition } ;
definition     = scale | tokens | utility | variant | support ;
scale          = "@scale", identifier, positive-scalar, ";" ;
tokens         = "@tokens", identifier, value-type,
                 "{", { token-key, ":", css-value, ";" }, "}" ;
utility        = "@utility", head, utility-template ;
variant        = "@variant", head, variant-template ;
head           = literal-name | quoted-literal
               | quoted-pattern, "(", identifier, ":", sources,
                 [ "within", interval ], ")" ;
sources        = source, { "|", source } ;
source         = "tokens(", identifier, ")"
               | "scale(", identifier, ")"
               | "bare(", value-type, ")"
               | "arbitrary(", value-type, ")" ;
interval       = "[", bound, ",", bound, "]" ;
bound          = numeric-literal | "*" ;
candidate-list = candidate, { html-whitespace, candidate } ;
candidate      = { variant-name, ":" }, utility-name ;
variant-name   = literal-name | instantiated-family | bracket-payload ;
utility-name   = literal-name | instantiated-family ;
```

Lexical and structural rules complete the grammar:

- Names are case-sensitive. Library identifiers start with an ASCII letter and then contain letters, digits, `_` or `-`. Token keys may also start with a digit. Literal names and pattern prefixes contain no HTML whitespace, colon, bracket delimiters, quote, backslash, brace or exclamation mark. A bare header name is a CSS identifier. Other literal names, such as `bg-black/20` or `*`, must be quoted in a definition header; `/` has no built-in modifier behavior. Quoting is only source notation and is not part of the candidate.
- A quoted pattern consists of a nonempty literal prefix ending in `-` or `/`, followed by exactly one `{parameter}` at the end. The same parameter is declared once in its header. This prevents a numeric argument from being partly consumed as another family's prefix. Patterns are prefix matchers, not regexes. Utility and variant names have separate namespaces.
- `instantiated-family` is that literal prefix followed by an argument. `tokens(...)` accepts one declared token key, `scale(...)` accepts an unsigned decimal, `bare(...)` accepts the corresponding scalar literal/ratio, and `arbitrary(...)` accepts a bracket payload. A scale multiplier has digits before an optional decimal point and digits after it; signs and exponent notation are not accepted for scales. Leading and trailing zeros are allowed and do not create a different numeric value, but their authored class spelling remains distinct.
- `value-type` is one of the types in the table below. Unitless zero is a length; a nonzero unitless number is not. CSS-wide keywords such as `unset` are not members of these parameter types; a library can expose such a value through a literal utility. Types validate supported static structure and preserve unresolved browser variable substitution. Selector-only `<an+b>` must be statically parsed and accepts no `var()`. No type accepts a declaration list or a complete container condition.
- Utility templates contain declarations and checked nested selector/condition rules. `param(name)` is a typed hole substituted in parsed CSS, never in property names, owned names or string fragments. A selector hole is permitted only in `:nth-child(param(name))` for `<an+b>`. A container-condition hole is permitted only in the typed size-feature value positions described above; media/supports condition holes are outside this grammar. Variant templates contain wrapper rules and exactly one `@slot;` leaf. Undefined parameters and use of `param(...)` outside a template are errors.
- `support` is the existing utility-library subset for property registrations, keyframes, font faces and custom-property initialization. Definitions remain subject to existing ownership and dependency checks. It does not include arbitrary at-rules, ordinary global element defaults or raw class utility rules in a native source.
- HTML whitespace splits an annotation value into class tokens before candidate parsing: space, tab, line feed, carriage return and form feed. Split `:` only outside balanced brackets, strings and parentheses. A colon inside `[&_th:not(:first-child)]` is not a variant separator. Brackets, strings, escapes and parentheses must balance. These are language rules built over [CSS tokenization](https://www.w3.org/TR/css-syntax-3/#tokenization), not a split on every colon.
- Within bracket payloads, every unescaped `_` means a space, including in quoted CSS strings. `\_` means a literal underscore; a literal backslash uses `\\`. Other CSS escapes remain intact for CSS parsing. There are no URL or identifier heuristics. Thus an underscore in a custom-property name must be escaped inside brackets. A Java string literal adds its normal escape layer: the candidate backslash is written `\\_` in Java source. Outside brackets, underscores are literal.
- Empty candidates, empty brackets, duplicate candidates in one list and unbalanced syntax are errors. `!important` and important class modifiers remain unsupported. The compiler does not execute JavaScript, Java callbacks or arbitrary rule-author code.

The type recognizers have a concrete boundary. CSS numeric dimensions and compatible math follow [CSS Values](https://www.w3.org/TR/css-values-4/#numeric-types); this is a fixed supported type set rather than a rule-author-defined type system:

| Type | Accepted static structure |
| --- | --- |
| `<integer>` | Signed decimal integer literals, and numeric math in an integer value context using CSS rounding semantics. A direct fractional literal is rejected. |
| `<number>` | Signed CSS number literals and type-compatible numeric math. |
| `<percentage>` | Percentage literals and compatible percentage math. |
| `<length>` | Unitless zero, CSS length dimensions, compatible math and the context-restricted anchor functions described above. Container-relative length units are lengths too. |
| `<length-percentage>` | Lengths and percentages, including mixed length/percentage math; anchor context restrictions still apply. |
| `<angle>` | Angle dimensions with `deg`, `grad`, `rad` or `turn`, and compatible math. A bare unitless nonzero value is not an angle. |
| `<time>` | Dimensions with `s` or `ms`, and compatible math. Unitless zero is not a CSS time. |
| `<ratio>` | A nonnegative number or two nonnegative numbers separated by `/`. Degenerate zero ratios retain CSS behavior; they are not division performed by the compiler. |
| `<string>` | One quoted CSS string with CSS escapes. It is a complete value, not interpolation into another string. |
| `<color>` | Hex colors, named colors, `transparent`, `currentColor`, and the `rgb`, `rgba`, `hsl`, `hsla`, `hwb`, `lab`, `lch`, `oklab`, `oklch` and `color` functions using their [CSS color grammars](https://www.w3.org/TR/css-color-4/#typedef-color). |
| `<box-shadow>` | `none` or a comma-separated list of [box shadows](https://www.w3.org/TR/css-backgrounds-3/#propdef-box-shadow), with optional `inset`, two to four lengths and an optional color from the preceding type. |
| `<image>` | One URL, linear/radial/conic gradient (including repeating forms), or `image-set()` with checked sources. Gradient arguments use the supported color, position and numeric grammars. Multiple background layers and `none` are separate literal utilities, not one image. See [CSS Images](https://www.w3.org/TR/css-images-4/#image-values). |
| `<position>` | The standard one-to-four-component [position grammar](https://www.w3.org/TR/css-values-4/#position): directional keywords, lengths/percentages and compatible math. |
| `<grid-tracks>` | A row/column track value: `none`, standard track/auto-track lists with line names, `fr`, `minmax()`, `fit-content()` and `repeat()`, or `subgrid` with its line-name list. This language type follows [Grid Level 2](https://www.w3.org/TR/css-grid-2/#track-sizing); it is not the entire grid shorthand or template-area grammar. Line names remain grid-local CSS, not managed symbols. |
| `<an+b>` | A static [An+B expression](https://www.w3.org/TR/css-syntax-3/#anb-microsyntax), including an integer, `odd` or `even`. |

The numeric math function set is `calc()`, `min()`, `max()` and `clamp()`, with checked dimensional arithmetic and compatible nested arguments. Unsupported functions receive a type diagnostic; the compiler does not guess their result type. Literal declarations remain under the existing CSS syntax contract, so a new CSS function can be authored in a fixed utility without becoming an admitted parameter input. Named-anchor binding and prohibited name substitutions still apply to literal declarations.

All declaration-value types permit unresolved `var()` substitution except in the static name/selector positions described above. A variable may supply multiple CSS components, so check the static structure that remains and leave substitution-dependent validity to the browser. Typed strings do not bypass URL or symbol checks when used in another CSS context. URLs from tokens or arbitrary images must satisfy the existing [checked asset contract](../specs/web-assets.md); a local URL cannot bypass declared assets. Properties, IDs, animations, fonts, containers and anchors retain their binding rules after substitution. Implementing these recognizers is additional work beyond the current parser's syntax checks.

Source alternatives must produce a common value type and accept disjoint argument spellings. An integer can join a number source and a length can join a length/percentage source; a color cannot join a spacing scale. Adding a numeric `4` key to `space` would make `tokens(space) | scale(spacing)` ambiguous for `p-4`; the provider fails validation. A numeric `bare(<number>)` source also overlaps a number scale's raw multipliers. At most one bare source and one arbitrary source are permitted in a family. Two arbitrary alternatives are rejected even when their types appear different. Reject overlapping spellings before applying bounds: a range failure must not select another source. The single parameter type must be explicit without guessing from payloads or unresolved `var()` values.

Literal candidates are matched exactly. Otherwise dispatch selects the longest declared family prefix and validates its argument; a failed argument does not fall through to a shorter prefix. Duplicate literal names and duplicate family prefixes fail provider validation. A literal that is also a valid member of a family, or a named token made unreachable by a longer prefix, fails provider validation too. This permits `text-sm` beside a color family whose tokens do not contain `sm`, but rejects a later color token named `sm` in that library.

All definitions are validated when the provider is compiled, including unused definitions. Candidate-dependent CSS is validated again after expansion. A token or parameter cannot inject a second declaration, selector or at-rule through a value position.

## Cascade and identity

Class-list order does not change CSS precedence. No conflict merging is performed. Within one native provider, sort expanded candidates by:

1. Variant count: unqualified utilities first, then one variant, then two, and so on.
2. The variant-rank sequence in written order. Named variant ranks come from declaration order. Arbitrary selector variants share a rank after named variants and use their decoded selector spelling as a secondary key.
3. Utility definition order, with literal and family definitions sharing one declaration sequence.
4. Full authored candidate spelling in Unicode code-point order.

Each expansion's declaration order remains authored order. This is a serialization order, not a specificity reset: browser specificity and conditions still apply. `p-2 p-4` and `p-4 p-2` therefore have identical precedence; the tie within that family is resolved by the final spelling key. Authors should use distinct state getters or local CSS when they intend an override, rather than depending on the order of two conflicting class tokens.

The library should declare broad spacing families before narrower axis/side families when the latter should override the former. Variant declaration order is also part of the library's behavior. The expansion trace reports this ordering; source-file reordering must not be presented as cosmetic.

Preserve the existing outer `shared`, `utilities`, `components` layer order, with a separate identity for each utility provider. Ordinary local component declarations retain precedence over utility declarations. Page resets remain explicit globals; the language does not synthesize a reset to make its utilities work. For example, `border-l` above declares its own border style.

Canonical identity is the Java provider owner plus the complete authored candidate. Consumers requesting the same candidate from that provider receive the same [CSS symbol](../glossary/README.md#css-symbol), independent of which compilation requests it first. Equivalent spellings do not automatically merge. Existing [naming and stability rules](../specs/css-names.md) apply after expansion.

## Worked Rose cases

These are selected current source patterns, not a claim that the grammar can replace all Rose styles. Paths are relative to Rose's `user-experience/src/main/java/iris/rose/client/`.

| Current source and request | Proposed result |
| --- | --- |
| `components/payments/editor/ActivityNotesContainerView.java`: `flex flex-col gap-1 p-3 basis-130` | Same candidate spellings. The spacing definitions yield `.25rem` gap, `.75rem` padding and `32.5rem` basis. Each utility has its own managed class. |
| `components/payments/editor/ActivityConsiderationAttributeTableView.java`: `w-[10%]` | Same spelling; `width: 10%`. |
| The same table: `[&_th]:p-3` | `.C th { padding: .75rem; }`. The class is applied to the table; no class has to be added to each `th`. |
| The same table: `[&_th]:bg-[rgba(var(--cs5),0.05)]` | `.C th { background-color: rgba(var(--cs5),0.05); }`, preserving the external property. `th:bg-table-tint` is an optional library-authored shorthand with the same selector/declaration meaning. |
| The same table: `[&_th]:not-first:border-l` | `.C th:not(:first-child)` receives the two explicit border-left declarations. |
| `components/payments/editor/ActivitySetTableView.java`: `[&_th]:nth-[n+3]:border-l` | `.C th:nth-child(n+3)` receives those declarations. |
| `components/admins/payments/delwp/DelwpBatchTableView.java`: `[&_th:not(:first-child):not(:nth-child(2))]:p-3` | `.C th:not(:first-child):not(:nth-child(2)) { padding: .75rem; }`. |
| The same table: `[&_td_div:not(.iconStack)]:w-full` | `.C td div:not(.iconStack) { width: 100%; }`. `iconStack` must be external; Rose also appends it as a literal class in `components/IconStackView.java`. |
| `components/coordination/event/planning/dialog/TargetEventFieldView.java`: `-ml-2` | Same spelling, with the explicit negative-margin family. |
| `components/payments/editor/ActivityNotePanelView.java`: `bg-black/20` | Recommend a named `bg-overlay` token. The original spelling could instead be declared as a literal utility; the first grammar has no generic slash-opacity modifier. |

Rose's footer shadow can remain verbatim or acquire a named token:

```css
@tokens shadow <box-shadow> {
  footer: 0 -1px 5px 1px rgba(var(--cs1), .2);
}

@utility "shadow-{value}" (value: tokens(shadow) | arbitrary(<box-shadow>)) {
  box-shadow: param(value);
}

@utility sticky { position: sticky; }
@utility bottom-0 { bottom: 0; }
```

The current footer list in `components/form/ListItemView.java` becomes either:

```java
@UtilityCss("sticky bottom-0 shadow-[0_-1px_5px_1px_rgba(var(--cs1),0.2)]")
abstract String footer();
```

or the shorter `sticky bottom-0 shadow-footer`. The compiler does not replace the other raw state hooks used by that view. They remain a separate migration decision.

Rose's `input.css` also defines a reusable `fill-panel` through `@apply`. Express it directly:

```css
@utility fill-panel {
  display: flex;
  flex-grow: 1;
  width: 100%;
  flex-direction: column;
  min-height: 0;
  position: relative;
}
```

This introduces no utility-to-utility macro graph. Existing base rules for `a`, `img` and form elements belong in explicitly selected globals. The class hook `.requestPropertyEditing` could become a literal utility with its own declarations; component-specific selectors and external hooks should be migrated deliberately rather than inferred from source scanning.

Rose's `InlineListPickerView.java` also requests `left-[anchor(left)]` and `min-w-[anchor-size(width)]`. These families preserve those inputs:

```css
@utility "left-{value}" (value: arbitrary(<length-percentage>)) {
  left: param(value);
}

@utility "min-w-{value}" (value: arbitrary(<length>)) {
  min-width: param(value);
}
```

The source's `NativePopoverView.java` already assigns matching `anchorName` and `positionAnchor` inline styles using a per-instance string. The expressions above use that default anchor and can continue to do so. Its server stylesheet already contains `position-area` and flip fallbacks; introducing checked named anchors is not evidence that the reported offscreen or clipped popovers have been fixed.

### A reusable popover recipe

The following definitions illustrate how checked named anchors could replace those two Java style maps when the wrapper contains both trigger and panel:

```css
@utility popover-scope {
  anchor-scope: --popover-trigger;
}

@utility popover-trigger {
  anchor-name: --popover-trigger;
}

@utility popover-panel {
  position: fixed;
  position-anchor: --popover-trigger;
  inset: auto;
  margin: 0;
  padding: 0;
  border: 0;
  position-area: block-end span-inline-end;
  position-try-fallbacks: flip-block, flip-inline, flip-block flip-inline;
  min-inline-size: 0;
  max-inline-size: calc(100dvw - 2rem);
  max-block-size: calc(100dvh - 2rem);
  overflow: auto;
}

@utility popover-match-width {
  inline-size: anchor-size(width);
}
```

```java
@UtilityStyles(RoseUtilities.class)
abstract class PopoverStyles {
    static PopoverStyles get() {
        return Styled_PopoverStyles.get();
    }

    @UtilityCss("popover-scope")
    abstract String style();

    @UtilityCss("popover-trigger")
    abstract String trigger();

    @UtilityCss("popover-panel popover-match-width")
    abstract String panel();
}
```

| Existing DOM role | Checked accessor | Responsibility |
| --- | --- | --- |
| Wrapper containing trigger and panel | `style()` | Limit the anchor lookup to this rendered instance. |
| Trigger | `trigger()` | Declare the checked anchor. |
| Native popover panel | `panel()` | Refer to that anchor, declare placement/fallbacks and limit scrollable content size. |

The provider owns one anchor identity for `--popover-trigger`; the browser scope makes each panel find its own instance's trigger. The panel can enter the native popover top layer while remaining in that DOM subtree. A panel rendered elsewhere through a portal would need a different scope/association design. Keep HTML IDs, `popovertarget`, opening/closing and focus behavior in the component; this recipe does not replace them. It adds no Java coordinate calculation or generated browser runtime.

Placement, fallback and size declarations are ordinary library-authored CSS, not compiler defaults. Built-in flip tactics need no supporting named resource. They work with default anchors too: named ownership centralizes and checks the connection, while [position fallback](https://www.w3.org/TR/css-anchor-position-1/#propdef-position-try-fallbacks) supplies alternate placements. No alternative is guaranteed to fit, and viewport-based maximum sizes do not prove the available space beside a particular anchor is sufficient. Visibility when the anchor leaves view is another explicit CSS choice; the recipe does not silently hide content.

This is a design example, not a validated replacement for Rose's popover component. Before migration, exercise repeated instances, every viewport edge, oversized content, scrolling ancestors, zoom, writing modes and keyboard/focus behavior in supported browsers. Diagnose actual failing popovers with their rendered styles before choosing changes; matching generated CSS alone cannot establish that a clipping or placement failure is fixed.

## Diagnostics and authoring feedback

Errors have stable codes, a primary location and related definition locations. A source literal gets a candidate span when recoverable; a constant expression gets the expression location plus the decoded token and its offset. Rule definitions, token entries and variant wrappers provide related locations. This does not expand the existing deferred promise about exact CSS identifier source maps.

Ownership errors in token values are reported when compiling the provider. An external-property error introduced only by an arbitrary consumer value is reported at that candidate. The cases below describe the relevant isolated input; they do not assume an invalid provider can be used to reach consumer validation.

| Code | Example | Diagnostic content |
| --- | --- | --- |
| `U_UNKNOWN_UTILITY` | `flxe` | Unknown utility; suggest declared `flex`. Suggestions do not change the build result. |
| `U_ARGUMENT` | `p-missing` | Family `p-{size}` accepts `space` tokens, a nonnegative spacing multiplier or a bracketed length/percentage. List available named tokens. |
| `U_VALUE_TYPE` | `bg-[12px]` | Expected `<color>`, found a length. Point to the payload and the family's arbitrary source. |
| `U_VALUE_RANGE` | `duration-[-1ms]` | The resolved `-1ms` is outside `[0ms, *]`. Point to the value, rule and declared interval. |
| `U_RANGE_BOUND` | A time family with `within [0px, 1px]` | Bounds have the wrong dimension. Report at the provider even if no consumer requests the family. |
| `U_VALUE_CONTEXT` | `p-[anchor(left)]` | `anchor()` cannot supply padding; identify the declaration and valid inset contexts. |
| `U_ASSET_REFERENCE` | `bg-image-[url(logo.svg)]` | A local file requires a checked asset reference; type-valid URL syntax does not establish ownership. |
| `U_UNKNOWN_VARIANT` | `smm:p-4` | Unknown variant `smm`; suggest declared `sm` and show its condition. |
| `U_CONTAINER_CONDITION` | `@container card (inline-size param(size))` | Missing a comparison operator; point to the condition and its family definition. |
| `U_CONTAINER_NAME` | Query `missing` with no declaration or external hook | Unknown container name; identify `externalContainers` or the missing setup declaration. Do not create an ancestor utility. |
| `U_STARTING_STYLE` | `@starting-style (opacity: 0)` | Starting style takes no condition; put declarations in the utility, inside the wrapper. |
| `U_ANCHOR_NAME` | `left-[anchor(--missing_left)]` | Unknown anchor `--missing`; show owned names and explain `externalAnchors`. Do not treat it as a custom property. |
| `U_DYNAMIC_NAME` | `anchor-name: var(--target)` or `@container param(name)` | This name-bearing position requires a static name. Point to the declaration/template rather than guessing an identity. |
| `U_SELECTOR_GUARD` | `[th]:p-3` | Selector variant must start with a positive `&`; suggest `[&_th]` for the intended descendant form. |
| `U_EXTERNAL_PROPERTY` | `bg-[rgba(var(--cs5),.05)]` without ownership | Property `--cs5` has no owned definition or permitted external declaration. Identify the provider's `externalProperties` option, whose entry is `cs5`. |
| `U_EXTERNAL_CLASS` | `[&_td_div:not(.iconStack)]:w-full` without ownership | Literal hook `iconStack` must be declared external; do not silently rename or infer it. |
| `U_AMBIGUOUS_RULE` | Numeric `space` token `4` alongside `scale(spacing)` | Both sources accept `p-4`. Point to both definitions and require one meaning. |
| `U_SOURCE_TYPE` | `tokens(color) | scale(spacing)` | A parameter cannot combine color values and lengths. Point to both sources. |
| `U_PARAMETER` | `padding: param(szie)` | Undefined parameter `szie`; the rule declares `size`. |
| `U_SLOT` | Variant with no slot or two slots | Exactly one `@slot` is required, including for an unused variant. |
| `U_CANDIDATE_SYNTAX` | `bg-[rgba(var(--cs5),.05)` | Missing closing bracket; preserve inner function locations. |
| `U_IMPORTANT` | `p-4!` or an important template declaration | Important utility declarations violate local component precedence. |

For example, the human rendering of `U_VALUE_TYPE` should read:

```text
ButtonStyles.java: U_VALUE_TYPE: bg-[12px]
  Expected a <color> argument; 12px is a length.
  Rule: bg-{tone}, defined in rose.utilities.css.
```

The same diagnostic record is available as structured data. An agent should not have to parse human formatting, and structured output must not omit errors that the human renderer shows.

The compiler's authoring interface needs three operations, regardless of their eventual CLI spelling:

1. Validate a rule library without an application, using its provider binding snapshot. Check all definitions, references, typed holes, bounds, slot shape and finite domain collisions. Preparation without bindings reports its pending ownership checks rather than claiming the library is checked.
2. Describe the available utility/variant families, named tokens and accepted argument forms. Describe an infinite numeric scale rather than enumerating it.
3. Expand explicit candidates and return symbolic selectors, conditions, ordered declarations, canonical identities, ordering keys and an origin trace. Include every candidate failure in a batch.

Library verification cases should be data attached to those operations, rather than another rule-language construct. The following example is an expected expansion shape, not a frozen wire format:

```json
{
  "candidate": "[&_th]:nth-[n+3]:border-l",
  "rules": [{
    "selector": "& th:nth-child(n+3)",
    "conditions": [],
    "declarations": [
      ["border-left-width", "1px"],
      ["border-left-style", "solid"]
    ]
  }]
}
```

Positive and negative examples exercise real expansion and diagnostics. Rule authors still need browser checks when correctness depends on layout, theme values or computed-value behavior; a matching output snapshot cannot establish those facts.

An accepted browser-dependent bound appears in the expansion trace rather than as a fabricated success or compilation error. For example, `duration-[var(--motion)]` with `motion` explicitly external records the interval `[0ms, *]`, the unresolved value and `status: browser-dependent`. This is informational trace data; it does not add a warning to every build. Name-dependent substitutions prohibited by the static-name rules remain errors.

## Compilation and delivery boundary

A published native provider carries a validated, versioned rule descriptor and source origins, not an exhaustive generated class inventory. Annotation processing receives evaluated Java annotation strings through javac and expands every effective checked accessor, including unused accessors that still need validation. There is no application-source scanner or generator watcher.

File-backed native preparation therefore needs a different sidecar from the current finite `.classes.json` list. Prepare native grammar at explicit input packaging; bind it to provider ownership/imports during provider compilation; publish the checked descriptor where separately compiled consumers can load it, including header-jar builds. The source marker is inspected by the orchestration layer to choose finite CSS or the language facade. The shared finite input parser must not acquire a reverse dependency on the language module.

Separate consumers can request new candidates from the same published descriptor. An expansion fragment is identified by provider and candidate, carries the descriptor fingerprint and origins, and references the existing checked symbol/dependency model. Linking deduplicates identical fragments and rejects conflicting descriptor fingerprints for the same owner. Runtime theme-variable values do not enter this fingerprint; their declarations and permitted external identities do.

The fingerprint covers the rule definitions, token and scale data, bounds, ownership/import declarations and language/expansion semantics version. Compilations against different definitions of the same Java owner cannot be combined merely because a few requested candidates happen to produce equal CSS. A fingerprint mismatch or unsupported metadata version is a link error with both contributing origins; it is not an invitation to choose whichever descriptor was found first.

Library compilation has no dependency on consuming source files. Consumer compilation depends on the library and emits bindings plus expansion fragments. Linking orders and prunes the fragments using actual surviving getters. In development, retain every fragment requested during compilation; an infinite rule family cannot imply generation of every possible candidate. Finite CSS inputs retain their existing development-retention behavior.

The native class inventory at a complete link is the finite union of requested expansions. Templates introduce their own managed class through `&` and do not invent additional managed marker classes. Pruning can therefore analyze the selectors that were actually generated without enumerating the infinite candidate language. Owned name registries come from the full provider descriptor: the existence of an anchor/container identity is independent of which setup utilities were instantiated. Queries and anchor references do not create DOM elements or retain their setup classes. Supporting properties, animations, fonts and assets retain their existing dependency-driven liveness.

This needs a real extension to the current resource/catalogue format, which models one complete stylesheet per owner. Downstream candidate fragments cannot be bolted onto the provider's existing inventory without addressing collection, deduplication, supporting definitions and source maps. Shared anchor symbol occurrences must also participate in naming/compression, raw-name reservations, source maps and liveness reports. Version and rebuild affected compiled artifacts when those schemas change; this design promises no old-metadata compatibility shim. Source-level name-free anchor expressions keep their meaning. The grammar does not require a new scanner or a circular build to solve that problem.

Definition bodies keep their authored origins in CSS maps; substituted token values and candidate requests also appear in expansion traces. Preserve the existing [source-map guarantees](../specs/css-source-maps.md); exact multi-origin mapping for one generated CSS token remains a separate implementation question.

## Scope and feasibility checks

The proposed grammar covers the typed scalar/compound values, spacing, colors, shadows, structural selectors, container queries, starting styles and named anchors demonstrated above. It excludes general regex matching, multiple captures, utility-to-utility `@apply`, generic slash modifiers, arbitrary properties, arbitrary at-rule variants, executable plugins and generic group/peer marker synthesis. A ratio argument such as `aspect-16/9` is one typed value, not a generic modifier mechanism. Custom named `@position-try` definitions, dynamic anchor/container names and new Java name-accessor APIs are outside this expansion.

Before implementation, verify parsing typed template holes and flattening the selected selector subset without losing specificity or source origins; publishing/merging same-provider fragments across independent Java/J2CL compilations; and extracting enough annotation-free binding behavior to support the package direction without a dependency cycle. Exercise numeric domain collisions, mixed units and browser-dependent bounds through the facade. The current parser's syntax validation is not evidence that these recognizers or interfaces already exist.

Container and anchor integration needs name-rewrite/source-map cases inside nested wrappers, pruning cases where a valid name's setup class is absent, and repeated-instance browser checks for scope and placement. Keep nested queries intact and verify starting-style transition behavior in browsers. The popover example supplies a concrete validation target, not a proven fix for Rose.

The source marker, one-parameter matching, guarded arbitrary selectors and variant-count-first ordering remain the agreed design direction. The separate language package concentrates those semantics while shared CSS contracts continue to own binding and delivery. No part of this proposal requires full Tailwind compatibility, a non-JVM tool or a browser-side language interpreter.
