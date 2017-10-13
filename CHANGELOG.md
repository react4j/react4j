# Change Log

## Unreleased

##### Fixed
* Add the missing inherit `com.google.gwt.user.UI` to `react4j.widget.ReactWidget.gwt.xml`.

##### Changed
* 💥 Remove `Component.forceUpdate()` and replace it with the api `Component.scheduleRender(boolean force)`
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
