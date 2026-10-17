# Java and runtime

Use Java 17 language/output for owned APIs and generated code. The formatter and strict compiler policy are canonical in the build graph; [CONTRIBUTING.md](../../CONTRIBUTING.md) owns their commands.

Owned contracts use JSpecify. Place `@NullMarked` on packages/types; preserve `@NullUnmarked` declaration scopes and explicit qualifiers when generating APIs. Type variables are conditionally nullable unless their bounds establish a stronger contract. Qualifiers on array elements and array references are separate. Legacy annotations are deliberately ignored by processor inference.

Keep dynamic validation of React props/context and conversions from nullable values. Static non-null parameters and initialized internal state need no repeated `Objects.requireNonNull`. Native JavaScript fields may need narrow initialization suppressions because Java constructors do not populate them.

Core APIs live under `react4j`, internal helpers under `react4j.internal`, and DOM bindings under `react4j.dom`. When changing runtime switches, inspect these sources together:

- `core/src/main/java/react4j/ReactConfig.java`: JVM overrides and browser providers.
- `core/src/main/java/react4j/react4j.js` and `ReactConfig.native.js`: registered Closure definitions and retention.
- `tools/browser/rules.bzl`: explicit development/production values and runtime defines for BUNDLE.
- `doc-examples/src/test/js/react4j/examples/browser_test.js`: live configuration and rendering contracts.

The package-private `GwtIncompatible` annotation is a J2CL pruning marker recognized by simple name. It removes JVM-only System.getProperty overrides from browser sources. Keep it independent of any compiler dependency. The browser runtime retains React/ReactDOM 16.6.0 and the React.Element symbol patch; changing these needs asset and both-mode browser validation.

CSS authoring annotations live under `react4j.css`, with build-only implementation in `react4j.css.processor`. For dynamic browser props, use `CssProps.customProperty("--name", value)` and `HtmlGlobalFields.data("suffix", value)` with String values (nullable for removal). Keep JavaScript conversion details inside the DOM API.
