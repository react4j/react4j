# Java and runtime

## Source conventions

- Use Java 17, two-space indentation, and opening braces on a new line for types and methods. Compilation treats warnings as errors with `-Xlint:all,-processing,-serial`.
- Prefer `javax.annotation.Nonnull` and `javax.annotation.Nullable`.
- Mark JVM-only helpers with [`@GwtIncompatible`](../../core/src/main/java/react4j/GwtIncompatible.java).
- Document public APIs with Javadoc; put package documentation in `package-info.java`.

## Runtime and browser compilation

Core APIs live under `react4j`, with internal helpers in `react4j.internal`. DOM bindings are under `react4j.dom`, with event wrappers in `react4j.dom.events`.

When changing runtime flags or their defaults, inspect these sources together:

- [`ReactConfig.java`](../../core/src/main/java/react4j/ReactConfig.java): configuration consumers and JVM/system-property providers.
- [`React.gwt.xml`](../../core/src/main/java/react4j/React.gwt.xml): GWT properties and production defaults.
- [`ReactDev.gwt.xml`](../../core/src/main/java/react4j/ReactDev.gwt.xml): development defaults and assets.
- [`react4j.js`](../../core/src/main/java/react4j/react4j.js): Closure/J2CL defines registered as system properties.

Core output trimming uses Grim `@OmitPattern`/`@KeepPattern` declarations in [`package-info.java`](../../core/src/main/java/react4j/package-info.java). DOM GWT modules are [`Dom.gwt.xml`](../../dom/src/main/java/react4j/dom/Dom.gwt.xml) and [`DomDev.gwt.xml`](../../dom/src/main/java/react4j/dom/DomDev.gwt.xml).
