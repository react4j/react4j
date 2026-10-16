# CSS-in-Java proof

This directory proves static CSS extraction from Java declaration-list annotations with checked abstract style accessors. `org.realityforge.css` is a provisional namespace; directory layout, final product name, repository and publication artifacts remain open. The annotation and processor targets are separate here so their dependency costs can be inspected independently.

WorldBuilder's [authoring architecture](https://github.com/realityforge/WorldBuilder/blob/ad52a87/docs/adr/0020-extract-static-client-styles.md), [separate-product boundary](https://github.com/realityforge/WorldBuilder/blob/ad52a87/docs/adr/0021-consume-a-separate-style-generator.md) and [selected tools](https://github.com/realityforge/WorldBuilder/blob/ad52a87/spikes/j2cl-client/tools/README.md#selected-css-pipeline) are the consumer context. This proof does not migrate WorldBuilder or publish a release.

## Authoring and generated API

See [SaveButton.java](example/src/main/java/org/realityforge/css/example/SaveButton.java) and [BrowserExample.java](example/src/main/java/org/realityforge/css/example/BrowserExample.java) for the complete checked example. A style definition is an accessible abstract class with an accessible no-argument constructor, `abstract String style()`, and zero-argument abstract `String` modifier accessors. A literal `@Css` text block supplies root declarations and nested same-element rules such as `&:focus-visible` and `&.success`. Java interpolation, constant concatenation and runtime stylesheet generation are unsupported. Static nested definition classes are supported; inheritance and generic style definitions are outside this proof.

The processor generates a package-access `Styled_SaveButton` subclass. `style()` returns the root class token; `success()` returns a space-separated root and namespaced success token. The factory remains ordinary author-written Java. The modifier methods select static classes and allocate no CSS. Applying conflicting modifiers follows authored CSS order, independently of class-token order. Fragment independence assumes that an element uses one component owner; the String API does not prevent callers from combining roots from different owners.

Names derive from the Java binary owner name, including package and enclosing classes. ASCII letters and digits remain readable, package dots become single hyphens, and all other UTF-16 code units become `_hhhh_`. The root has a `jcss-` prefix and modifier names follow `--`. Because Java owner names contain no hyphens and literal underscores are escaped, separator and escape sequences cannot be confused. Generated subclass names use the encoded local binary name. Names do not depend on content, compilation order or checkout paths. Name shortening is outside this proof.

The parsed selector tree must agree with the declared accessors in both directions. Comments, property values and quoted strings do not declare modifiers. State pseudo-classes need no accessor. Functional pseudos are limited to `:is`, `:not` and `:where` in this proof. Component selectors must begin with `&` and stay on the owned element; descendant/sibling reach, global escapes, `:has`, component keyframes and component global at-rules are diagnosed. Conditional `@media`, `@supports` and `@container` rules are supported. These are explicit proof limits, not a complete CSS ownership language.

## Build and tools

```sh
bazel build //css/example:styles //css/example:bindings
bazel test //css:tool_tests //css:processor_tests //css/example:browser_tests --test_output=errors
bazel test -c opt //css/example:browser_tests --test_output=errors
bazel run //css:tool -- targets
css/check_reproducibility.sh
```

`styles` runs the annotation processor through a declared Java compiler action before ordinary checked Java/J2CL compilation. It emits a deterministic generated source archive, external minified CSS and Brotli output. CSS resources are emitted through Java's Filer, then collected without relying on J2CL to retain auxiliary resources. The proof adapter accepts self-contained style definitions; application classpath wiring and installation as a conventional compiler plugin are not delivered.

The annotation processor reads javac's cooked text-block value and parses the original token to retain per-character Java offsets. The mapper handles incidental indentation, trailing whitespace, CRLF, Java escapes, octal escapes, Unicode escapes and continued lines, and checks its result against javac before reporting CSS locations. Diagnostics include the original Java path, line and column; javac's attached caret covers the annotation value. This bridge uses javac's public tree API and has not been verified under Turbine.

The processor invokes the declared CSS bridge before emitting Java. PostCSS parses the stylesheet and `postcss-selector-parser` parses selectors; Prettier formats CSS; Stylelint's recommended semantic rules lint both shared and embedded CSS; Lightning CSS transforms nesting, targets the pinned browser list, and bundles and minifies the declared fragments through its resolver API. Shared styles precede component rules through explicit `shared, components` cascade layers. `!important` is rejected in both inputs because it reverses layer precedence. Shared imports and explicit author layers are outside the proof.

`pnpm-lock.yaml` pins the complete Node tool graph and native Lightning CSS packages by integrity. Node 24.9.0 and rules_js are declared in the root Bazel module as development dependencies, outside the configuration exported to ordinary React4j consumers. Normal compilation actions require no network access or globally installed Node packages. The browser receives Java-generated class strings and static CSS; it receives none of these tools. The processor adds no JVM CSS parser dependency. JSpecify supplies Java nullness annotations.

Browser targets resolve from pinned Browserslist and caniuse-lite data using the consumer's last-two-stable-major policy for Chrome, Edge, Firefox, Safari and iOS Safari. The `targets` command prints the resolved versions. That consumer policy is not automatically the separate product's support contract. The browser test uses the pinned Chromium runtime with `--headless=new` to exercise actual focus state; it does not establish Firefox, WebKit or full accessibility conformance.

## Formatting

Build `//css:format` and `//css:tool`, then invoke the Java formatter with the bridge and source paths:

```sh
bazel build //css:format //css:tool
BAZEL_BINDIR=bazel-bin bazel-bin/css/format \
  "$PWD/bazel-bin/css/tool_/tool" check "$PWD/css/example/src/main/java/org/realityforge/css/example/SaveButton.java"
```

Use `write` instead of `check` to rewrite CSS text blocks. For standalone CSS, use `bazel run //css:tool -- check "$PWD/css/example/shared.css"` or replace `check` with `format`. Formatting parses Java, calls Prettier on the CSS block and re-encodes Java escapes; it leaves surrounding Java unchanged. Run the normal Java formatter afterward when changing Java layout. The tests check CSS equivalence and formatting idempotence. Source writeback is a separate operation from annotation processing.

To update Node dependencies, edit exact versions in `package.json` and run `npx --yes pnpm@10.22.0 install --lockfile-only --ignore-scripts` in this directory, then review the lock diff. Package lifecycle hooks are disabled. Bazel consumes the lock without invoking npm installation in build actions.

## Evidence and remaining product work

The maintained tests exercise generated methods, complete CSS resources, independent and reversed compilation order, clean compiler outputs, nested owners, encoding collisions, negative CSS/Java contracts and source diagnostics. The browser case exercises real React4j props, custom-property assignment, shared/component precedence, focus and disabled states, conflicting modifiers, nested instances and independent fragment reordering while preserving order inside each component.

The proof leaves final artifact layout, publication, editor integration, cross-file style composition, richer owned subelements, keyframe bindings and broader browser verification open. One Node bridge serves all style definitions in a compilation and is closed in the final processing round. The proof groups Java and shared CSS inputs in one extraction action: shared edits rerun that action with unchanged Java bindings, while embedded edits also invalidate checked Java, J2CL and the development bundle. Scaling the development loop across thousands of definitions and many compilation targets still requires measurement before product adoption.
