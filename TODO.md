## TODO

### Very High Priority

* Consider auto-observing all props at start of render - skipping those that will be observed in anyPropsDisposed().
  Perhaps we could skip this if there are no `@Computed` (which would normally be invoked later as lower priority)

* If a `ReactArezComponent` does not override `componentDidMount()` and `ReactArezConfig.shouldStoreArezDataAsState()`
  returns `false` we will still generate `Lifecycle.componentDidMount()` and thus the method is not eliminated in native
  code (it is just empty). We should instead consider writing `LiteLifecycle` and `Lifecycle extends LiteLifecycle`
  as well as `LiteNativeReactComponent` and `NativeReactComponent extends LiteNativeReactComponent`. Then the
  `getConstructorFunction()` would select the appropriate `NativeReactComponent` based on compile time setting.
  The same could be said for `componentDidUpdate`...

* Add helper to autoload js assets

* Make the name of the assets based off the version of the underlying react library. i.e. Name them `react-16.5.0.js`
  rather than `react.js` so cache is never in conflict.

* Consider whether can turn off remove `@Callback` annotation altogether.
- It seems we probably want to turn off `@Action` wrapping for `@Callback`? Unless (see next point)
- EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff autorun that will be re-run when scheduler is enabled. (The autorun will need to dispose itself
  and will need to be marked as runImmediately=false)
- if we support disabling change checking on props then do we need callbacks anymore?

* Should we mark `@JsFunction`/`@FunctionalInterface` props that are not via a callback. They can be tracked as
  coming from a callback by magic prop put on function. Does that mean we should track `@FunctionalInterface` props
  that are not `@JsFunction` instances?

* Start to add javascript tests - starting with braincheck ala
  https://github.com/google/jsinterop-base/commit/7d0380758b6bef74bd947e284521619b6826346f

* Collections returned from props should be made immutable.

* Fix `@Prop` change propagation in Arez components so changes are generated even if renderRequired flag is set.

* Props should not be observable by default. Maybe mark each prop as observable or not (and thus requiring change
  propagation or not). We could have `ENABLE`, `DISABLE`, `AUTODETECT` scenario. `AUTODETECT` would be driven by a
  class level annotation which defines default - perhaps it could be `ENABLE`, `DISABLE`, `AUTODETECT` too - with
  `AUTODETECT` only enabling propagation if there is `@Computed` or `@Observed` methods present?

* Arez Components that only have dependencies on props at end of render could warn if they may not need to
  be arez components.

* Add `Observer` react component that is just an arez component that performs change tracking for render prop.

* Migrate to React 16.5.0 features
  - https://github.com/facebook/react/blob/master/CHANGELOG.md#1650-september-5-2018
  - DevTools profiling capabilities
  - Consider generating "Interaction tracking with React" - https://gist.github.com/bvaughn/8de925562903afd2e7a12554adcdda16
  - https://elijahmanor.com/react-devtools-profiler/
  - Consider adding `react-dom/profiling` "production" javascript profiler - may need to wait for umd variant of
    production js produced. - https://github.com/facebook/react/issues/13634 

* Migrate to React 16.4.0 features - See https://reactjs.org/blog/2018/05/23/react-v-16-4.html
  - Pointer Events

* Move to React 16.3.0 - See https://reactjs.org/blog/2018/03/29/react-v-16-3.html
  - Implement `getDerivedStateFromProps` static method (via an annotation on a static method?)
  - Implement `getSnapshotBeforeUpdate` lifecycle method.
  - Add `snapshot` parameter to `componentDidUpdate`
  - Add support for the new ref proposal.
    - Consider typed refs for DOM factories
    - Support `React.createRef()` for refs as well as callbacks. They should be typed as well:
      See https://www.fullstackreact.com/articles/using-refs-in-react/

### High Priorities

* Update generated builder to allow skipping of default steps and onto next step and set default value in builder

* Add default values for state via `@InitialState`. Can be instance methods, static methods or static final fields?

* Change state setters so that if during construct then sets initial state otherwise schedules state update.

* Add ability to `@Prop` to add enhancers to builder.

* Add support for methods annotated with `@OnPropChanged` and `@OnStateChanged`

* Add support for methods annotated with `@PropValidate` method. These would be optimized out in production mode.
  In development mode the types, and requiredness should already be checked but this would allow additional custom
  validation.

* Add ability to `@Prop` to add enhancers to builder.

* Consider separating Arez react component infrastructure into a mixin with default methods.

### Medium Priorities

