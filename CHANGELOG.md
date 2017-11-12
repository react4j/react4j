# Change Log

### Unreleased

### [v0.13](https://github.com/realityforge/react4j/tree/v0.13) (2017-11-13)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.12...v0.13)

##### Added
* **\[dom\]** Add `ReactDOM.createPortal()` method to support React 16 portals.
* **\[extras\]** Add an "extras" module that will contain non-core utilities and components that are useful across
  a broad range of applications. Over time as React4J stabilizes it is expected that they will migrate out to be
  managed externally. Until then it is a useful place for the components and utilities to live while they co-evolve.
* **\[extras\]** Add the `WindowPortal` component that creates a portal to a new top level window.
* **\[core\]** Add initial support for Reacts concept of "Context". React4j supports writing context consumer
  components as well as context provider components.

##### Fixed
* 💥 **\[core\]** Make sure the type of `ReactElementChildren` does not imply a prototype.
* 💥 **\[dom\]** Make sure the type of `DOMElement` does not imply a prototype.
* 💥 **\[core\]** Add support for representing numbers as instances of `ReactNode`.

##### Changed
* 💥 **\[core\]** Make `ReactElement` implement the `ReactNode` interface to simplify code base.
* 💥 **\[dom\]** Change the DOM factory to accept and return instances of `ReactNode` rather than `ReactElement`.
* 💥 **\[dom\]** Change children parameter of `ReactDOM.createElement()` to be a `ReactNode`.
* 💥 **\[dom\]** Change element parameter of `ReactDOM.render()` to be a `ReactNode` so can render arbitrary nodes
  such as strings, portals etc.
* 💥 **\[dom\]** Explicitly type the containers in `ReactDOM` as Elemental2 `Element` instances.
* 💥 **\[dom\]** Add nullability annotations in `ReactDOM` to document expected behaviour.
* 💥 **\[dom\]** Rewrote `ReactDOM.render(ReactNode,Element)` as an overlay method.
* 💥 **\[dom\]** Rewrote `ReactDOM` and javadocs to align with the official React documentation except where it
  is less precise where actual behaviour was documented.
* 💥 **\[core\]** Upgrade to react 16.1.0.
* **\[dom\]** Move the react-specific `@JsOverlay` methods into `HtmlGlobalFields` parent class rather than
  duplicating them in every subclass.
* **\[dom\]** Move the global html props `@JsOverlay` methods into `HtmlGlobalFields` parent class rather than
  duplicating them in every subclass. This includes the methods: `accessKey`, `className`, `contentEditable`,
  `contextMenu`, `dir`, `draggable`, `hidden`, `id`, `lang`, `style`, `spellcheck`, `tabIndex`, `title` and
  `translate`.
* **\[dom\]** Add nullability annotations for event handlers in html props classes.
* 💥 **\[dom\]** Change the non-fluent setters in `HtmlGlobalFields` to be protected access. The goal is to encourage
  the creation of fluent setters for all setters that are actually used.
* 💥 **\[dom\]** Change the event handlers on `LabelProps` to use camel case rather than pascal case. i.e. Rename the
  fluent event handlers from `OnX` to `onX`
* 💥 **\[core\]** Move `react.dom.RefConsumer` to `react.core.RefConsumer`.
* 💥 **\[core\]** Remove support for string refs as they are deprecated in React js and slated for removal.
* 💥 **\[core\]** Remove `ReactRefCallback` and replace it with `RefConsumer`.
* 💥 **\[core\]** Remove `ReactElementChildren` class and replace usages with `ReactNode` class as they are equivalent.
* 💥 **\[core\]** Add support for the `componentDidCatch` lifecycle method introduced in React 16.
* 💥 **\[core\]** Removed `Component.renderAsElement()` as `ReactElement` now implements `ReactNode` and thus
  `Component.render()` is a better alternative.
* 💥 **\[core\]** Removed `Component.renderAsString()` as it is trivial to wrap a string in a `ReactNode`.
* 💥 **\[processor\]** Update the annotation processor so that the enhanced component classes generate the `_create`
  methods with a return type of `ReactNode`.
* 💥 **\[core\]** Add nullability annotations onto `ReactElement`.
* 💥 **\[core\]** Reduce the number of type parameters required to implement `ComponentConstructorFunction`. As a
  result make the `NativeComponent` class public.

### [v0.12](https://github.com/realityforge/react4j/tree/v0.12) (2017-11-09)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.11...v0.12)

##### Changed
* Upgrade to Arez 0.25.

### [v0.11](https://github.com/realityforge/react4j/tree/v0.11) (2017-11-03)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.10...v0.11)

##### Fixed
* Fixed bug in `DragEvent` where field name was `clipboardData` rather than `dataTransfer`.

##### Changed
* Upgrade to Arez 0.24.

### [v0.10](https://github.com/realityforge/react4j/tree/v0.10) (2017-11-01)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.09...v0.10)

