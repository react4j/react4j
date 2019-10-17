# Change Log

### Unreleased

* Transform several java enum classes within the `react4j-dom` module to classes containing string constants. GWT and J2CL will no optimize out unused constants or the `values()` method of the enum class which can result in some increased code-size while using string constants means applications pay for the values that they use. To keep the developer experience, the methods have been annotated with the `@MagicConstant` annotation so that IntelliJ will highlight incorrect values and provide an almost equivalent developer experience when using the associated classes.
* Add some additional nullability annotations to the props objects defined in the `react4j-dom` module.

### [v0.134](https://github.com/react4j/react4j/tree/v0.134) (2019-10-17) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.133...v0.134) · [API Differences](https://react4j.github.io/api-diff?key=react4j&old=0.133&new=0.134)

The release includes 1 breaking API change.

Changes in this release:

* Upgrade the `org.realityforge.grim` artifacts to version `0.03`.
* Upgrade the `org.realityforge.org.jetbrains.annotations` artifact to version `1.1.0`.
* Upgrade the `org.realityforge.gwt.symbolmap` artifact to version `0.09`.
* Remove the method `ReactElement.createRawElement(...)` from the public interface by making the method private. The method was never used outside the class.
* Upgrade the `org.realityforge.arez` artifacts to version `0.150`.
* Use the `org.realityforge.grim` library to perform validation that output emitted by the GWT compiler has stripped the symbols that are not expected to be present. This involved annotating the symbols in the `core` and `dom` modules that should be omitted in different contexts and removing the `gwt-output-qa` module. The `core` and `dom` modules now ship with the required grim rules/metadata as part of the jar. See the `META-INF/grim/*` files included in the `react4j-core` and `react4j-dom` archives for more details.

### [v0.133](https://github.com/react4j/react4j/tree/v0.133) (2019-10-04) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.132...v0.133) · [API Differences](https://react4j.github.io/api-diff?key=react4j&old=0.132&new=0.133)

The release includes 1 non breaking API change.

Changes in this release:

* Upgrade the `org.realityforge.braincheck` artifact to version `1.25.0`.
* Upgrade the `org.realityforge.javax.annotation` artifact to version `1.0.1`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `2.25`.
* Upgrade the `org.realityforge.guiceyloops` artifact to version `0.102`.
* Upgrade the `org.realityforge.arez` artifacts to version `0.146`.
* Add the `@react4j.annotations.SuppressReact4jWarnings` annotation as an alternative to `@java.lang.SuppressWarnings` if the suppression occurs on a base class that is present in library and not necessarily compiled in the same compiler instances as the React4j component.
* Generate an invariant failure if a non-immutable `@Prop` annotated method is invoked from inside  a `@PostConstruct` annotated method and there is not a `@OnPropChange` annotated method that covers the `@Prop`. This is considered an error as the prop iss used in the `@PostConstruct` method presumably to drive the component behaviour but when the prop changes no changes are made to the component. This invariant can be suppressed by adding a `@SuppressWarnings( "React4j:MutablePropAccessedInPostConstruct" )` annotation to either the `@Prop` annotated method or the containing class. This check is designed in such a way that it is optimized out at compile time.
* Archive the `react4j-windowportal` library as there is no desire or need to continue supporting it. The library was never completed to a high enough standard to make it worthwhile to continue supporting the library.
* Start to record a report describing API differences between releases so that they are easier to minimize and track over time. The API differences will be added to the CHANGELOG, the Github Release description and website release notice.
* The behaviour of the `@Prop.disposable` parameter when set to `ENABLE` will now default to `ENABLE` if the type of the prop is annotated with the new Arez annotation `@ActAsComponent`.
* The `@Prop.immutable` parameter can now be set to true if the type of the prop is annotated with the new Arez annotation `@ActAsComponent`.

### [v0.132](https://github.com/react4j/react4j/tree/v0.132) (2019-07-15) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.131...v0.132)

* Upgrade the `au.com.stocksoftware.idea.codestyle` artifact to version `1.14`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.20.0`.
* Explicitly declare `gwt-user` dependency in core module rather than relying on automatic inclusion due to the presence of the gwt addon. Exclude to `gwt-user` dependency from the generated pom file so as not to include it in j2cl projects.
* Upgrade the `org.realityforge.arez` artifacts to version `0.142`.

### [v0.131](https://github.com/react4j/react4j/tree/v0.131) (2019-07-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.130...v0.131)

* Upgrade the `org.realityforge.arez` artifacts to version `0.141`.
* Refactor the mechanisms via which `@Memoize` annotated methods have their priority modified to use the new `@PriorityOverride` annotation from Arez. React4j will generated a `@PriorityOverride` annotated method unless the user explicitly specifies a priority for an `@Memoize` annotated method or there is an existing `@PriorityOverride` annotated method associated with the `@Memoize` annotated method. This change is intended to reduce the coupling between React4j and Arez as prior to this change, any changes in the way `@Memoize` was implemented by Arez could result in a corresponding set of changes in React4j.

### [v0.130](https://github.com/react4j/react4j/tree/v0.130) (2019-07-01) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.129...v0.130)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.19.0`.
* Upgrade the `org.realityforge.gir` artifact to version `0.10`.
* Upgrade the `org.realityforge.arez` artifacts to version `0.140`.

### [v0.129](https://github.com/react4j/react4j/tree/v0.129) (2019-06-03) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.128...v0.129)

* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `2.24`.
* Remove `React.ConcurrentMode` as `React` has removed the symbol in a later version. See
  [facebook/react#15532](https://github.com/facebook/react/pull/15532). The reason is because it is
  not practical to opt a particular subtree into Concurrent Mode without opting in the entire root.
  The recommendation is to use createRoot instead.
* Stop patching `react.js` to export the symbols `Portal`, `Consumer`, `ForwardRef`, `Memo` and `Lazy`
  on the `React` type as they are never used.

### [v0.128](https://github.com/react4j/react4j/tree/v0.128) (2019-05-08) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.127...v0.128)

* Explicitly specify the name of the `CssProps.zIndex` property as jsinterop was incorrectly identifying the
  native property as `ZIndex`.

### [v0.127](https://github.com/react4j/react4j/tree/v0.127) (2019-05-03) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.126...v0.127)

* Upgrade the `org.realityforge.arez` artifacts to version `0.137`.
* Prefix the synthetic key generated by immutable props with the name of the containing component to ensure that
  across component types, that keys are unique.

### [v0.126](https://github.com/react4j/react4j/tree/v0.126) (2019-04-29) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.125...v0.126)

* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `2.23`.
* Remove `{@inheritDoc}` as it only explicitly indicates that the default behaviour at the expense of significant visual clutter.
* Remove intrinsic prop named `key`. End-users can specify a immutable prop which will synthesize a key to be passed
  to react runtime.
* Rename `@PostRender` annotation to to `@PostMountOrUpdate`.
* Add `Keyed.getKey(Object)` utility method.
* Fix bug where a null value for a `@Prop(immutable=true)` that had a type that was `Keyed` could generate a null pointer at runtime.
* Extract props variable to optimize code generation when a key is synthesized from multiple `@Prop(immutable=true)` props.
* Insert casts to improve code optimization opportunities when a key is synthesized from immutable props.
* Optimize code generation when a component has multiple default values for props by extracting a variable.

### [v0.125](https://github.com/react4j/react4j/tree/v0.125) (2019-04-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.124...v0.125)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.16.0`.
* Upgrade the `org.realityforge.arez` artifacts to version `0.136`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b21-6a027d2`.
* Improve the way defines are handled in closure by assigning the results of `goog.define` to a module local variable.

### [v0.124](https://github.com/react4j/react4j/tree/v0.124) (2019-04-07) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.123...v0.124)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.15.0`.
* Update the release process so that once react4j reaches 1.0, the releases will not be marked
  as pre-releases  on Github.

### [v0.123](https://github.com/react4j/react4j/tree/v0.123) (2019-04-05) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.122...v0.123)

* Upgrade the `org.realityforge.gir` artifact to version `0.08`.
* **\[core\]** Use `const` rather than `let` for module import to avoid closure compiler warning.
* **\[core\]** Improve the `react.externs.js` so that it generates no errors when closure compiler
  processes the file with ` --jscomp_error '*'` setting enabled.

### [v0.122](https://github.com/react4j/react4j/tree/v0.122) (2019-03-21) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.121...v0.122)

* Upgrade the `org.realityforge.arez` artifacts to version `0.133`.
* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b20-bfe6e22`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.13.0`.
* Start building `spritz` branch in `react4j-todomvc` as part of build and release cycle.
* **\[core\]** Correct the pom so dependencies are accurately recorded. This pom ceased to be
  accurate after the `jsinterop` and `elemental2` moved group ids and the `arez` dependency
  was added directly to the `react-core` module.

### [v0.121](https://github.com/react4j/react4j/tree/v0.121) (2019-02-24) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.120...v0.121)

* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b18-f3472e7`.
* **\[core\]** Upgrade the `com.google.jsinterop:base:jar` library to version `1.0.0-b2-e6d791f`
  released under groupId `org.realityforge.com.google.jsinterop`.

### [v0.120](https://github.com/react4j/react4j/tree/v0.120) (2019-02-21) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.119...v0.120)

* Upgrade the `org.realityforge.com.google.elemental2` artifacts to version `1.0.0-b17-6897368`.
* **\[processor\]** Fix the error message when a `type=TRACKING` component fails to access an arez
  dependency within the render method so that it makes sense with modern React4j.
* **\[dom\]** Remove methods from the `DOM` factory that have a parameter of type `java.util.List`
  as it was resulting in some very bad code in downstream systems. In particular it was too easy
  for developers to conditionally add elements to a list and then render the list. This resulted
  it React failing to identify nodes as identical if they did not specify a `key` field. This
  resulted in poorly performing code. Developers can still access similar functionality by invoking
  `.stream()` on the list if integration with lists is needed.

### [v0.119](https://github.com/react4j/react4j/tree/v0.119) (2019-02-14) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.118...v0.119)

* Add the `react4j-drumloop` sample application into set off used to track code size changes over time.
* Cleanup the react closure externs by removing `React.DOM` definition as well as historic parameters to
  `componentDidMount` and `componentDidUpdate`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.130`.

### [v0.118](https://github.com/react4j/react4j/tree/v0.118) (2019-02-08) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.117...v0.118)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.128`.
* **\[processor\]** Changed the way that the annotation processor generates errors. Previously if an error was
  detected, it was reported as a fatal error and the annotation processor halted the compile after the current
  processing round. This made development painful where one annotation processor consumed artifacts produced by
  another annotation processor or used multiple processing rounds to generate the required source as many errors
  would be produced due to missing artifacts that were completely unrelated to the original problem. Within React4j
  this scenario was common if dagger injection was enabled and React4j generated artifacts that were expected to be
  passed into the dagger annotation processor. Due to limitations imposed by the annotation processor framework,
  issues need to be reported in the round in which they are generated to avoid omitting diagnostic information
  about the error such as source location *but* reporting the issue as an error terminates the annotation
  processing rounds. To work around these limitations, React4j now reports all issues as `MANDATORY_WARNING` issues
  and adds a flag so that on the last round an error is generated if any of the `MANDATORY_WARNING` issues were
  generated. This is not ideal but is the best outcome we could find within the current annotation processing
  framework.

### [v0.117](https://github.com/react4j/react4j/tree/v0.117) (2019-02-04) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.116...v0.117)

* **\[core\]** Upgrade the `com.google.elemental2:*` libraries to version `1.0.0-b15-7a28038`
  released under groupId `org.realityforge.com.google.elemental2`.
* **\[processor\]** Immutable `@Prop` methods were unable to identify props that implemented `Keyed`
  via implementing an interface that extended `Keyed`. This has been fixed by delegating the decision
  of whether a type is assignable to `Keyed` to the compiler.
* **\[processor\]** Add support for enum typed immutable props.
* **\[processor\]** Ensure that the render method allows zero dependencies in the scenario where the
  first disposable prop is disposed and thus render terminates without observing any dependencies.

### [v0.116](https://github.com/react4j/react4j/tree/v0.116) (2019-01-31) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.115...v0.116)

* **\[core\]** Remove the `inject` parameter from the `@ReactComponent` annotation the value is derivable.

### [v0.115](https://github.com/react4j/react4j/tree/v0.115) (2019-01-30) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.114...v0.115)

* **\[core\]** Improve the javadocs on the `ReactComponent.Type` enums to clarify when a component will be
  re-rendered.
* **\[processor\]** Generate an error if a `@Prop` has specified `immutable=true` and either
  `shouldUpdateOnChange=ENABLE` or `observable=ENABLE`.
* **\[processor\]** If a `@Prop` has specified `immutable=true` and `shouldUpdateOnChange=AUTODETECT` then
  resolve the `shouldUpdateOnChange` parameter to `DISABLE`.
* **\[processor\]** Implement the `shouldComponentUpdate()` lifecycle method for all components, even if
  they have no props or no props where `shouldUpdateOnChange` resolves to `ENABLE`. This is so that components
  have consistent behaviour regardless of whether they require a prop or not. This is more consistent
  behaviour but is a breaking change compared to version `0.114` that would re-render components with no
  props every time their container was re-rendered.
* **\[processor\]** Add missing type parameters in generated component class to avoid `unchecked` compiler warning.
* **\[dom\]** Add explicit cast in generated `DOM` factory to avoid varargs compiler warning.
* **\[processor\]** Add support for defining react components with constructors that take parameters. These
  parameters are injected into the component similarly to `@Inject` annotated fields.

### [v0.114](https://github.com/react4j/react4j/tree/v0.114) (2019-01-30) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.113...v0.114)

* **\[core\]** Merge the `react4j-arez` artifact into `react4j-core` as the `react4j-core` artifact has never
  been used without also using `react4j-arez` in any production application.
* **\[core\]** Move the `ObservableValue` introspection utilities from `ReactArezComponent` into the new utility
  class `react4j.internal.arez.IntrospectUtil` as the code is not expected to be used by end-user code.
* **\[core\]** Move the `Observer` introspection utilities from `ReactArezComponent` into the new utility
  class `react4j.internal.arez.IntrospectUtil` as the code is not expected to be used by end-user code.
* **\[core\]** Extract some utility code out of `ReactArezComponent` into a new utility
  class `react4j.internal.arez.SchedulerUtil` as the code is not expected to be used by end-user code.
* **\[processor\]** Classes that extend `react4j.Component` but not `react4j.arez.ReactArezComponent` are now
  generated as `@ArezComponent(allowEmpty=true)` annotated classes. This means that the normal arez lifecycle
  methods (i.e. those annotated with `@PostConstruct`, `@PreDispose`, `@PostDispose` etc.) are available to all
  react4j components as are helper annotations such as `@CascadeDispose`. This removes the need to implement
  parallel capabilities into the react4j annotation processor and significantly simplifies integration with
  the injection framework.
* **\[core\]** Move `ReactArezComponent.getArezComponentId()` to `Component.getComponentId()` and
  `ReactArezComponent.getArezComponentName()` to `Component.getComponentName()` so that the methods are available
  to all react components.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.127`.
* **\[core\]** Guard the data emitted as debug state to ensure that all the required compile time properties are
  set to `true` before emitting debug values. This includes `react4j.store_debug_data_as_state` as well as the
  associated Arez compile time properties `arez.enable_spies` and/or `arez.enable_names`. This ensures that these
  compile time properties can be controlled independently while ensuring unused code is optimized out.
* **\[processor\]** Fix bug where the enhanced component classes would trigger the Arez scheduler twice if there
  was any schedulable elements that were part of the component. The second trigger would result in no action as
  there would be no pending tasks but it did result in more code being generated than required. Removing the custom
  react4j triggering resulted in reduced code size where this feature was used.
* **\[processor\]** Fix bug where the enhanced component classes with fields annotated with `@Dependency` would
  trigger at the incorrect time and potentially result in null pointer exceptions.
* **\[processor\]** Allow Arez annotations on react components that do not extend `ReactArezComponent` as there
  is no longer any reason to disallow that scenario.
* **\[core\]** Remove the `ReactArezComponent.getContext()` as it is no longer required within react4j. Users can
  add a similar method back in, if/when it is needed.
* **\[core\]** Remove the constructor in `ReactArezComponent` as it was only overridden to make it protected
  access but the class is abstract so can never be directly instantiated and thus the override had no impact.
* **\[processor\]** Generate the `populateDebugData(data)` method for components that extend the `ReactArezComponent`
  class rather than inheriting implementation from `ReactArezComponent`. The purpose is to make it easier to remove
  `ReactArezComponent` and use the compiler to optimize away unneeded code.
* **\[processor\]** Move the `ReactArezComponent.getRenderObserver()` method into the generated class as user code
  does not need access to the raw Observer instance.
* **\[core\]** Re-add the `Component.scheduleRender()` method that takes no parameters.
* **\[processor\]** Move the remaining logic in `ReactArezComponent` to classes generated by the annotation
  processor in preparation for removing `ReactArezComponent` and also enabling better optimization by the GWT
  and J2CL compilers.
* Archive the `react4j-widget` library as there is no desire or need to continue supporting it. While the library
  will likely continue to work, the project will not verify this going forward.
* **\[processor\]** Remove `react4j.internal.NativeAdapterComponent` and have the annotation processor generate
  the required infrastructure per-component. This affords more opportunities for the optimizing compilers to eliminate
  unused code and simplifies understanding of the generated classes.
* **\[core\]** Remove the `dagger` parameter from `@ReactComponent` as the framework has not supported alternative
  injection frameworks for a while and thus the parameter provided no actual value.
* **\[core\]** Completely rewrite the injection framework support is generated to use infrastructure supplied by
  the Arez component model now that all react4j components are Arez components.
* **\[core\]** Convert `Component.bindComponent()` to protected access and add invariant check to verify that it is
  not called multiple times.
* **\[core\]** Remove lifecycle method `Component.componentDidCatch(...)` and replace with annotation `@OnError`.
* **\[processor\]** Move from `Disposable.dispose( this )` to `((Arez_MyComponent) this).dispose()` so that the
  optimizing compiler can inline the method where possible.
* **\[core\]** Change `ReactElement.createFragment(...)` and `React.createFragment(...)` methods to accept a
  nullable key parameter and add add a factory method in `react4j.dom.DOM` that accepts a key. Keys are valid on
  fragment nodes and the code has been change to reflect this.
* **\[core\]** Add initial support for `<Suspense/>` component.
* **\[dom\]** Add support for `ReactDOM.createRoot(...)` as the first step for enabling usage of concurrent mode.
* **\[core\]** Add the `immutable` parameter to the `@Prop` annotation. If set to true then the react component
  will be recreated if the value of the prop changes. This is accomplished by synthesizing a key for the component
  from the set of immutable props. If any prop has been marked as immutable then it will no longer possible for the
  developer to manually specify the key parameter.
* **\[core\]** The error message from annotation processor when a user defines a prop named `key` suggested using
  a method that does not exist and has not worked since upgraded to React 16+ so update the error message.
* **\[core\]** Remove `React.createFragment()` in favour of directly calling `ReactElement.createFragment()`.
* **\[core\]** Remove every variant of `React.createElement(...)`. The code and/or behaviour has been inlined into
  the `DOM` factory methods to aid code optimization process.
* **\[core\]** Move `React.createStrictMode(...)` to `ReactElement.createStrictMode(...)`.
* **\[core\]** Remove the `ReactArezComponent` class and replace it with the `type` parameter on the
  `@ReactComponent` annotation. The possible values for `type` are:
  - `STATEFUL`: The component can use fields or lifecycle methods.
  - `TRACKING`: The component is `STATEFUL` and the render method is annotated with `@Observe` and will
    re-render when a dependency changes.
  - `MAYBE_TRACKING`: The component is `TRACKING` but the render method may have zero dependencies.
* **\[core\]** Remove the `allowNoArezDeps` parameter from the `@ReactComponent` annotation as it no longer
  provides any value over `MAYBE_TRACKING` components.
* **\[core\]** Add support for `@Prop(disposable=ENABLE)` props in `@ReactComponent(type=STATEFUL)` components
  and update `@Prop(disposable=AUTODETECT)` to resolve correctly detect disposable props even in `STATEFUL`
  components.
* **\[core\]** Add support for `@Prop(observable=ENABLE)` props in `@ReactComponent(type=STATEFUL)` components
  and update `@Prop(disposable=AUTODETECT)` to resolve correctly detect observable props even in `STATEFUL`
  components. See javadocs for further details.
* **\[processor\]** Remove an "optimization" that resulted in the `shouldComponentUpdate()` react lifecycle being
  omitted if all the props were `observable=DISABLE` and `shouldUpdateOnChange=ENABLE`.

### [v0.113](https://github.com/react4j/react4j/tree/v0.113) (2019-01-21) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.112...v0.113)

* **\[core\]** Add the parameter `allowNoArezDeps` to the `ReactComponent` component to support scenario
  where an Arez/React4j component may render without any arez dependencies. This may be acceptable.

### [v0.112](https://github.com/react4j/react4j/tree/v0.112) (2019-01-18) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.111...v0.112)

* **\[processor\]** Extract the definition of the lifecycle methods from out of each of the generated
  component subclasses into interfaces such as `react4j.OnComponentDidMount`, `react4j.OnComponentDidUpdate`
  etc. The native implementations then implement each lifecycle interface as required rather than synthesizing
  a aggregate lifecycle definition custom to that component.
* **\[core\]** Simplify the `Component.storeDebugDataAsState()` to make it easier to identify when the debug
  state is updated.
* **\[core\]** Move internal classes used by the code generator to the `react4j.internal` package so that it
  is clear that they are not intended to be referenced by external code and may change between releases. The
  classes move include the classes named `react4j.On*`, `react4j.Native*` and `react4j.ComponentConstructorFunction`.
* **\[core\]** Move the method used used to access compile time configuration from the `ReactConfig` class to the
  `React` class and change the access level of `ReactConfig` to package access.
* **\[processor\]** Remove the `prevProps` parameter from the synthetic `componentDidUpdate()` lifecycle method
  on the java component if it is unused.
* **\[processor\]** Improve the code generated for the synthetic `shouldComponentUpdate()` lifecycle method on
  the java component by eliminating unused local variables if unused.

### [v0.111](https://github.com/react4j/react4j/tree/v0.111) (2019-01-17) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.110...v0.111)

* **\[processor\]** Ensure that the synthesized lifecycle methods are private unless they are annotated by the
  `@arez.annotations.Action` annotation. Prior to this change different methods had different policies, some
  were optionally private, some were final and some were package access.
* **\[core\]** Fix bug introduced into `React.createFragment(...)` in `0.109` as a result of setting the
  `$typeof` key to `React.Fragment` rather than `React.Element`.

### [v0.110](https://github.com/react4j/react4j/tree/v0.110) (2019-01-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.109...v0.110)

### [v0.109](https://github.com/react4j/react4j/tree/v0.109) (2019-01-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.108...v0.109)

* **\[core\]** Rename the `@OnPropChanged` annotation to `@OnPropChange`.
* **\[core\]** Add support for the `Component.componentPreUpdate(...)` lifecycle method that is invoked prior
  to updating a.k.a. re-rendering a component. This is mapped to the native `getSnapshotBeforeUpdate`
  lifecycle method except that the application developer can not return a value from the method.
* **\[processor\]** Change the way observable props are generated so that the change detection for the props
  occurs in the `shouldComponentUpdate(...)` and `componentPreUpdate(...)` lifecycle steps rather than the
  `shouldComponentUpdate(...)` and `componentDidUpdate(...)` lifecycle steps. This means that the `@Memoize`
  annotated methods that have a return value derived from observable props will be marked as possibly stale
  before a call to `render()` occurs and the component will not need to be re-rendered to reflect a changed
  value returned from the `@Memoized` method.
* **\[core\]** Change the `@OnPropChange` annotation so that it accepts one or more parameters. Each parameter
  represents a prop that is tracked for changes. Any time a change occurs in any prop then the annotated method
  is invoked. The parameters are mapped to props based on a naming pattern or via an explicit `@PropRef`
  annotation if the naming convention is insufficient.
* **\[core\]** Change the `@OnPropChange` annotation so that it accepts a `phase` parameter that controls
  whether the method is invoked before the component is updated/rendered or after the component is updated.
* **\[core\]** Introduce the `@PreUpdate` annotation that can be used to annotate a method that is invoked
  when a component is about to be updated or re-rendered.
* **\[core\]** Introduce the `@PostUpdate` annotation that can be used to annotated a method that is invoked
  when a component has been updated or re-rendered. This replaces the ability to override the
  `componentDidUpdate()` lifecycle method.
* **\[core\]** Introduce the `@PostMount` annotation that can be used to annotated a method that is invoked
  after a component has been mounted. This replaces the ability to override the `componentDidMount()` lifecycle method.
* **\[core\]** Reduce the access level of `Component.start()` method so that it is package access and
  no longer accessible by subclasses. It has not been modifiable since version `0.102` of React4j and there is
  no reason to read it when writing components.
* **\[arez\]** Fix invariant failure that occurred when generating debug state for a component that extends
  `ReactArezComponent` that has observed an Arez observable that has no accessor defined.
* **\[core\]** Remove invariant checking based on the state of the react component. This invariant checking
  was duplicating functionality already implemented by the underlying `ReactJS` library. This also involved
  removing the compile time setting `react4j.check_component_state_invariants` and the accessor
  `ReactConfig.checkComponentStateInvariants()`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.122`.
* **\[processor\]** Optimize the processor so that `shouldComponentUpdate(...)` methods are not generated on
  `Lite` native components if the component has `@PropValidate` annotated props but has no observable props
  and is not an Arez component. This reduces the code size and eliminates a noop lifecycle method.
* **\[processor\]** Optimize the processor so that the `componentDidUpdate(...)` and `componentDidMount(...)`
  methods are not generated on a non-arez component unless those features are required to implement other
  capabilities. This reduces code size and eliminates unnecessary work for React during rendering.
* **\[arez\]** Update the `ReactArezComponent` class to change the strategy via which resources are released
  to use the `@PreDispose` annotation from Arez to mark the component as unmounted. This avoids the overhead
  associated with pausing and resuming the scheduler and reduces code size by using builtin Arez mechanisms.
* **\[core\]** Introduce the `@PostRender` annotation that can be used to annotated a method that is invoked
  when a component has been rendered. This can be after the component has been mounted or after it has been
  updated. This method is invoked before the `@PostUpdate` annotated method in the `componentDidUpdate(...)`
  method and before the `@PostMount` annotated method in the `componentDidMount(...)` method.
* **\[core\]** Re-introduce the boolean parameter `force` to `Component.scheduleRender()` method so that it
  is possible to trigger a re-render but still pass through the `shouldComponentUpdate()` lifecycle stage if
  present.
* **\[processor\]** Avoid generating `componentPreUpdate` lifecycle method for components that have have
  observable props and no `@PreUpdate` or `@OnPropChange(phase=PRE)` annotated methods. This was achieved by
  migrating all observable prop monitoring into `shouldComponentUpdate` and out of the `componentPreUpdate`
  lifecycle method.
* **\[processor\]** Avoid generating the `shouldComponentUpdate` lifecycle method if changes to any prop will
  cause the component to be re-rendered and there are no observable methods for component.
* **\[core\]** Remove the `ReactArezEnvironment` class as it no longer provides any value given that the Arez
  scheduler is paused until a subsequent microtask.
* **\[core\]** Introduce the `@PreUnmount` annotation that can be used to annotate a method that is invoked
  before a component is unmounted or detatched. This replaces the ability to override the
  `componentWillUnmount()` lifecycle method.
* **\[core\]** Remove the `React.cloneElement(...)` method as it has never been used by any React4j application
  and if it was used it could be reimplemented in java rather than relying on javascript extern.
* **\[dom\]** Remove the invariant check in `ReactDOM.render(...)` that duplicates check already present in
  native react.js code.
* **\[core\]** Remove the unused `React.isReactElement(...)` method.
* **\[core\]** Import Closure Compiler annotated externs for react. This is needed to support the closure
  compiler compilation stage that is part of J2CL compilation.
* **\[core\]** Remove the `Component.getKey()` as it is not expected to work as keys are not stored on props.
* **\[core\]** Mark the `Component.props` and the `Component.state` externs as `@nosideeffects` so closure
  compiler will remove accesses if results are unused.
* **\[core\]** Use the builtin mechanisms for defining Arez component ids. The React4j previously shared a
  single generator across every component while the new version has a per-type generator. Thew builtin variant
  produces slightly more optimized code.
* **\[processor\]** Ensure that components that extend `ReactArezComponent` never implement `DisposeTrackable`
  as this feature does not make sense for a react component.
* **\[processor\]** Remove `isDisposed( this )` check inside `render()` method for components that extend
  `ReactArezComponent` as there should be no scenario where the component is disposed without going through
  `componentWillUnmount()` lifecycle stage. Eliminating this check reduces the size of code.
* **\[arez\]** Refactor the `react4j.arez.Arez` module to remove the inherit from `react4j.dom.Dom` as it is
  no longer used.
* **\[processor\]** Change the name of the interfaces used to construct the components from `BuilderN` to
  `StepN` to improve clarity as the `"Builder"` nomenclature is over-used in that class.
* **\[core\]** Ensure that component elements have their key initialized to `null` rather than leaving it
  `undefined`. This results in monomorphic code when processing these type of elements and eliminates a
  source of deoptimization that can occur in V8. This also eliminates a correctness bug that can occur in
  fragments that contain a list of keyed and unkeyed elements which resulted in incorrect behaviour during
  reconciliation.
* **\[core\]** Change the `$$typeof` property on fragments from `Symbol(react.element)` to `Symbol(react.fragment)`.
  The change seems to have no behavioural difference as the reconciler uses `type` field to control behaviour
  but it makes it easier for humans visually inspecting data.
* **\[core\]** Remove addition of empty string to `key` element when supplied as a prop and instead just
  cast the key to a string. The supporting infrastructure no longer supplies non-string keys and adding an
  empty string will stop the GWT compiler from removing `toString()` on any subclass of `Object` (i.e. no
  `toString()` methods are candidates for dead code elimination).
* **\[processor\]** Eliminate the static inner class named `Prop` from the enhanced react component if it is empty.
* **\[processor\]** Rename the generated dagger helper class from `[MyComponent]DaggerFactory` to
  `[MyComponent]DaggerComponentExtension` to align with the same pattern in Arez.
* **\[core\]** Remove the `@PreUnmount` annotation as it is identical in function to `@PreDispose` now that
  all react4j components are Arez components.

### [v0.108](https://github.com/react4j/react4j/tree/v0.108) (2018-11-22) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.107...v0.108)

* **\[processor\]** The generated builder previously supported a `child(ReactNode)` method on components that
  supported multiple children. The `child(ReactNode)` supported accumulation of children over multiple
  invocations. This feature was not used in any downstream project and made certain optimizations difficult.
  This method is no longer generated. A method with the same function in `Context.Provider` class was also
  removed for the same reason.
* **\[core\]** Remove `react4j.Key` as the values is always converted to a string so convert at API layer.
* **\[processor\]** Refactor the annotation processor to directly create `ReactElement` instances. This
  reduces some memory overhead by:
  - eliminating the need to handle `key` as a prop for components.
  - eliminating the duplication of the props object.
  - inlining the setting of default props into component builder. This eliminated the need to set the static
    property `defaultProps` onto the component constructor function.
* **\[core\]** Remove the `React.createElement(...)` methods used for creating component `ReactElement`
  instances as they are no longer used anymore.

### [v0.107](https://github.com/react4j/react4j/tree/v0.107) (2018-11-19) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.106...v0.107)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.115`.
* **\[processor\]** Simplify the way that the provider is generated for dagger injection enabled components.
  This goal was to eliminate a level of indirection that was previously required due to the type parameters on the
  `react4j.Component` class. The indirection is still required if the component class is not public but can be
  eliminated when the component class is public and thus guaranteed to be accessible from the associated
  `DaggerComponent`.

### [v0.106](https://github.com/react4j/react4j/tree/v0.106) (2018-11-08) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.105...v0.106)

* **\[processor\]** Upgrade the version of javapoet to `1.11.1`.
* **\[processor\]** Upgrade the Dagger2 support to version `2.19`.
* **\[dom\]** Remove the methods from the `DOM` factory class that accepted `JsArray` parameters. These
  methods have never been used in downstream projects.
* **\[core\]** Rewrite the javascript `React.createElement(...)` method in java. This allows the GWT2.x/J2CL
  compilers to alias methods and optimize the code. This will will result in smaller code sizes as the number
  of calls to that method increases but may increase the size of the code in smaller code bases.
* **\[processor\]** Fix a bug in introduced when the annotation processor that started to optimize prop names
  that resulted in the keys of the `defaultProps` value being `undefined` and thus no default prop values being
  supplied.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.114`.
* **\[processor\]** Move the constant holding the `ComponentConstructorFunction` instance field to a separate
  inner class to avoid having to check `<clinit>` has been invoked prior to accessing methods on synthetic
  subclass. This results in a modest decrease in size per react component.
* **\[processor\]** Move the constant holding the `PROP_myKey` static constants to a separate class to avoid
  the creation of a `<clinit>` when not needed. Rename the constant to remove the `PROP_` prefix now that it is
  no longer needed.
* **\[processor\]** Move the injection support into static inner class within generated component to avoid
  the creation of a `<clinit>` when not needed. This results in small decrease in code size.

### [v0.105](https://github.com/react4j/react4j/tree/v0.105) (2018-11-02) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.104...v0.105)

* Convert several of the `React.createElement(...)` methods to be native methods to avoid passing an explicit
  `undefined` parameter as the GWT2.x compile is unable to optimize this scenario.

### [v0.104](https://github.com/react4j/react4j/tree/v0.104) (2018-11-02) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.103...v0.104)

* Update the javascript included within package to react version `16.6.0`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.112`.
* **\[processor\]** Change the way the Dagger2 factory is defined by switching from a static method using a
  `@Provide` annotation to using an abstract method using a `@Binds` annotation. This results in more optimized
  code being output by the dagger compiler.
* **\[arez\]** Set the `@Observe.reportResults` parameter to `false` for the `ReactArezComponent.trackRender()`
  method so that the result of rendering are not reported to the spy system. This eliminates a significant
  performance degradation that occurs when naive spy listeners serialize the results.

### [v0.103](https://github.com/react4j/react4j/tree/v0.103) (2018-10-19) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.102...v0.103)

* **\[core\]** Remove the `Annotations.gwt.xml` gwt module and include source from `React.gwt.xml` gwt module.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.111`.
* **\[core\]** Remove the `@Callback` annotation. It encouraged poor practices such as using callbacks when not
  needed and often led to over use of `@Action` annotation for arez based react components.

### [v0.102](https://github.com/react4j/react4j/tree/v0.102) (2018-10-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.101...v0.102)

* **\[core\]** Remove the `@State` annotation. This is the first step to removing the infrastructure to
  support the traditional react state model.
* **\[core\]** Replace the compile time constant `react4j.arez.store_arez_data_as_state` with
  `react4j.store_debug_data_as_state`, expose it via `ReactConfig.shouldStoreDebugDataAsState()` and move
  the configuration property to the `[core]` module.
* **\[arez\]** Remove the empty lifecycle methods in the `ReactArezComponent` class and enhance the annotation
  processor so that they are no longer needed to store arez details as debug data.
* **\[core\]** Move the capability to store arbitrary debug data as react component state into the
  `react4j.Component` class from the `react4j.arez.ReactArezComponent` class.
* **\[core\]** Remove the `nextState` parameter from `Component.shouldComponentUpdate(...)` and
  `Component.componentDidUpdate(...)` as it is no longer used now that individual components do not manage
  state.
* Compile with `-parameters` passed to javac so that debug information includes parameter names. This will
  result in the annotation processor generating methods using real parameter names rather than synthesized
  parameter names (such as `arg0`) in java version 9+.
* **\[processor\]** Optimize the code generated for detecting changes in props annotated with
  `@Prop(shouldUpdateOnChange=true)` but not `@Prop(observable=false)` by immediately returning `true` when
  a change is detected and avoiding comparing any further props that may exist.
* **\[core\]** Add support for the `shouldUpdateOnChange` parameter on the `@Prop` annotation for non-arez
  subclasses. This involved moving functionality from `ReactArezComponent` back to `arez.Component` class,
  marking the `shouldUpdateOnChange()` as package-access and final and updating the annotation processor to
  generate the native glue code only if there is any logic generated to support `shouldUpdateOnChange`.
* **\[core\]** Make the methods `Component.performComponentDidUpdate(...)` and `Component.performComponentDidMount()`
  final and package access as no longer any need to allow middleware to customize these methods.
* **\[core\]** Remove the mechanisms by which component authors can update react state.
* **\[core\]** Fix a bug where methods annotated with `@Prop(observable=true)` that return a primitive
  value would always force a re-render of the component even if the value is the same.
* **\[core\]** Remove the `arez.Procedure` interface as it is no longer used.
* **\[core\]** Reduce the access level of `ReactConfig.checkComponentStateInvariants()` to package access as
  not intended to be used outside the package.
* **\[core\]** Remove the unused method `ReactConfig.isProductionMode()` and the associated compile time
  constant `react4j.environment`.
* **\[core\]** Remove the `force` parameter from the method `Component.scheduleRender()` as every render
  triggered through this mechanism is expected to explicitly render the component.
* **\[processor\]** Remove the `@Action` annotation from the generated `shouldUpdateOnPropChanges`
  method as it is unnecessary and increases codesize and decreases runtime performance.
* **\[processor\]** Eliminate numerous null checks when implementing `Component.shouldUpdateOnPropChanges()`
  using the annotation processor by moving the null check into the caller. This resulted in a significant
  decrease in code-size and a slight performance improvement.
* **\[core\]** Remove the `prevProps` parameter from the method `Component.componentDidUpdate()` as component
  writers can not safely use parameter and are expected to make use of annotation processor features instead.
* **\[core\]** Introduce the `@OnPropChanged` annotation that can be applied to methods that will be invoked
  in the `Component.componentDidUpdate()` lifecycle stage when a prop has actually changed.

### [v0.101](https://github.com/react4j/react4j/tree/v0.101) (2018-10-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.100...v0.101)

* **\[processor\]** Fix a bug where primitive types that had a `@Nonnull` annotation were being
  incorrectly boxed as `Objects.requireNonNull(...)` was invoked on the value before being passed
  to prop setter. This resulted in the value being boxed prior to reaching the `@DoNotAutobox` method.

### [v0.100](https://github.com/react4j/react4j/tree/v0.100) (2018-10-08) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.99...v0.100)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.109`.
* **\[dom\]** Introduce a functional interface `react.dom.ReactDOM.RenderCallbackFn` to replace the use of
  `react.Procedure` as the type of the callback in the top-level rendering methods.
* **\[dom\]** Introduce a functional interface `react.dom.ReactDOM.BatchedUpdatesFn` to replace the use of
  `react.Procedure` as the type of the callback for the `ReactDOM.batchedUpdates(BatchedUpdatesFn)` methods.
* **\[dom\]** Introduce several more utility methods to the dom factory class that accepts primitive values
  as content.

### [v0.99](https://github.com/react4j/react4j/tree/v0.99) (2018-10-05) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.98...v0.99)

* **\[processor\]** Generate a compile time error if a `@Prop` annotated method returns a boxed primitive and
  is annotated with `@Nonnull` annotation. The code should just use the primitive type.
* **\[processor\]** Generate a compile error if a component that does not extend `ReactArezComponent` has a field
  annotated with an arez annotation.

### [v0.98](https://github.com/react4j/react4j/tree/v0.98) (2018-10-04) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.97...v0.98)

* **\[arez\]** Fixed a bug in `ReactArezComponent` that could cause `react.js` to generate an error message
  indicating `setState()` or `forceRender()` was invoked from within the `render()` method of a component.
  This could occur where an event handler caused a change that resulted in a component re-rendering and ceasing
  to observe a component with `disposeOnDeactivate` set to `true`. The component would then dispose, potentially
  triggering changes and triggering re-rendering other components (via `setState()` or `forceRender()` ) from
  within the `render()` of the react component that un-observed the arez components. The fix was to pause the
  arez scheduler and re-enable it in a microtask following the render.

### [v0.97](https://github.com/react4j/react4j/tree/v0.97) (2018-10-02) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.96...v0.97)

* Update the javascript included within package to react version `16.5.2`.
* **\[arez\]** Make the `ReactArezConfig` class public to make the compile time constants accessible by
  generated component classes.
* **\[arez\]** Change the way subclasses of `ReactArezComponent` are generated. If a `ReactArezComponent` does
  not override `componentDidMount()` or does not override `componentDidUpdate()` then generate a `LiteLifecycle`
  and a `LiteNativeReactComponent` along with the normal `Lifecycle` and `NativeReactComponent`. If the compile
  time setting `ReactArezConfig.shouldStoreArezDataAsState()` returns `false` then use `LiteNativeReactComponent`
  when constructing the components. This improves dead code elimination in both the GWT and J2CL toolchain as
  the `componentDidMount()` and/or `componentDidUpdate()` methods need not be generated and empty and can instead
  just be removed by the compiler. This also has a positive impact on runtime performance.
* **\[processor\]** Generate a compile time error if the return type of a method annotated with `@Prop` is a type
  variable that was declared on the method.
* **\[processor\]** Add a mechanism for minimizing prop names in generated component class to reduce code size.
  A `@JsType` annotated `Props` class does not work in J2CL as the closure compiler would need to be told about
  the keys as part of externs so that the `Object` type is correctly typed. This would stop the names being
  renamed. Manually minimizing the keys based on a compile time constant `react4j.minimize_prop_keys` will work
  around this limitation and make sure the optimization is available in both GWT2 and J2CL builds.
* **\[core\]** Add the `observable` parameter to the `@Prop` annotation. This will control whether a prop is
  modelled as an Arez observable. This change fixes a bug where not all prop changes were detected and reported.
  The default value is to `AUTODETECT` which eliminates observation unless the annotation processor detects that
  it may be needed (i.e. the prop must be on subclasses of `ReactArezComponent` and the component must declare
  methods annotated with `@Computed`, `@Memoize` or `@Observed`). This reduces output code size in scenarios where
  observable props are not needed.
* **\[core\]** Add the `@PropValidate` annotation to identify method that must be called when the value of a prop
  is initially specified or changed. This method is expected to generate an invariant failure if an invalid prop
  is passed to the component and will optimized out if `ReactConfig.shouldValidatePropValues()` returns `false`
  which is the default for production builds. The framework will test the type and nullness of props even if there
  is no `@PropValidate` for prop.
* **\[processor\]** If the element annotated by the `@PropDefault` annotation is not accessible in the package that
  has the type annotated with `@ReactComponent` then generate a descriptive compile error.
* **\[processor\]** Generate a compile error if the method annotated by `@Prop` is public.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.108`.

### [v0.96](https://github.com/react4j/react4j/tree/v0.96) (2018-09-21) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.95...v0.96)

* Update the javascript included within package to react version `16.5.1`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.107`.
* **\[arez\]** The values of computed and observable properties were not being updated under arez state key
  when they were being changed resulting in incorrect values being presented in DevTools inspector and
  profiler. This has been fixed.
* **\[arez\]** Subclasses of `ReactArezComponent` are no longer able to store values in reacts native state,
  thus there is no need to namespace the dependency state under a key named `arez` so remove the key and add
  dependency data directly into state object.
* **\[arez\]** The method `ReactArezConfig.shouldStoreArezDataAsState()` should never return true if
  `Arez.areSpiesEnabled()` returns false as the Arez spy system is used to extract and store arez state. The
  `shouldStoreArezDataAsState()` method has been adapted to incorporate this condition and this guard has been
  removed from other locations where it is no longer required.
* **\[arez\]** Unwrap boxed values and convert enums into strings when emitting arez property values in the
  react state.
* **\[arez\]** If a dependency was removed from a component and `ReactArezConfig.shouldStoreArezDataAsState()`
  returns true then `ReactArezComponent` could result in an infinite loop trying to converge state as reacts
  `setState` merges new state and will not replace state. This has been fixed by setting the value of the keys
  to `undefined` value.
* **\[arez\]** Expose the id and name of the react component according to arez as state values under the
  `Arez.id` and `Arez.name` keys when `ReactArezConfig.shouldStoreArezDataAsState()` returns true.
* **\[core\]** Introduce several methods that wrap around the component lifecycle method so frameworks can
  customize particular lifecycle steps to provide custom behaviour. This was already possible with specific
  lifecycle steps but support for customization has been added to the lifecycle methods `componentDidMount()`,
  `componentDidUpdate()`, `componentDidCatch()` and `shouldComponentUpdate(...)`.
* **\[arez\]** Rework `ReactArezComponent` so that it overrides the `performComponentDidMount()` method and the
  `performComponentDidUpdate()` method to store Arez dependencies in react state when
  `ReactArezConfig.shouldStoreArezDataAsState()` returns `true`. This means that sub-classes no longer need to
  call the `super.componentDidMount()` method or the `super.componentDidUpdate()` method if the component overrides
  the `componentDidMount()` method or the `componentDidUpdate()` method.
* **\[arez\]** Avoid comparisons of state in `ReactArezComponent.shouldComponentUpdate()` as state is unused in arez
  based react components and `ReactArezComponent.scheduleArezKeyUpdate()` has been updated for forcefully schedule
  a render which skips `ReactArezComponent.shouldComponentUpdate()` lifecycle step.
* **\[dom\]** Add missing "bottom" property to `react4j.dom.proptypes.html.CssProps`.
* **\[arez\]** Specially handle `java.util.stream.Stream` values when emitting them as values in component state
  for DevTools by converting them to string constants. This vastly improves the readability in the DevTools.
* **\[arez\]** Add mechanisms for customizing values emitted in state for DevTools by adding a template method
  named `ReactArezComponent.renderValueAsState(ObservableValueInfo,Object)`.
* **\[arez\]** Add guard to avoid infinite recursion as a result storing dependencies in react state causing a
  re-render.
* **\[processor\]** Generate a compile error if methods annotated with `@Callback` have a public access modifier
  unless the method is defined as a default method on an interface.

### [v0.95](https://github.com/react4j/react4j/tree/v0.95) (2018-09-06) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.94...v0.95)

* **\[arez\]** Restructure spy code to prepare for Arez version `0.107`.

### [v0.94](https://github.com/react4j/react4j/tree/v0.94) (2018-08-31) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.93...v0.94)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.106`.
* **\[processor\]** Add support for the `arezOnlyDependencies` parameter on the `arez.annotations.Computed`
  annotation.
* **\[arez\]** Migrate the classes from the `react4j.arez.spy` package to the
  `org.realityforge.arez.spytools:arez-spytools:jar:0.30` artifact.

### [v0.93](https://github.com/react4j/react4j/tree/v0.93) (2018-08-23) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.92...v0.93)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.105`.
* **\[arez\]** Update the `org.realityforge.arez.spytools` dependency to version `0.29`.
* **\[processor\]** Avoid overriding methods annotated with `@Memoize` or `@Computed` to lower priorities if
  the method is private as private methods are not valid in this scenario. The Arez annotation processor will
  detect this scenario and generate an error and will report the error on the original method if the react4j
  annotation processor does not override the method.
* **\[core\]** Change the method `ReactNode.of(long)` as boxed primitives can not be processed by `react.js`
  and you can not safely convert a `long` value to a javascript `number` value. The method instead converts the
  long value to a string before passing the value to react.
* **\[core\]** Change the implementation of the `ReactNode.of(...)` methods that have a primitive parameter
  type other than long so that they are not boxed prior to being passed to react.

### [v0.92](https://github.com/react4j/react4j/tree/v0.92) (2018-08-07) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.91...v0.92)

* **\[core\]** Add the `disposable` parameter to `@Prop` that allows more explicit control over whether a
  prop needs to be checked in `isAnyPropDisposed()` to see if the prop is disposed prior to rendering
  the component.

### [v0.91](https://github.com/react4j/react4j/tree/v0.91) (2018-08-06) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.90...v0.91)

* **\[processor\]** Change the annotation processor to eliminate type checking in `@Prop` method
  implementation when `ReactConfig.shouldCheckInvariants()` return false. This also reduces code size.

### [v0.90](https://github.com/react4j/react4j/tree/v0.90) (2018-08-01) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.89...v0.90)

* **\[processor\]** Change the annotation processor so that only whitelisted annotations are copied to
  subclasses and overridden methods. The whitelisted annotations include `javax.annotations.Nonnull`,
  `javax.annotations.Nullable` and `java.lang.Deprecated`.
* **\[processor\]** Change the way the annotation processor handles the Arez annotation `@Computed`.
  If the annotation processor detects that the user has not supplied a `priority` then the annotation
  processor will override the method and redefine the priority as `LOWEST`. This simplifies code in
  `@Computed` methods that will not attempt to schedule before the `render()` reaction that is scheduled
  at `LOW` priority and will thus be un-observed if no longer needed. In the scenario where a `@Computed`
  is derived from a `@Prop` that is an Arez element and the arez element is disposed, then the `render()`
  method will trigger, render null and un-observe any `@Computed` methods. Thus `@Computed` methods will
  not need to include checks to see if props are disposed unless accessed from other observers.
* **\[processor\]** Detect `@Memoize` methods and default priority to `LOWEST` unless specified.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.104`.
* **\[processor\]** Generate a compile error if any react4j component lifecycle method is annotated with
  an Arez annotation that is not `@Action`.
* **\[processor\]** Generate a compile error if a component has arez annotations but does not
  extend `ReactArezComponent`.
* **\[processor\]** Generate a compile error if a `@Prop` method is annotated with an arez annotation.
* **\[processor\]** Generate a compile error if a `@State` method is annotated with an arez annotation.
* **\[processor\]** Generate a compile error if a `@Prop` method returns a collection or array where
  the elements contained by the collection or array are Arez elements. This can result in significant
  problems if the arez component is disposable as an arez component can be disposed but that will not
  necessarily trigger a re-render of the react component (unless the react component has deliberately)
  observed the component and/or is coded to correctly skip disposed elements. This is considered too
  difficult to get right and now generates an error.

### [v0.89](https://github.com/react4j/react4j/tree/v0.89) (2018-07-31) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.88...v0.89)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.103`.
* **\[dom\]** Improve the documentation of `react4j.dom.proptypes.html.RefConsumer` to clarity that the
  element is nullable.
* **\[arez\]** Skip checking of state in `ReactArezComponent.shouldComponentUpdate()` unless
  `ReactArezConfig.shouldStoreArezDataAsState()` returns `true`. State is not used in subclasses of
  `ReactArezComponent` except during development to simplify debugging. Eliminating the check reduces a
  small amount of code in production mode.
* **\[arez\]** Replace `reportPropsChanged(...)` with `shouldComponentUpdate(...)` on the `ReactArezComponent`
  class so that subclasses can control whether a prop change should update component rather than just reporting
  a prop change as an arez change.
* **\[processor\]** Avoid generating an empty `shouldComponentUpdate(...)` if there are no props to check and
  instead add a concrete implementation on the `ReactArezComponent` class. This reduces the code size of compiled
  output.
* **\[core\]** Add the `shouldUpdateOnChange` parameter to the `@Prop` annotation that will determine if the
  `@Prop` needs to be checked as part of the `shouldComponentUpdate(...)` lifecycle method.
* **\[arez\]** Mark all props as `@Observable(readOutsideTransaction=true)` so that the props can be read outside
  of an arez transaction. It is not uncommon for callbacks to access props but no other arez elements. Marking
  props as `readOutsideTransaction=true` eliminates the need to add aditional `@Action` annotations everywhere.
* **\[dom\]** Remove the `ReactDOM.createElement(...)` overlay methods. The methods simply delegate to the
  equivalent method on the `React` class. This level of indirection is no longer required.
* **\[dom\]** Add `DOM.<element>(..., JsArray<ReactNode> children)` methods. These constructions are useful when
  creating elements where you do not want the overhead of Java collections.
* **\[dom\]** Add `DOM.thead(...)` and `DOM.tbody(...)` element factory methods.
* **\[arez\]** Configure the `ReactArezComponent.trackRender()` method so that it can observe lower priority
  dependencies. This is useful when `@Computed` properties are accessed from within the `render()` method. If
  the `@Computed` is a lower priority than render (which is `LOW`) then it will not be recalculated if the render
  method ceases to observe the value. This is of particular relevance as the render method will cease to observe
  all computeds if a `@Prop` is disposed as render will return a `null`. Thus if the `@Computed` also depends upon
  the prop and is not referenced by an autorun within the class then it need not check whether the prop is disposed.
* **\[dom\]** Add `DOM.fragment(...)` method aliases for `React.createFragment(...)` methods.
* **\[core\]** Introduce the `react4j.Key` class and support either an integer or string `key` prop in the builders
  generated by the annotation processor and the `DOM` factory methods.

### [v0.88](https://github.com/react4j/react4j/tree/v0.88) (2018-07-26) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.87...v0.88)

* Release to fix deployment process

### [v0.87](https://github.com/react4j/react4j/tree/v0.87) (2018-07-25) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.86...v0.87)

* **\[downstream-test\]** Test against output of J2CL over time to ensure that no size regressions occur.
* **\[dom\]** Fix message generated for invariant check in `HtmlGlobalFields.className(...)` where the
  classname element has leading whitespace.
* Update the release process to remove artifacts staged in previous releases.
* **\[arez\]** Enhance `ReactArezComponent.onRenderDepsChanged()` to avoid scheduling render if a render
  has already been scheduled and has not yet occurred.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.100`.
* Remove dependency on the `org.realityforge.arez.browserlocation:arez-browserlocation:jar` artifact.

### [v0.86](https://github.com/react4j/react4j/tree/v0.86) (2018-07-20) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.85...v0.86)

### [v0.85](https://github.com/react4j/react4j/tree/v0.85) (2018-07-19) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.84...v0.85)

* **\[processor\]** Avoid using synthetic parameter names in cached handlers and overridden methods
  when generating code to support the `@Callback` annotated methods. Instead use the names of the
  implementing method or the defining interface as appropriate. This produces more readable code
  and improves spy logging messages when the component is a subclass of `react4j.arez.ReactArezComponent`.
* **\[processor\]** Cleanup the method that creates the callback handler to avoid defining a local
  variable if it immediately returned.
* **\[core\]** Add the `@define` configuration for the compile-time constants that is required for the
  closure compiler to correctly process constants at compile time.
* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.12.0` to include
  closure defines for braincheck compile-time constants.
* **\[dom\]** Add invariant check to the `HtmlGlobalFields.className(...)` method that verifies the class
  name elements passed to the method are not empty and do not include leading or trailing whitespace. These
  scenarios typically mean the developer should be passing multiple parameters, some of which can be null
  if they are to be omitted.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.99`.

### [v0.84](https://github.com/react4j/react4j/tree/v0.84) (2018-07-17) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.83...v0.84)

* Re-release library as a failure in the release process led to an invalid artifacts being deployed to
  Maven central.
* **\[processor\]** Update the ` org.realityforge.guiceyloops:guiceyloops:jar` dependency to version `0.95`.

### [v0.83](https://github.com/react4j/react4j/tree/v0.83) (2018-07-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.82...v0.83)

* **\[arez\]** Replace the jetbrains artifact with a J2CL compatible variant.
* **\[arez\]** Made the `ReactArezEnvironment.ReactReactionEnvironment` class final so that Arez can
  change the `ReactionEnvironment` interface into a `@JsFunction` in the future to eliminate some overhead.
* **\[processor\]** Enhance the generated subclasses of `react4j.arez.ReactArezComponent` to eliminate
  the `arez.annotations.Action` annotation on the `reportPropsChanged(...)` method if there is no props
  as the `@Action` annotation is not needed and will generate an invariant failure in Arez version `0.97+`.
* **\[processor\]** Fix a bug when overriding the `react4j.arez.ReactArezComponent.reportPropsChanged(...)`
  method in the generated component subclasses where the component has a prop named `"child"`. Changes are
  no propagated as the key was not translated to the correct key `"children"`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.98`.
* **\[arez\]** Update the `org.realityforge.arez.browserlocation` dependency to version `0.25`.
* **\[arez\]** Update the `org.realityforge.arez.spytools` dependency to version `0.23`.

### [v0.82](https://github.com/react4j/react4j/tree/v0.82) (2018-07-10) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.81...v0.82)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.96`.
* **\[arez\]** Update the `org.realityforge.arez.browserlocation` dependency to version `0.23`.
* **\[arez\]** Update the `org.realityforge.arez.spytools` dependency to version `0.20`.
* **\[core\]** Introduce JDepend based test that verifies that no unexpected dependencies between packages
  occur.
* 💥💥💥💥 **\[core\]** The `react4j-annotations` module has been merged into `react4j-core` as they are
  almost always used in combination. It was felt merging the modules simplified usage in downstream projects.
  JDepend is used to ensure that no undesired dependencies between packages are added now that the code is
  in a single module.
* 💥 **\[core\]** Move `react4j.RefConsumer` to `react4j.dom.proptypes.html.RefConsumer` and into the
  `react4j-dom` module from the `react4j-core` module.

### [v0.81](https://github.com/react4j/react4j/tree/v0.81) (2018-07-02) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.80...v0.81)

* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.11.0` for improved
  compatibility with J2CL with respect to compile-time constants.
* **\[processor\]** Change the jsinterop typing of the `Lifecycle` interface applied to native React4j
  components from `@JsType(isNative = true)` to `@JsType(isNative = true,namespace = JsPackage.GLOBAL,name = "?")`
  as that seems to be more inline with the code expected by J2CL or more precisely the closure compiler.
* **\[core\]** Move the native methods `ReactDOM.createElement(String,Props,...)` to the `React` class
  as `@JsMethod` and add `@JsOverlay` methods to `ReactDOM` that type them more specifically using types
  within the dom package. This results in a 1% size reduction under J2CL with no performance impact.
* Compile-time constants work differently between the JRE, J2CL and GWT2.x environments. Adopt an
  approach that has the same effective outcome across all environments. This involves using instance
  comparisons for results returned from `System.getProperty(...)` in GWT2.x and J2CL environments and
  using normal `equals()` method in JRE. It should be noted that for this to work correctly in the J2CL
  environment, the properties still need to defined via code such as:
  `/** @define {string} */ goog.define('react4j.environment', 'production');`

### [v0.80](https://github.com/react4j/react4j/tree/v0.80) (2018-06-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.79...v0.80)

* **\[arez\]** Stop annotating props with `@Dependency` if the prop type is annotated with
  `@ArezComponent` and the prop is on a sub-class of `react4j.arez.ReactArezComponent`. The
  `ReactArezComponent` has added an additional method `anyPropsDisposed()` that is overridden
  by the annotation processor when candidate props are present. The render method skips rendering
  if any prop is disposed.
* Update the javascript included within package to react version `16.4.1`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.95`.
* **\[arez\]** If a render is triggered on a subclass of `react4j.arez.ReactArezComponent` and the
  render has no Arez dependencies then the code will now generate an invariant failure. Extending
  a `react4j.arez.ReactArezComponent` when it is not needed is an unnecessary overhead.
* **\[core\]** Change the namespace the jsinterop typing of the `React` class from
  `@JsType( isNative = true, namespace = "React", name = "Component" )` to
  `@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "React.Component" )` as J2CL only
  considers types with a namespace of `JsPackage.GLOBAL` to be capable of being externs.

### [v0.79](https://github.com/react4j/react4j/tree/v0.79) (2018-06-24) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.78...v0.79)

* **\[dom\]** Explicitly specify the namespace and name for event types. This is required by J2CL.
* **\[dom\]** Move the javascript packaged in `react-dom` artifact from the `react4j.core.public`
  package to the `react4j.dom.public` package and updated the GWT module definition to retrieve
  resources from that package.
* **\[dom\]** Introduce the `react4j.dom.ReactDOMDev` GWT module that includes the debug source
  for the `react-dom` library. This is now needed as the dev source is no longer incidentally
  included when the `react4j.core.ReactDev` GWT module is incidentally included.
* **\[dom\]** Rename the `react4j.dom.ReactDOM` GWT module to `react4j.dom.Dom` and the
  `react4j.dom.ReactDOMDev` GWT module to `react4j.dom.DomDev` to follow existing conventions.
* **\[arez\]** Rename the GWT modules in the `react4j-arez` module to follow conventions (i.e. to
  be named the same as the package name) to follow existing conventions.
* **\[core\]** Rename the `react4j.core` package to `react4j`

### [v0.78](https://github.com/react4j/react4j/tree/v0.78) (2018-06-20) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.77...v0.78)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.93`.
* **\[arez\]** Change the priority of the `ReactArezComponent.trackRender()` method to `LOW`
  so that component re-renders are always scheduled after other higher priority Arez reactions.
  This reduces the chance that a single component will be re-rendered multiple times within
  a single Arez reaction round.

### [v0.77](https://github.com/react4j/react4j/tree/v0.77) (2018-06-18) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.76...v0.77)

* **\[gwt-output-qa\]** Upgrade the version of `gwt-symbolmap` to `0.08`.
* **\[gwt-output-qa\]** Cleanup dependency tree in `gwt-output-qa` to use transitive dependencies
  where applicable.
* **\[core\]** Add the `@JsConstructor` to the constructor of the `react4j.core.NativeComponent`
  class and to the constructors of all subclasses, including those generated by the annotation
  processor. This is to satisfy J2CL which has stricter requirements with respect to the jsinterop
  annotations.

### [v0.76](https://github.com/react4j/react4j/tree/v0.76) (2018-06-17) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.75...v0.76)

* **\[core\]** Make sure the core GWT module inherits the `react4j.annotations.Annotations` module.
  This means downstream projects need only inherit the `react4j.core.React` module or the
  `react4j.dom.ReactDOM` module and include all the dependencies required to author components.
* Update build process so that the generated poms do not include dependencies on GWT. The GWT
  dependencies are not required by react4j but are only required to GWT compile the project. This
  dependency needs to be broken for GWT3.x/j2cl support.
* Remove the unused `org.realityforge.anodoc:anodoc:jar` dependency.
* Remove the `com.google.jsinterop:base:jar` artifact with the `sources` classifier from the build as
  the main jar includes the sources required for the GWT compiler.
* Remove the `com.google.jsinterop:jsinterop-annotations:jar` artifact with the `sources` classifier
  from the build as the main jar includes the sources required for the GWT compiler.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.92`.
* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.9.0` for compatibility
  with J2CL.
* **\[arez\]** Upgrade the `org.realityforge.arez.browserlocation` dependency to version `0.18`.
* **\[arez\]** Upgrade the `org.realityforge.arez.spytools` dependency to version `0.16`.
* Replace usage of the `com.google.code.findbugs:jsr305:jar` dependency with the
  `org.realityforge.javax.annotation:javax.annotation:jar` dependency as the former includes code that
  is incompatible with J2CL compiler.
* Remove the usage of the `javax.annotation.OverridingMethodsMustInvokeSuper` annotation as the constraint
  is not enforced by code and it's presence requires usage of a library incompatible with J2CL.
* Significantly improve the generation of POMs by using transitive dependencies when applicable.

### [v0.75](https://github.com/react4j/react4j/tree/v0.75) (2018-06-13) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.74...v0.75)

* **\[processor\]** Fixed bug introduced in `0.74` by generating `deferScheduler` parameter if a
  `@Dependency` or `@Computed(keepAlive=true)` annotation is present.
* **\[processor\]** Fixed a bug where Arez component props would not have an associated schedule
  triggered despite the presence of a `@Dependency` annotation.

### [v0.74](https://github.com/react4j/react4j/tree/v0.74) (2018-06-13) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.73...v0.74)

* **\[processor\]** Enhance the processor so that it does not generate the `deferScheduler` parameter on
  the `@ArezComponent` annotation if there are no `@Autorun` methods contained within the `ReactArezComponent`.
* Fixed a bug that allowed compiled javascript artifacts to be packaged inside the published jars.

### [v0.73](https://github.com/react4j/react4j/tree/v0.73) (2018-06-08) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.72...v0.73)

* **\[dom\]** Enhanced `HtmlGlobalFields.className` so that it will filter out null className elements
  thus allowing you to adopt simpler mechanisms for defining attribute such as inline ternaries when some
  classes are optional. i.e. `new HtmlProps().className( "button", isActive() ? "active" : null )`
* **\[processor\]** Update the processor so it does not generate classes that require the
  `javax.annotation.Generated` be present on the class path. The classes will only be annotated with
  this annotation if it is present on the classpath at the time of generation and the source version
  is `8`.
* **\[processor\]** Enhance the annotation processor so that it will not attempt to process classes
  until they are completely resolved. If an React4j component contains a dependency on code with compilation
  errors or has a circular dependency with generated code then it will not able to be processed
  by the new annotation processor.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.90`.

### [v0.72](https://github.com/react4j/react4j/tree/v0.72) (2018-06-04) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.71...v0.72)

* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.88`.
* **\[arez\]** Upgrade the `org.realityforge.arez.spytools` dependency to `0.11`.

### [v0.71](https://github.com/react4j/react4j/tree/v0.71) (2018-05-31) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.70...v0.71)

* **\[dom\]** Add helper method `onWheel` to `HtmlGlobalFields` so it is available for all elements.
* **\[dom\]** Add helper method `onScroll` to `HtmlProps`, `TextAreaProps` and `SelectProps`.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.87`.

### [v0.70](https://github.com/react4j/react4j/tree/v0.70) (2018-05-20) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.69...v0.70)

#### Changed
* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.6.0` as
  previous version was incompatible with GWT 3.x.
* **\[arez\]** Update the `org.realityforge.arez` dependencies to version `0.84` for
  GWT 3.x compatibility.
* **\[arez\]** Upgrade the `org.realityforge.arez.spytools:arez-spytools:jar` dependency
  to `0.07` as previous version was incompatible with GWT 3.x.

### [v0.69](https://github.com/react4j/react4j/tree/v0.69) (2018-05-19) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.68...v0.69)

##### Added
* **\[docs\]** Add a page that references the TodoMVC examples to the website.
* **\[docs\]** Add some documentation on how to configure IntelliJ IDEA to the website.
* **\[docs\]** Add some initial "Getting Started" documentation to the website.

##### Removed
* **\[core\]** Removed `react4j.core.util.JsUtil` and inlined the one usage of
  the `JsUtil.isObjectShallowModified(...)` method.
* **\[dom\]** Removed unnecessary `com.google.gwt.useragent.UserAgent` inherit from
  `ReactDOM.gwt.xml` GWT module.
* **\[annotations\]** Removed unnecessary `com.google.gwt.core.Core` inherit from the
  `Annotations.gwt.xml` GWT module.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.83`.
* **\[processor\]** Claim the React4j annotations that are processed by the annotation processor.
  Subsequent annotation processor will not be asked to process the annotation types which results
  in a very slight performance improvement during compilation.
* **\[arez\]** Upgrade the `org.realityforge.arez.browserlocation` dependency to version `0.09`.
* **\[arez\]** Upgrade the `org.realityforge.arez.spytools` dependency to version `0.05`.

### [v0.68](https://github.com/react4j/react4j/tree/v0.68) (2018-04-24) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.67...v0.68)

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.80`.
* **\[processor\]** Change the way that the annotation processor marks generated classes as
  generated. When the source version of input code is Java 9 or greater then the
  `javax.annotation.processing.Generated` annotation is added rather than the historic
  `javax.annotation.Generated` which can be difficult to support in Java 9 due to split modules.
* **\[docs\]** Enable search on the website.
* **\[processor\]** Verify that methods annotated with `@Callback` are not abstract.
* **\[processor\]** Verify that methods annotated with `@Callback` are not static.
* **\[processor\]** Verify that methods annotated with `@Callback`, `@State` and `@Prop` are
  callable by subclasses. They must not be private and if they are package access then the methods
  must be defined in the same package as the class annotated with `@ReactComponent`
* **\[annotations\]** Document the requirements for `@Callback` annotated methods.
* **\[processor\]** Fix the error message emitted when the render method is not overridden.

### [v0.67](https://github.com/react4j/react4j/tree/v0.67) (2018-04-20) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.66...v0.67)

#### Fixed
* The react javascript packaged in `0.66` was corrupted during download from unpkg. This release
  is only a repackage of correct javascript.

### [v0.66](https://github.com/react4j/react4j/tree/v0.66) (2018-04-20) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.65...v0.66)

#### Fixed
* **\[processor\]** Fix bug in generated React classes where a `@Nullable @Prop` method could
  generate a `NullPointerException` when reading the prop if the prop was explicitly set to null.

#### Changed
* 💥 **\[core\]** Upgrade to react `16.3.2`.

### [v0.65](https://github.com/react4j/react4j/tree/v0.65) (2018-04-17) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.64...v0.65)

#### Fixed
* **\[core\]** Make sure that the native component invoked `Component.performComponentWillUnmount()`
  rather than `Component.componentWillUnmount()` to allow middleware the chance to decorate the
  lifecycle.

##### Added
* **\[processor\]** The types generated by the annotation processor are now associated with the
  annotated class and all it's supertypes as a originating elements. These are provided as hints
  to the tool infrastructure to better manage dependencies. In particular incremental compilation
  by IDEs can make use of this to trigger recompilation when necessary.

#### Removed
* **\[core\]** Removed the method `Component.isComponentBound()` as it was only valid if the component
  lifecycle method `Component.componentWillUnmount()` was specified in a component. While this was
  documented, it proved to be too confusing a feature to rely upon. The introduction of this feature
  in `v0.64` changed the behaviour of `ReactArezComponent` which meant that these components may not
  clean up arez elements and could receive callbacks after the components had been unmounted.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.79`.

### [v0.64](https://github.com/react4j/react4j/tree/v0.64) (2018-04-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.63...v0.64)

#### Fixed
* **\[arez\]** Avoid invoking `Component.scheduleRender(...)` from `ReactArezComponent.onRenderDepsChanged()`
  if the component is no longer bound. This can occur when the component has been removed from the DOM tree
  and has been scheduled for disposal but Arez has yet to complete the disposal. This scenario previously
  triggered an invariant failure in native react development library.

#### Added
* **\[core\]** Re-add the method `Component.isComponentBound()` that will return false after the
  `Component.componentWillUnmount()` lifecycle method completes. This method should be avoided unless
  an asynchronous task can invoke `Component.scheduleStateUpdate(..)` or `Component.scheduleRender(...)`
   and the asynchronous tasks can not be easily aborted when the component is unmounted.

#### Changed
* 💥 **\[arez\]** Avoid overriding `ReactArezComponent.componentWillUnmount()` and mandating that subclasses
  call the method in any overriding classes.

### [v0.63](https://github.com/react4j/react4j/tree/v0.63) (2018-04-12) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.62...v0.63)

#### Fixed
* **\[processor\]** Methods annotated with `@Callback` will have their return values validated to ensure
  that they align with the return value of the associated callback type.

#### Changed
* **\[processor\]** Remove restriction that methods annotated with `@Callback` must have types that are
  annotated with `@JsFunction`.

### [v0.62](https://github.com/react4j/react4j/tree/v0.62) (2018-04-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.61...v0.62)

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.76`.
* 💥 **\[arez\]** Replace usage of the Arez `extra` and `browser-extra` spy utilities class with the separate
  top level project `arez-spytools`.

### [v0.61](https://github.com/react4j/react4j/tree/v0.61) (2018-04-06) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.60...v0.61)

#### Fixed
* **\[processor\]** The annotation processor would generate `@Prop` accessor implementations that explicitly cast
  a null to a primitive value, when accessing a prop that was not marked as `@Nonnull`. However primitive values
  can never be annotated with `@Nonnull` and thus all primitive methods included this methods, guaranteeing a
  NullPointerException and triggering errors in several static source code analysis tools such as Findbugs.

### [v0.60](https://github.com/react4j/react4j/tree/v0.60) (2018-04-05) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.59...v0.60)

#### Changed
* Upgrade the Dagger2 support to version `2.15`.

### [v0.59](https://github.com/react4j/react4j/tree/v0.59) (2018-04-04) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.58...v0.59)

#### Fixed
* **\[processor\]** Ensure that a react component with a parameterized type will add the parameterized type
  to the builder step interfaces to avoid compilation errors if the type parameter is used by the prop methods.
* **\[processor\]** The annotation processor will generate an error if dagger is enabled and the react component
  has type parameters as dagger is incapable of injecting such components using the technique employed by React4j.

### [v0.58](https://github.com/react4j/react4j/tree/v0.58) (2018-04-03) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.57...v0.58)

##### Added
* Added support for the context API added in react `16.3.0`. See `React.createContext(...)`.
* Added support for creating the `StrictMode` component added in react `16.3.0`. See `React.createStrictMode(...)`.

#### Changed
* 💥 **\[core\]** Remove support for the `componentWillReceiveProps` lifecycle method as it is being deprecated in
  React 16.3 and removed in React 17.x.
* 💥 **\[core\]** Upgrade to react `16.3.0`.

### [v0.57](https://github.com/react4j/react4j/tree/v0.57) (2018-03-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.56...v0.57)

#### Fixed
* **\[processor\]** A component that has a single optional `@Prop` annotated method will terminate the build after
  the builder method for the optional prop is invoked.

### [v0.56](https://github.com/react4j/react4j/tree/v0.56) (2018-03-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.55...v0.56)

#### Changed
* **\[processor\]** Update the processor so that `@Prop` annotated methods that are marked as `@Nullable`
  with a `required` parameter set to `AUTODETECT` will be detected as an optional prop.

### [v0.55](https://github.com/react4j/react4j/tree/v0.55) (2018-03-27) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.54...v0.55)

#### Changed
* 💥 **\[widget\]** The `react-widget` artifact has been removed from the `react4j` project and migrated
  to it's own top level repository [react4j/react4j-widget](https://github.com/react4j/react4j-widget).
* 💥 **\[extras\]** The `react-extras` artifact has been removed from the `react4j` project and the single
  component has been migrated to it's own top level repository [react4j/react4j-windowportal](https://github.com/react4j/react4j-windowportal).

### [v0.54](https://github.com/react4j/react4j/tree/v0.54) (2018-03-25) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.53...v0.54)

##### Fixed
* **\[arez\]** Correct the dependencies as declared in pom for `react4j-arez` artifact.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.73`.
* Upgrade `org.realityforge.braincheck:braincheck:jar` artifact to `1.5.0` which removes the gwt classifier.

### [v0.53](https://github.com/react4j/react4j/tree/v0.53) (2018-03-23) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.52...v0.53)

##### Fixed
* **\[processor\]** Remove all dependencies declared in the pom for the `react4j-processor` artifact so
  that tools that inspect the pom do not incorrectly try to add the dependencies to the classpath. All
  required dependencies have been shaded in.
* **\[core\]** Fixed the pom generated for the `react4j-core` artifact to included the classifiers of
  dependencies so that the tools that inspect the pom include the correct artifacts when generating the
  classpath.

#### Changed
* **\[core\]** Annotate `performPostConstruct` method in `react4j.core.Component` with
  `@OverridingMethodsMustInvokeSuper` so that findbugs can verify that subclasses correctly invoke super
  class method if they override it.
* **\[arez\]** Annotate several methods in `react4j.arez.ReactArezComponent` with
  `@OverridingMethodsMustInvokeSuper` so that findbugs can verify that subclasses correctly invoke super
  class methods if they override them.
* **\[core\]** Add the `react-annotations` as a pom dependency so that downstream applications need only
  include `react-core` dependency and be supplied the `react-annotations` on the classpath. The
  `react-annotations` is not added as a normal compile dependency as no code within this package actually
  makes use of the artifact or should make use of the package.
* Change the generated poms so that transitive dependencies of intra-project modules are not duplicated
  as dependencies within each modules dependencies list and instead intra-project dependencies include
  dependencies transitively.
* **\[dom\]** Remove unused dependency on `gwt-user` artifact.

### [v0.52](https://github.com/react4j/react4j/tree/v0.52) (2018-03-18) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.51...v0.52)

##### Fixed
* **\[core\]** Fixed bug where `ReactConfig` was incorrectly accessing the setting `arez.enable_component_names`
 rather than correct `react4j.enable_component_names`.
* Fixed a bug where sources generated by the annotation processor may not be included in output jars.
* Avoid building Intellij IDEA projects with `generated/processors/main/java` on the class path unless the
  module is expected to use annotation processors.
* Fix the build process so that it is no longer possible to build jars where the java source is not present
  in the gwt enhanced artifacts.

##### Added
* Added test infrastructure that builds downstream `todomvc` application with the updated version of `react4j`,
  compares the build sizes and then pushes the updates during release process.

#### Changed
* **\[docs\]** Updated favicon so that transparent background is supported.
* **\[docs\]** Updated the color scheme of the logo and website to a green based color scheme to distinguish
  it from Arez website on which the original site was based.
* **\[core\]** Introduce a `react4j.check_invariants` compile time setting to control whether invariant
  checking is enabled in react4j. Expose this setting via `ReactConfig.shouldCheckInvariants()`.
* **\[core\]** Added guards around the `invariant(...)` calls to make it easier for the GWT compiler to optimize
  out unused lambda expressions.
* **\[processor\]** Enhance the generator so that `invariant(...)` calls in the generated code are guarded
  to make it easier for the GWT compiler to eliminate unused lambda expressions.

### [v0.51](https://github.com/react4j/react4j/tree/v0.51) (2018-03-06) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.50...v0.51)

##### Fixed
* **\[processor\]** Changed the way that `@Prop` annotated methods are generated so that the GWT compiler
  does not assume that all values are non-null and thus eliminate null checks from production code.

##### Added
* **\[core\]** Add the `React.isValidELement(ReactNode)` method that exposes the underlying ReactJS method.

##### Removed
* **\[core\]** Remove the `ReactElement` class as it no longer adds any value.

### [v0.50](https://github.com/react4j/react4j/tree/v0.50) (2018-03-05) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.49...v0.50)

##### Added
* **\[core\]** Add support for the `React.Fragment` node type introduced in React `v16.2.0` as described in
  the [fragments](https://reactjs.org/blog/2017/11/28/react-v16.2.0-fragment-support.html) section of the
  ReactJS release nodes. This has been implemented by adding several overlay `React.createFragment(...)` methods.

#### Changed
* **\[core\]** Convert the `React.createElement(...,JsArray)` method from a native method to an overlay method
  to reduce size of generated code and add opportunities for optimization within the GWT compiler.
* **\[core\]** Change the `React.createElement(...,ReactNode[])` method to a varargs method.

### [v0.49](https://github.com/react4j/react4j/tree/v0.49) (2018-02-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.48...v0.49)

##### Added
* **\[arez\]** Introduce the `ReactArezEnvironment` utility class to aid install an Arez `ReactionEnvironment`
  that batches all updates from Arez to React.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.62`.

### [v0.48](https://github.com/react4j/react4j/tree/v0.48) (2018-02-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.47...v0.48)

##### Fixed
* **\[arez\]** Avoid invoking logic within `ReactArezComponent.storeArezDataAsState()` if the component has been
  disposed. This is possible when the component triggers it's own disposal from one of the relevant lifecycle
  callback methods.
* **\[arez\]** Gracefully handle scenario where a subclass of `ReactArezComponent` has been disposed but react
  has requested that the component render. This can occur if the code explicitly disposes the component or a
  `@Dependency` annotation has been used.

##### Added
* **\[dom\]** Expose the unstable `batchedUpdates` API as `ReactDOM.batchedUpdates(Procedure)` as it is useful
  to batch changes that are coming in from a network endpoint.
* **\[processor\]** In components extending `ReactArezComponent`, detect whether the return type of a `@Prop`
  annotated method is a Arez component and if it is then annotate it with `arez.annotations.Dependency`. This
  has the side-effect that if a component has a prop that is a disposed Arez component, then the component will
  be disposed.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.61`.

### [v0.47](https://github.com/react4j/react4j/tree/v0.47) (2018-02-21) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.46...v0.47)

##### Fixed
* **\[core\]** Passed the `onStateUpdateComplete` callback parameter of the `Component.setState(...)` methods
  to the native javascript `setState(...)` method to ensure that the react runtime invoked the callback at the
  correct time.

##### Added
* **\[annotations\]** Introduced the `@State` annotation that can be used on abstract getter/setter method
  pairs on instances of `ReactComponent`. The annotation processor will implement the getter as an access from
  reacts state value and a scheduled update of reacts state value via `scheduleStateUpdate()`. In subclasses of
  `ReactArezComponent`, the `@State` annotated method is made observable. This resulted in the simplification
  of `react.core.Component` to remove the remaining generics and the removal of the `BaseState` class.

#### Changed
* 💥 **\[core\]** Default the `react4j.check_component_state_invariants` configuration to `true` in development build.
* 💥 **\[core\]** Annotate the `react4j.core.Component.SetStateCallback` interface with the `@FunctionalInterface`
  annotation.

### [v0.46](https://github.com/react4j/react4j/tree/v0.46) (2018-02-15) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.45...v0.46)

#### Changed
* 💥 **\[core\]** Remove support for the `componentWillUpdate` lifecycle method as ReactJS is deprecating
  the method in `16.3` and plans to remove it in `17.x`.
* 💥 **\[annotations\]** Rename `@EventHandler` to `@Callback`.
* 💥 **\[annotations\]** Added the `initCallbackContext` parameter to the `@Callback` annotation. This determines
  whether the annotation processor attempts to generate code that initializes the calling context of the callback
  or leaves that responsibility to the invoker. In practical terms, subclasses of the `ReactArezComponent` class
  will have callbacks annotated with an `@arez.annotations.Action` if the parameter is set to `ENABLED` or
  `AUTODETECT`. This resulted in the removal of the `@react4j.arez.NoAutoAction` annotation as it no longer
  provides any additional functionality.
* 💥 **\[processor\]** Separate the enhanced component class `[MyComponent]_` into two classes. Move the
  implementation artifacts to a class named `React4j_[MyComponent]` but leave the code that is accessed by
  a developer in the `[MyComponent]_` helper class. The helper class only contains the callback helper static
  methods and if there is no `@Callback` methods defined on the component the helper class will not be generated.
  The developer should not access `React4j_[MyComponent]` as internal implementation details are not guaranteed
  to remain consistent over time.

### [v0.45](https://github.com/react4j/react4j/tree/v0.45) (2018-02-14) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.44...v0.45)

#### Fixed
* **\[processor\]** Fixed potential null pointer exception in the `reportPropsChanged` method generated
  by the annotation processor for subclasses of `ReactArezComponent` when `nextProps` parameter is `null`
  or `undefined`.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.52`.

### [v0.44](https://github.com/react4j/react4j/tree/v0.44) (2018-02-14) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.43...v0.44)

##### Fixed
* **\[processor\]** Fix a bug where a `@Prop` named child would incorrectly use the key `child` rather
  than `children` to access the underlying prop value.
* **\[processor\]** Add the `@Generated` annotation onto the generated dagger factory and component builder
  so that tooling and developers can easily identify which code is generated.
* **\[processor\]** Detected usage of names in `@Prop`, `@EventHandler` and `@ReactComponent` to ensure
  that no java keyword is used as this would cause a compilation error when generating the builder.
* **\[processor\]** Fixed processing of the `@PropDefault` annotation on fields so it is able to derive
  default the name if the prop name has characters other than letters.

#### Changed
* 💥 **\[core\]** Remove the methods on `ReactNode` that are responsible for querying the node type and
  converting the `ReactNode` to other types. The method removed include: `asString()`, `asReactElement()`
  `isString()`, `isReactElement()` and `isRenderResults()`. These methods were removed as they are not
  likely to be used within application code.
* 💥 **\[core\]** Remove the `ReactNode.of(...)` methods that accept `ReactElement` parameters as they
  are no longer useful now that `ReactElement` implements `ReactNode`.
* 💥 **\[core\]** Changed the casts in the `ReactNode.of(...)` methods to use `Js.uncheckedCast(...)`.
* **\[processor\]** Used code supplied by the JVM to detect whether a name is a valid java identifier and
  removed custom code to detect whether name is a java identifier. Enhanced the exceptions to give greater
  context to why a name is invalid.

### [v0.43](https://github.com/react4j/react4j/tree/v0.43) (2018-02-14) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.42...v0.43)

##### Fixed
* **\[processor\]** Fix the `reportPropsChanged()` method override that is generated by the annotation
  processor for subclasses of `ReactArezComponent` to ensure that the method is wrapped in an Arez transaction
  by annotating the method with `@Action`.
* **\[processor\]** Fix the generation of the `@Prop` annotated method with the name `children` so that children
  are correctly passed as the third parameter to `React.createElement()` rather than treating them as a prop
  with the name `"children"`.
* **\[processor\]** Fix the generation of the `@Prop` annotated method with the name `child` so that the child
  is correctly passed as the third parameter to `React.createElement()` rather than treating them as a prop
  with the name `"child"`.

##### Added
* **\[core\]** Define an additional `React.createElement(...,JsArray<ReactNode>)` native method that
  accepts a `JsArray<ReactNode>` parameter for children. This method eliminates the need for unchecked casts
  when passing javascript arrays.
* **\[core\]** Define an additional `React.createElement(...,ReactNode[])` native method that
  accepts an array parameter for children. This method eliminates the need for unchecked casts when
  passing java arrays.
* **\[processor\]** The children `@Prop` will now have non-terminal `'child(...)` methods generated by builder
  so that children can be incrementally appended.
* **\[processor\]** The children `@Prop` will now generate a method on the builder that accepts a
  `Stream<? extends ReactNode>` parameter. The goal is to simplify the developer experience when integrating
  with Java stream API.

#### Changed
* 💥 **\[processor\]** Explicitly checked that no `@Prop` is named `build` as that would result in a compilation
  error when the annotation processor generated the builder.
* **\[processor\]** The `@Prop` named `children` is generated as a varargs method rather than as a method
  that accepts an array to simplify developer experience of generated builders.

### [v0.42](https://github.com/react4j/react4j/tree/v0.42) (2018-02-12) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.41...v0.42)

##### Added
* **\[processor\]** The annotation processor now generates `[MyComponent]Builder` class that can be used to
  build the `ReactNode` instance for a component. The builder is constructed such that it mandates that the
  developer supply all required props, may supply optional props and must complete the build of the component
  with the `children` prop if present.

#### Changed
* Upgrade the `org.realityforge.braincheck:braincheck` dependency to `1.4.0`.
* 💥 **\[arez\]** Upgrade Arez to version `0.51`.

### [v0.41](https://github.com/react4j/react4j/tree/v0.41) (2018-02-11) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.40...v0.41)

##### Fixed
* **\[annotations\]** Add the `require` parameter to the `@Prop` annotation to specify whether the developer
  is expected to supply the prop when constructing the component.

### [v0.40](https://github.com/react4j/react4j/tree/v0.40) (2018-02-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.39...v0.40)

##### Fixed
* **\[processor\]** Fixed handling of documented annotations, in particular nullability annotations such as
  `javax.annotations.Nonnull` and `javax.annotations.Nullable` for `@Prop` annotated methods. The annotations
  are copied to the appropriate methods in the generated builder and onto the implemented prop methods in the
  enhanced component subclass.

#### Changed
* **\[extras\]** Prefer the `@Props` annotation strategy for getting props for `WindowPortal` component.
* 💥 **\[core\]** Remove the `BaseProps` class and all the associated infrastructure. This involved removing
  the type `P` type variable from the `Component` class and associated infrastructure. Where the framework
  needs to refer to props as a generic object, replace the type variable `P extends BaseProps` with the type
  `JsPropertyMap<Object>`.
* 💥 **\[annotations\]** Introduced the `@PropDefault` annotation that can be placed on static final fields
  or static getters that will specify or return the default value for a specific prop. This replaces that
  static magically named method `getInitialProps()` that returned the initial props for the entire component.
  This approach is more explicit and allows better type analysis when constructing builders etc.

#### Removed
* 💥 **\[core\]** Removed the `BaseContext` and `BaseChildContext` types and all associated infrastructure.
  ReactJS is moving to deprecate context in the next point `16.3` release and replace it with new, improved
  context API. Rather than supporting the older API that introduces a significant amount of complexity to the
  codebase, React4j has decided to move directly to the new API.

### [v0.39](https://github.com/react4j/react4j/tree/v0.39) (2018-02-07) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.38...v0.39)

##### Fixed
* **\[processor\]** Update the annotation processor to support generic react components.
* **\[processor\]** Fixed react components that are defined as nested classes when the react component uses
  the `@EventHandler` or is expected to generate a `*DaggerFactory` class.

##### Added
* **\[annotations\]** Introduced the `@Prop` annotation that can be used on abstract getter methods on
  instances of `ReactComponent`. The annotation processor will implement the getter as an access from
  reacts props value. In subclasses of `ReactArezComponent`, the `@Prop` annotated method is made observable.
* **\[core\]** Expose the special `key` prop on the component class via `Component.getKey()`.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.49`.
* **\[processor\]** Changed the `NativeReactComponent` class nested in the enhanced component class to private
  access. This eliminates the possibility that it will be accidentally referenced outside of the enhanced component.
* 💥 **\[core\]** The `Component.props()` method has been made final as there is no need for overriding in subclasses.
* **\[core\]** The `Component.render()` method has been made abstract. This makes it clearer to user that this method
  needs to be overridden.

#### Removed
* 💥 **\[arez\]** The `ReactArezComponent.props()` method is no longer observable. Instead it is expected that
  individual `@Prop` annotated methods are defined and these methods are observable.
* 💥 **\[processor\]** Removed the `_create(*)` methods on the generated enhanced component class as no longer
  required now that builders are synthesized.

### [v0.38](https://github.com/react4j/react4j/tree/v0.38) (2018-01-31) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.37...v0.38)

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.47`.

### [v0.37](https://github.com/react4j/react4j/tree/v0.37) (2018-01-26) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.36...v0.37)

##### Fixed
* **\[docs\]** Fixed documentation describing the dependency injection `Feature` enum values.

#### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.44`.
* 💥 **\[processor\]** React4j components must be abstract and must not be concrete. This is the inverse of the
  requirements in version `v0.36` and earlier. The reason is that it never makes sense to instantiate the React4j
  components directly as it is expected that only the enhanced subclass is instantiated. This change was also
  mandated by the move to a later version of Arez that requires certain methods to be abstract and thus required
  that Arez components were at least able to be abstract.
* 💥 **\[core\]** Move `react4j.annotations.ReactComponent.Feature` from being a static inner class to top-level
  class `react4j.annotations.Feature`.
* 💥 **\[core\]** The `componentDidConstruct()` and `componentWillMount()` lifecycle methods on
  `react4j.core.Component` have been merged into a `postConstruct()` lifecycle method that exists purely within
  the java component model without ever being invoked from the native react framework.

### [v0.36](https://github.com/react4j/react4j/tree/v0.36) (2018-01-25) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.35...v0.36)

#### Changed
* Move from [Jekyll](https://jekyllrb.com/) to [Docusaurus](https://docusaurus.io/) to generate website.
  The motivation was the better documentation styling and layout offered by Docusaurus.
* 💥 **\[arez\]** Upgrade Arez to version `0.43`.

### [v0.35](https://github.com/react4j/react4j/tree/v0.35) (2018-01-11) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.34...v0.35)

##### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.42`.

### [v0.34](https://github.com/react4j/react4j/tree/v0.34) (2018-01-10) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.33...v0.34)

##### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.40`.
* 💥 **\[annotations\]** Rename the `ReactComponent.Feature` constants to improve clarity;
  - `TRUE` renamed to `ENABLE`
  - `FALSE` renamed to `DISABLE`
  - `IF_DETECTED` renamed to `AUTODETECT`
* 💥 **\[arez\]** The contents of the `arez` state value in `ReactArezComponent` components has
  changed to a frozen map between the Arez node name and the value returned by the Arez spy
  property introspector. This improves the ability to introspect Arez components until an
  appropriate DevTool is put in place.

### [v0.33](https://github.com/react4j/react4j/tree/v0.33) (2018-01-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.32...v0.33)

##### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.38`.

### [v0.32](https://github.com/react4j/react4j/tree/v0.32) (2018-01-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.31...v0.32)

##### Fixed
* **\[arez\]** Add an inherit for the `org.realityforge.arez.annotations.Annotations` GWT module to the
  `react4j.arez.ReactArez` GWT module to ensure that the `org.realityforge.arez.annotations.Injectible`
  enum has source available during the GWT compile.

### [v0.31](https://github.com/react4j/react4j/tree/v0.31) (2018-01-02) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.30...v0.31)

##### Changed
* 💥 **\[dom\]** Re-add `DOM.*()` factory methods that accepted a `java.util.List` parameter using a mechanism
  that is compatible with J2CL/GWT3.

### [v0.30](https://github.com/react4j/react4j/tree/v0.30) (2018-01-02) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.29...v0.30)

##### Fixed
* 💥 **\[processor\]** Fix the generated class so that they generate an invariant failure if you attempt
  to instantiate a React4j component that requires dependency injection without correctly configuring the
  dependency injection provider. This means that during development mode a meaningful error message will
  be generated but the invariant check will be compiled out during production compiles.

##### Changed
* 💥 **\[dom\]** Changed `DOM.*()` factory methods that accepted a `java.util.List` to accept a
  `java.util.stream.Stream` instead. The aim was to discourage the creation of intermediate `java.util.List`
  instances when not needed. The change also removes the last direct JSNI dependency that was blocking
  a move to J2CL/GWT3.
* 💥 **\[core\]** Remove `JsUtil.asJsArray()` as it is no longer used from within the React4j library.
* 💥 **\[core\]** Revert the change in `v0.21`  that modified the `Component.componentDidConstruct()` lifecycle
  method to accept two parameters `props` and `context` and instead just use the helper methods.

### [v0.29](https://github.com/react4j/react4j/tree/v0.29) (2017-12-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.28...v0.29)

##### Fixed
* 💥 **\[arez\]** Ensure that the generated subclasses of `ReactArezComponent` set the
  `deferSchedule` parameter on the `@ArezComponent`. This fixes bug where `@Autorun` annotated
  methods are running prior to injection completing.

##### Changed
* **\[docs\]** Use relative paths where possible for release posts on website.
* 💥 **\[arez\]** Stop subclasses of `ReactArezComponent` from using component state and instead fail
  with an invariant that suggests using observable Arez state.

### [v0.28](https://github.com/react4j/react4j/tree/v0.28) (2017-12-19) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.27...v0.28)

##### Changed
* 💥 **\[core\]** Upgrade `com.google.jsinterop:base` library to version `1.0.0-RC1`.
* 💥 **\[core\]** Upgrade `com.google.elemental2:*` libraries to version `1.0.0-RC1`.
* 💥 **\[arez\]** Upgrade Arez to version `0.37`.

### [v0.27](https://github.com/react4j/react4j/tree/v0.27) (2017-12-18) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.26...v0.27)

##### Added
* **\[docs\]** Add initial documentation about integrating React4j components into dependency injection
  frameworks such as [Dagger2](https://google.github.io/dagger).
* **\[docs\]** Add expected "Code of Conduct" documentation.

##### Changed
* 💥 **\[core\]** Changed the name of the nested type within dagger specific injection factory from
  `MyComponentDaggerFactory.DaggerComponent` to `MyComponentDaggerFactory.DaggerSubcomponent`. This
  is so that the name of class reflects the behaviour of the class.
* Change the publish task so that it only publishes tag as an artifact if the tag is on the master branch.

### [v0.26](https://github.com/react4j/react4j/tree/v0.26) (2017-12-13) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.25...v0.26)

##### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.36`.
* 💥 **\[dom\]** Change type of the `value` parameter passed to `HtmlGlobalFields.props()` to
  `jsinterop.base.Any` to avoid binding to an GWT2 implementation dependent annotation.

### [v0.25](https://github.com/react4j/react4j/tree/v0.25) (2017-12-12) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.24...v0.25)

##### Changed
* 💥 **\[arez\]** Upgrade Arez to version `0.35`.

### [v0.24](https://github.com/react4j/react4j/tree/v0.24) (2017-12-11) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.23...v0.24)

##### Fixed
* **\[core\]** Fixed bug where `ReactArezComponent.scheduleStateUpdate()` could result in a call to
  `Component.forceUpdate()` within the context of state updater. This would trigger an invariant failure
  within ReactJs code.

##### Changed
* 💥 **\[core\]** Add a parameter `onStateUpdateComplete` to `Component.scheduleStateUpdate(...)` that is
  invoked by the react runtime after the state update has completed. Add additional overloaded variants of
  `Component.scheduleStateUpdate(...)` where the call back is not present. This had the impact of changing
  the variant without the `onStateUpdateComplete` parameter to be a final method invocation.
* 💥 **\[core\]** Remove the remaining `Component.renderAs*` methods as they do not provide significant benefit
  relative to cost now that the `ReactNode` class has been introduced.

### [v0.23](https://github.com/react4j/react4j/tree/v0.23) (2017-12-10) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.22...v0.23)

##### Changed
* **\[core\]** Remove unused methods `JsUtil.asList(..)` and `JsUtil.getPrototypeForClass(..)`.
* **\[annotations\]** Add parameter `inject` to the `@ReactComponent` annotation that explicitly controls
  whether the enhanced component is generated with an `@Inject` annotation on the constructor. The default
  value for this parameter is `IF_DETECTED` and will generate a constructor using the existing algorithm.
  i.e. An `@Inject` annotated cosntructor will be generated if a field of method of the component is annotated
  with `@Inject`.
* **\[annotations\]** Add parameter `dagger` to the `@ReactComponent` annotation that explicitly controls
  whether a dagger component is created for the react component. The default value for this parameter is
  `IF_DETECTED` and will generate a constructor using the existing algorithm. i.e. A module is created if the
  dagger classes are on the compile classpath.
* 💥 **\[core\]** Upgrade `com.google.jsinterop:jsinterop-annotations` library to version `1.0.2`.
* 💥 **\[core\]** Upgrade `com.google.jsinterop:base` library to version `1.0.0-beta-3`.
* 💥 **\[core\]** Upgrade `com.google.elemental2:*` libraries to version `1.0.0-beta-3`.
* 💥 **\[core\]** Upgrade Arez library to version `0.34`.
* 💥 **\[arez\]** Skip the `shouldComponentUpdate()` lifecycle method when Arez re-schedules a render action
  for subclasses of `ReactArezComponent`.

### [v0.22](https://github.com/react4j/react4j/tree/v0.22) (2017-12-05) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.21...v0.22)

##### Changed
* Incorporate the processorpath buildr addon to simplify build configuration.
* **\[processor\]** Initial support for integration into Dagger2 by generating `@Subcomponent` and `@Module`
  annotated classes in a class named `[MyComponent]DaggerFactory`.
* **\[processor\]** Update the enhanced component subclass use a static field of type `javax.inject.Provider`
  to instantiate components.

### [v0.21](https://github.com/react4j/react4j/tree/v0.21) (2017-12-04) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.20...v0.21)

##### Changed
* **\[core\]** Upgrade to react 16.2.0.
* 💥 **\[core\]** Remove `Component.isComponentBound()` method as usage is an indication that there is a bug or
  failure in the API that should be removed.
* 💥 **\[core\]** Fix the nullability annotations on parameters to the `NativeAdapterComponent` constructor and
  cascade the fix to subclasses generated by the annotation processor.
* **\[core\]** Fix javadocs errors in the `NativeAdapterComponent` class.
* 💥 **\[core\]** Change the `Component.componentDidConstruct()` lifecycle method to accept two parameters
  `props` and `context`.
* 💥 **\[core\]** Remove `NativeComponent.setState(State)` method as `NativeComponent.setState(SetStateCallback)`
  is a better alternative.
* **\[arez\]** Upgrade Arez to version 0.33.
* **\[arez\]** Improve the javadocs on the `ReactArezComponent` class.
* 💥 **\[arez\]** Change `props()` and `state()` methods in the `ReactArezComponent` class to be observable
  properties. This reverts an earlier change made in v0.07 as it was found that a lot of downstream code made
  the assumption that they are observable.

### [v0.20](https://github.com/react4j/react4j/tree/v0.20) (2017-12-01) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.19...v0.20)

##### Fixed
* **\[arez\]** Enhance `ReactArezComponent` so that autoruns are not scheduled when the component is created but are
  instead scheduled after the `componentDidConstruct` lifecycle method was invoked. This ensures that `@Autorun`
  methods are not invoked when the component is invalid (i.e. when the native component has not been attached or
  when the `componentDidConstruct` method has yet to complete.)

##### Changed
* **\[arez\]** Upgrade Arez to version 0.32.
* Upgrade Buildr to version 1.5.4.
* **\[processor\]** Detect the presence of the `@Inject` annotation on fields or methods of classes annotated with
  `@ReactComponent`. If detected then ensure that the enhanced component either; sets the `inject = true` parameter
  on the `@ArezComponent` annotation for arez components or defines a default constructor with an `@Inject` annotation
  for non-arez components. This is the first step to enabling integration of React4j components with injection
  frameworks such as Dagger2 or Google GIN.

### [v0.19](https://github.com/react4j/react4j/tree/v0.19) (2017-11-29) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.18...v0.19)

##### Changed
* **\[processor\]** Remove a few more direct dependencies on the `javax.annotation.Nonnull` annotation
  from the `react4j-processor` artifact.

### [v0.18](https://github.com/react4j/react4j/tree/v0.18) (2017-11-29) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.17...v0.18)

##### Changed
* Upgrade Arez to version 0.30.
* **\[processor\]** Remove the direct dependency on the `javax.annotation.Nonnull` and
  `javax.annotation.Nullable` annotations from the `react4j-processor` artifact.
* **\[processor\]** Shade the processor dependencies so that the only jar required during annotation processing
  is the annotation processor jar. This eliminates the possibility of processorpath conflicts causing issues in
  the future.

### [v0.17](https://github.com/react4j/react4j/tree/v0.17) (2017-11-28) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.16...v0.17)

##### Changed
* 💥 **\[processor\]** Changed the naming convention of the classes generated from nested static classes.
  Previously the name components were separated by a `$` but this is the same convention that is used by
  nested classes and thus a nested class could have aname collision with a generated class. Instead the
  `_` character has been used to separate name components.
* **\[processor\]** Remove direct dependency on `elemental.core` package from `processor` artifact.
* **\[processor\]** Remove direct dependency on `jsinterop.*` packages from `processor` artifact.
* **\[processor\]** Remove direct dependency on `react4j.annotations` and `react4j.core` packages
  from `processor` artifact.
* **\[processor\]** Upgrade the version of javapoet to 1.8.0.
* Upgrade Arez to version 0.27.

### [v0.16](https://github.com/react4j/react4j/tree/v0.16) (2017-11-14) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.15...v0.16)

##### Changed
* 💥 Move react4j from `https://github.com/realityforge/react4j` to it's own organization `https://github.com/react4j`.

### [v0.15](https://github.com/react4j/react4j/tree/v0.15) (2017-11-14) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.14...v0.15)

##### Added
* **\[processor\]** Initial support added for React's `"defaultProps"` added. The component defines a static,
  non-private methods named `getInitialProps()` on the component class. This method is invoked when the component
  constructor is created and the results are defined as the default properties.

##### Fixed
* Make sure the `NativeComponent.render()` method is public otherwise the GWT compiler get's confused and
  generates self-recursive method for `render()`.

### [v0.14](https://github.com/react4j/react4j/tree/v0.14) (2017-11-13) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.13...v0.14)

##### Fixed
* Make sure the `react4j-extras` module is correctly defined and can be released to Maven Central.

##### Changed
* **\[dom\]** Refactor the props methods that accept or return event handlers allow null event handlers.
* 💥 **\[dom\]** Replace `DOMElement` with `ReactNode` and remove `DOMElement`.
* 💥 **\[dom\]** Add `HtlmGlobalFields.prop()` fluent method to simplify adding arbitrary props.
* 💥 **\[dom\]** Remove `react4j.dom.AbstractBuilder` until the idea is more fully developed.
* 💥 **\[dom\]** Remove unused type parameters from `ReactPortal`.

### [v0.13](https://github.com/react4j/react4j/tree/v0.13) (2017-11-13) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.12...v0.13)

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

### [v0.12](https://github.com/react4j/react4j/tree/v0.12) (2017-11-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.11...v0.12)

##### Changed
* Upgrade to Arez 0.25.

### [v0.11](https://github.com/react4j/react4j/tree/v0.11) (2017-11-03) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.10...v0.11)

##### Fixed
* Fixed bug in `DragEvent` where field name was `clipboardData` rather than `dataTransfer`.

##### Changed
* Upgrade to Arez 0.24.

### [v0.10](https://github.com/react4j/react4j/tree/v0.10) (2017-11-01) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.09...v0.10)

##### Changed
* 💥 Upgrade to Arez 0.23.

### [v0.09](https://github.com/react4j/react4j/tree/v0.09) (2017-10-30) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.08...v0.09)

##### Fixed
* Fixed bug introduced when `ReactDOM.createElement(String,Props)` variant was converted from native method
  to an overlay method.

### [v0.08](https://github.com/react4j/react4j/tree/v0.08) (2017-10-29) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.07...v0.08)

##### Changed
* Upgrade to Arez 0.22.
* Use Arez's `@ContextRef` annotation to get access to `ArezContext` rather than relying on custom code
  in `ReactArezComponent`.
* Use Arez's `@ObserverRef` annotation to get access to the `Observer` for render rather than relying on
  custom code in `ReactArezComponent`.
* 💥 Changed the access modifier on the `TYPE` field in the enhanced component class from public to private.
  Creation of `ReactElement` instances is now done by invoking the factory methods named `_create` that
  have been added to the enhanced component class.

### [v0.07](https://github.com/react4j/react4j/tree/v0.07) (2017-10-27) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.06...v0.07)

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

### [v0.06](https://github.com/react4j/react4j/tree/v0.06) (2017-10-23) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.05...v0.06)

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

### [v0.05](https://github.com/react4j/react4j/tree/v0.05) (2017-10-19) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.04...v0.05)

##### Added
* Added `RenderResult` union type that abstracts over the results of rendering from a component. It is designed
  to support the types supported by React16, namely `ReactElement`, `String` and arrays of values to render.

##### Changed
* **\[core\]** Change the return type of `Component.render()` method to `RenderResult` and add a default
  implementation that returns `null`. Also provide several other render method variants as part of the base
  component implementation `Component.renderAsString()`, `Component.renderAsElement()`, `Component.renderAsJsArray()`
  and `Component.renderAsArray()`. The developer is expected to override a single render method variant and
  the annotation processor will ensure the enhanced class uses that render method variant to render the component.

### [v0.04](https://github.com/react4j/react4j/tree/v0.04) (2017-10-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.03...v0.04)

##### Changed
* **\[core\]** 💥 Replace `Component.setState(...)` with `Component.scheduleStateUpdate(...)` so that it
  accurately reflects intent.
* Link Javadocs with Arez, GWT and JRE javadocs so as to improve the experience of looking up docs.
* Complete the automation of releasing to Maven central.

### [v0.03](https://github.com/react4j/react4j/tree/v0.03) (2017-10-16) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.02...v0.03)

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

### [v0.02](https://github.com/react4j/react4j/tree/v0.02) (2017-10-09) · [Full Changelog](https://github.com/react4j/react4j/compare/v0.01...v0.02)

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
* ✨ Added `react4j.arez.spy.ArezSpyUtil` to simplify the setup of Arez spy console logger during
  development and debugging.
* ✨ Added `react4j.widget.ReactWidget` class that simplifies integration with applications still using
  GWT Widgets.

##### Changed
* 💥 Updated `ArezComponent` so subclasses implement the `render()` method rather than `doRender()` to make it
  consistent with the way components that do not extend `ArezComponent` are written.
* 💥 Removed `NativeComponent` from the public API supported by the library by making it package access. It is
  an implementation detail that should not be relevant to downstream consumers.
* 💥 Renamed `componentInitialize()` to `componentDidConstruct()` to follow the naming conventions baked into
  the react component model.
* 💥 Stopped shipping artifacts with a classifier of `gwt` as there is no consumer of the library that is not
  gwt based. This essentially entails shipping the `.gwt.xml` and source files inside the main jar artifact.

### [v0.01](https://github.com/react4j/react4j/tree/v0.01) (2017-10-06) · [Full Changelog](https://github.com/react4j/react4j/compare/934ed5a707bfdab7959e9af5a793575a42a780ff...v0.01)

 ‎🎉	Initial super-alpha release ‎🎉.