* Move the Props and State objects back to being JsType backed under the cover rather than values looked up by
  strings. This will significantly optimize the output size. At least under J2CL/Closure.

* Figure out a way to define dom factories in java that are optimized away in production such that
  `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
  compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
  Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
  element factories can be built by looking at html spec and auto-generating?
  - https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/react/index.d.ts
  - https://www.w3schools.com/tags/ref_standardattributes.asp

* Add decorations to `@Props` that indicate whether they will be looked at in `PureComponent` scenario. i.e.
  Could decide to skip event handlers to reduce overhead and changes due to parent re-render. Or maybe just
  default to skipping `@FunctionalInterface` annotated ot `@JsFunction`

* Components that have no `@State` methods, no fields, no lifecycle methods and are not subclasses of
  `ReactArezComponent` could be made into stateless components when translating to React. This could also
  be enforced by a `stateless` parameter on the `@ReactComponent` annotation of type `Feature`. An even
  better optimization - at least in production would be to eliminate the component altogether and effectively
  have the `build()` method on the builder call the render method directly. Caching could also be enabled based
  on props.

### Low Priorities

* Port transition code ala
  - https://github.com/reactjs/react-transition-group
  - https://reactcommunity.org/react-transition-group/
  or maybe https://react-move.js.org/

* Introduce performance tools as in https://github.com/nitin42/react-perf-devtool into tool chain

* In base class have configuration that warns on re-renders that produced duplicate values. Note: that the tool to do
  this no longer works in React 16 but the ReactJS team expect something will replace it in the future.

* build in https://github.com/maicki/why-did-you-update

* Consider adopting variable arguments ala https://fblitho.com/docs/props#variable-arguments

* HackerNews clone
  - https://hnpwa.com/
  - https://github.com/rwieruch/react-mobx-hackernews
  - https://github.com/clintonwoo/hackernews-react-graphql
  - https://github.com/prabirshrestha/hn-react-mobx
  - https://github.com/kristoferbaxter/react-hn

#### Compiler optimizations

(Sourced from [ReactJS Changelog](https://reactjs.org/blog/2015/10/07/react-v0.14.html#compiler-optimizations))
Implement these compiler optimizations sourced from Babel 5.8.24 and newer. Both of these transforms should be enabled
only in production (e.g., just before minifying your code) because although they improve runtime performance, they make
warning messages more cryptic and skip important checks that happen in development mode, including propTypes.

* Inlining React elements: The optimisation.react.inlineElements transform converts JSX elements to object literals
  like {type: 'div', props: ...} instead of calls to React.createElement.
* Constant hoisting for React elements: The optimisation.react.constantElements transform hoists element creation to
  the top level for subtrees that are fully static, which reduces calls to React.createElement and the resulting
  allocations. More importantly, it tells React that the subtree hasn’t changed so React can completely skip it
  when reconciling.

* Consider moving factories to creating elements directly ie.
  https://github.com/kay-is/react-from-zero/blob/master/00-object-elements.html

* Synthesize Fragments - https://github.com/kay-is/react-from-zero/blob/master/04-components.html

#### Documentation

* Add graph reflecting size of TodoMVC over time

* Add documentation recommending that `@Callback` methods are `@JsFunction` annotated. The reason is that they
  will be converted to javascript functions which has significantly less overhead in GWT2.x (i.e. no class
  literals) and they will have useful names attached to them in React DevTool.

* Reorganize documentation using the following sites as inspiration.
  - Reason React - https://reasonml.github.io/reason-react/
  - EmberJS - https://www.emberjs.com/
  - VueGWT - https://axellience.github.io/vue-gwt/introduction/
  - https://mobx.js.org
  - https://redux.js.org/
  - https://fblitho.com/

* Incorporate many of the ideas of Litho. Inadvertantly we seem to have created something that looks similar with
  slight differences. Probably because both libraries try to use idiomatic java patterns with a react-like engine.

* Add a best practices section like: https://fblitho.com/docs/best-practices#props-vs-state

* Add lifecycle image from https://twitter.com/dan_abramov/status/981712092611989509 and credit author

* Or most excellent image from https://medium.freecodecamp.org/why-react16-is-a-blessing-to-react-developers-31433bfc210a 

* Create a jsbin-alike to display GWT code
  - https://github.com/jsbin/jsbin
  - http://jsbin.com/?html,css,js,console,output

* Prepare a screencast for React4j+Arez.
  - See egghead training videos as well.
  - https://www.youtube.com/playlist?list=PLV5CVI1eNcJhc9Lxu83Zp4uyqP2yKV4xl&app=desktop