##### Changed
* 💥 Upgrade to Arez 0.23.

### [v0.09](https://github.com/realityforge/react4j/tree/v0.09) (2017-10-30)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.08...v0.09)

##### Fixed
* Fixed bug introduced when `ReactDOM.createElement(String,Props)` variant was converted from native method
  to an overlay method.

### [v0.08](https://github.com/realityforge/react4j/tree/v0.08) (2017-10-29)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.07...v0.08)

##### Changed
* Upgrade to Arez 0.22.
* Use Arez's `@ContextRef` annotation to get access to `ArezContext` rather than relying on custom code
  in `ReactArezComponent`.
* Use Arez's `@ObserverRef` annotation to get access to the `Observer` for render rather than relying on
  custom code in `ReactArezComponent`.
* 💥 Changed the access modifier on the `TYPE` field in the enhanced component class from public to private.
  Creation of `ReactElement` instances is now done by invoking the factory methods named `_create` that
  have been added to the enhanced component class.

### [v0.07](https://github.com/realityforge/react4j/tree/v0.07) (2017-10-27)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.06...v0.07)

##### Fixed
* Ensure that `ReactArezComponent` runs all actions using the context in which the component was created. This avoids
  assertion failures if Arez is used in multi-Xone configuration error and the creation and rendering of components
  occurs in different zones.
* The `ReactArezComponent.componentWillUnmount` would dispose the manually created reactive elements one at a
  time starting with props, state and finally the render tracker. However this caused observers to react to each
  individual element being disposed which could cause assertion failures if an observer was scheduled that could
  use one of these individually. Instead they were wrapped in an action that disposes all of the reactive elements
  in one go eliminating many of these problems. (i.e. when a `@Computed` method uses a `prop()` and is used
  inside the `render()` function).
* Invoke `Disposable.dispose( this )` in `ReactArezComponent.componentWillUnmount()` so that all the other reactive
  elements on the component are disposed at the same time. This is most useful to stop `@Autorun` and `@Computed`
  methods on the component from running after the component is unmounted.

##### Added
* **\[arez\]** Add `ReactArezComponent.hasRenderDepsChanged()` that exposes flag indicating whether Arez has
  determined that the component needs to be re-rendered.
* **\[arez\]** Update the `react4j.arez.ReactArezDev` GWT module to inherit from the `org.realityforge.arez.ArezDev`
  GWT module. This ensures that assertions, and invariant checks are enabled in development mode.

##### Changed
* Upgrade to BrainCheck 1.3.0 so assertion failures open the debugger.
* Upgrade to Arez 0.19.
* 💥 Update the `ReactArezComponent` implementation so that `props` and `state` are made not observable. This
  dramatically simplified the code and conceptual model for `ReactArezComponent` components. This has meant that
  components can no longer define `@Autorun` or `@Track` methods that will react to changes in `props` or `state`
  but this was considered a "bad" practice and discouraged anyway.
* 💥 Update the `Component` implementation to make the `props()`, `state()` and `scheduleStateUpdate(...)` methods
  final.

### [v0.06](https://github.com/realityforge/react4j/tree/v0.06) (2017-10-23)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.05...v0.06)

##### Fixed
* Rewrite the `react4j.core.React` class, moving some code to java where possible and eliminating as yet unused
  code. Some code (i.e. code to interact with children) has yet to be imported. The children parameter has been
  replaced by RenderResult.

##### Added
* Added `i` method to DOM factory.

##### Changed
* Move to GWT 2.8.2.
* Upgrade to Arez 0.17.
* 💥 Rename `RenderResult` to `ReactNode`.
* Annotated the event handlers in `react4j.dom.events` package with `@FocusEventHandler`.
* 💥 Convert public fields of event in `react4j.dom.events` package to private and add overlay methods to access
  the fields.

### [v0.05](https://github.com/realityforge/react4j/tree/v0.05) (2017-10-19)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.04...v0.05)

##### Added
* Added `RenderResult` union type that abstracts over the results of rendering from a component. It is designed
  to support the types supported by React16, namely `ReactElement`, `String` and arrays of values to render.

##### Changed
* **\[core\]** Change the return type of `Component.render()` method to `RenderResult` and add a default
  implementation that returns `null`. Also provide several other render method variants as part of the base
  component implementation `Component.renderAsString()`, `Component.renderAsElement()`, `Component.renderAsJsArray()`
  and `Component.renderAsArray()`. The developer is expected to override a single render method variant and
  the annotation processor will ensure the enhanced class uses that render method variant to render the component.

### [v0.04](https://github.com/realityforge/react4j/tree/v0.04) (2017-10-16)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.03...v0.04)

##### Changed
* **\[core\]** 💥 Replace `Component.setState(...)` with `Component.scheduleStateUpdate(...)` so that it
  accurately reflects intent.
