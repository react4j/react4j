# Native utility example

From the repository root:

```sh
tools/examples.sh serve native-utilities development
```

Open [127.0.0.1:3000](http://127.0.0.1:3000). Resize the container, change the saved
state, and open both Details popovers. Use `production` instead of `development`
for optimized JavaScript and CSS with compressed names.

[Workspace.java](src/main/java/react4j/nativeutilities/Workspace.java) selects
utilities through ordinary Java accessors. The explicit
[utilities.css](src/main/java/react4j/nativeutilities/utilities.css) input declares
scales, colors, families, a container variant, anchors and starting styles.
[Utilities.java](src/main/java/react4j/nativeutilities/Utilities.java) connects the
two. The compiler runs on the JVM; the browser receives ordinary JavaScript/CSS.

The native popover buttons handle opening, closing, Escape and light dismissal.
Each card contains both its trigger and panel in one anchor scope. Placement,
flip fallbacks, size limits and the opacity transition are explicit library CSS.
Reduced-motion preferences remove that transition. Native browser support is
required; the example is checked in the repository's pinned Chromium.

Start with [using native utilities](../../docs/native-utilities.md), then follow
the [rule-author reference](../../docs/utility-rules.md) and
[portable recipes](../../docs/utility-recipes.md).