* Link Javadocs with Arez, GWT and JRE javadocs so as to improve the experience of looking up docs.
* Complete the automation of releasing to Maven central.

### [v0.03](https://github.com/realityforge/react4j/tree/v0.03) (2017-10-16)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.02...v0.03)

##### Added
* Cached javascript functions created for methods annotated with `@EventHandler`. This allows the equivalent
  of `PureComponent` to be used in React4j to avoid expensive re-renders when only event handlers have updated.

##### Fixed
* Add the missing inherit `com.google.gwt.user.UI` to `react4j.widget.ReactWidget.gwt.xml`.
* **\[processor\]** Add an explicit check to verify that `@EventHandler` annotated methods do not have the same
  logical name nor do they overload each other (i.e. have the same method name but with different parameters).
  Either scenario will result in compilation errors and these checks just make the failure reason explicit.
* **\[arez\]** Make sure `ReactArezComponent` implements `componentWillUnmount` lifecycle method and cleans up
  the arez resources.

##### Changed
* 💥 Change all the compile time constant property keys from being prefixed by `react.` to being prefixed
  by `react4j.` to align with project name change. This should impact downstream users if the builtin modules
  are inherited from and no custom properties were set.
* 💥 **\[arez\]** Classes extending `ReactArezComponent` must NOT be annotated with `@ArezComponent` as the
  generated subclass will be annotated instead. Any `@EventHandler` annotated method have the `@Action` annotation
  added by default and it is an error for the developer to explicitly annotate an `@EventHandler` with an `@Action`
  annotation. This behaviour can be aborted by annotating the method with `@NoAutoAction` in which case the
  developer is responsible for adding the `@Action` annotation if needed.
* **\[processor\]** Change the output of processor so that the method that previously provided the constructor
  function and the event handlers is now a subclass of the component. Also move the lifecycle class and native
  component adapter as static subclasses of this single enhanced component class. This means that React4j generates
  a single java file for each component. The intent is to make it easier to understand what is happening underneath
  the covers.
* **\[core\]** 💥 Remove `Component.forceUpdate()` and replace it with the api `Component.scheduleRender(boolean force)`
  where `force=true` calls the underlying `NativeComponent.forceUpdate()` and `force=false` is equivalent
  to `setState({})`.
* 💥 Changed the maven group coordinate from `org.realityforge.react` to `org.realityforge.react4j`
  so as to reflect project name change.
* Began to automate the release and publishing of both the website to GitHub and the release artifacts
  to Maven Central.
* Replace usages of `org.jetbrains:annotations:jar` dependency with `org.realityforge.anodoc:anodoc:jar`
  as that is the same dependency used in `arez`, `braincheck` and other upstream dependencies.

### [v0.02](https://github.com/realityforge/react4j/tree/v0.02) (2017-10-09)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.01...v0.02)

##### Fixed
* Upgraded `braincheck` dependency to ensure that invariant checks are correctly compiled out during
  production builds.
* Upgraded `arez` dependency to ensure that compile time constants are correctly resolved at compile time.
* Restructure the `ReactConfig` class so the compile time constants are correctly resolved at compile time.
* Removed the requirement to supply `-generateJsInteropExports` argument when compiling the GWT code by
  defining a `@JsType(isNative = true)` interface to define the lifecycle methods and manually patching
  the constructor function to specify the equivalent of ES6 static properties (i.e. `displayName` atm).
* Made sure that `ReactElement` does not expect a native type named `ReactElement` and is an `Object`.

##### Added
* ✨ Added [ArezSpyUtil](http://realityforge.org/react4j/api/react4j/arez/spy/ArezSpyUtil.html) to simplify the
  setup of Arez spy console logger during development and debugging.
* ✨ Added [ReactWidget](http://realityforge.org/react4j/api/react4j/widget/ReactWidget.html) that simplifies
  integration with applications still using GWT Widgets.

##### Changed
* 💥 Updated `ArezComponent` so subclasses implement the `render()` method rather than `doRender()` to make it
  consistent with the way components that do not extend `ArezComponent` are written.
* 💥 Removed `NativeComponent` from the public API supported by the library by making it package access. It is
  an implementation detail that should not be relevant to downstream consumers.
* 💥 Renamed `componentInitialize()` to `componentDidConstruct()` to follow the naming conventions baked into
  the react component model.
* 💥 Stopped shipping artifacts with a classifier of `gwt` as there is no consumer of the library that is not
  gwt based. This essentially entails shipping the `.gwt.xml` and source files inside the main jar artifact.

### [v0.01](https://github.com/realityforge/react4j/tree/v0.01) (2017-10-06)
[Full Changelog](https://github.com/realityforge/react4j/compare/934ed5a707bfdab7959e9af5a793575a42a780ff...v0.01)

 ‎🎉	Initial super-alpha release ‎🎉.
