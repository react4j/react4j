## TODO

### Next Release

* Change builders so the object contained within builder is a `ReactElement`, thus eliminating the need to invoke
  `createElement()` altogether. If we do this we could create defaults inline and remove magic from `createElement`.
  We could also eliminate code for special handling of `ref` and `key`. DOM elements would still call createElement
  however we may be able to eliminate this over time as well. Update; The MyComponentBuilder.Builder class should
  actually extend `ReactElement` and all the build methods would be `@JsOverlay` methods.

* Change all remaining lifecycle hooks so they have less name cruft. (i.e. `didUpdate()` rather than `componentDidUpdate()`)
  Or maybe we just add annotations to hook into various steps.

* Add some way to define effects which is just method called after render that returns a disposable to stop action.
  Possibly look at observed props and if they change then dispose and re-run? i.e. could be wrapped in `@Observe`
  method that calls dispose on previous return if any. (From react 17)

* Fix following warnings in J2CL code

jsZipCache/3a58dfb54e0ac4f66460fc127b4b9f4f-react4j-core-0.106.jar.js.zip!/react4j/React$$Overlay.impl.java.js:231:
Originally at:
jsZipCache/3a58dfb54e0ac4f66460fc127b4b9f4f-react4j-core-0.106.jar.js.zip!/react4j/React.java:399: WARNING - Property __SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED never defined on Object

jsZipCache/3a58dfb54e0ac4f66460fc127b4b9f4f-react4j-core-0.106.jar.js.zip!/react4j/React$$Overlay.impl.java.js:231:
Originally at:
jsZipCache/3a58dfb54e0ac4f66460fc127b4b9f4f-react4j-core-0.106.jar.js.zip!/react4j/React.java:399: WARNING - Property ReactCurrentOwner never defined on React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED

* Closure compiler has attribute. Can GWT2 also do same??? Can we use the annotation to propagate.
  `@nosideeffects` indicates that a call to the declared function has no side effects. This annotation allows the
  compiler to remove calls to the function if the return value is not used. This is not a signal that the function
  is "pure", it may still read mutable global state. Maybe this should be on the underlying `props()` method.

* Figure out a way to get the *Builders eliminated

* Collections returned from props should be made immutable.

* Consider adding a `type=STATELESS|PURE|STATEFUL|AREZ|AUTODETECT` to component.
  - `STATELESS` => inlined into caller without a component in production mode.
  - `PURE` => autogenerate SCU assuming `Js.isTripleEqual()` for props implies no re-render.
  - `STATEFUL` => can use fields or lifecycle methods.
  - `AREZ` => `STATEFUL` + can use `@Observable`, `@Memoize`, `@Observed`.
  - `AUTODETECT` will be `STATELESS` if no fields, lifecycle methods or `@Observed`/`@Memoize` annotated methods
    and no prop is an arez component. `AUTODETECT` will be `PURE` if it satisfies `STATELESS` and all props are
    primitives or the processor knows shallow comparison works. It will be `AREZ` if it has an arez annotation and/or
    anty props are arez components. Otherwise it is `STATEFUL`.

    For `STATELESS|PURE` components we could add an invariant check to ensure props are not invoked out of render.
    When inlining the `build()` method in builder will access static singleton instance of component, set
    props and call render. Alternatively we could require the users to write it as a static method somewhere.

* Make it possible to specify an arez component that may not always read arez state ... somehow

* Add `@DisposeOnUnmount` which is functionally equivalent to `@CascadeDispose` but available on normal react components

* Generate a compile error if public methods and protected in actual react class .. unless they implement an interface?

* Introduce `TreeLocal` component which is react "context". A single `TreeLocal` can be represented using react 16.4's
  static context field. Multiple `TreeLocal` instances on a component may need to be represented by a chain of
  components the pass down context as props. This may be overly complex so perhaps we could just remove that possibility.

* Consider marking callbacks with marker type `<T extends MouseEventHandler & Serializable>` and mandate the usage
  of such in pure components?

### Very High Priority

* Add helper to autoload js assets

* Make the name of the assets based off the version of the underlying react library. i.e. Name them `react-16.5.0.js`
  rather than `react.js` so cache is never in conflict.

- EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff action that will be re-run when scheduler is enabled. It is whether it would be possible to do
  this lower down in the react stack.
- EventHandlers should probably start profiler "interactions" by default with the ability to disable

* Start to add javascript tests - starting with braincheck ala
  https://github.com/google/jsinterop-base/commit/7d0380758b6bef74bd947e284521619b6826346f

* Add `Observer` react component that is just an arez component that performs change tracking for render prop.

* Migrate to React 16.5.0 features
  - https://github.com/facebook/react/blob/master/CHANGELOG.md#1650-september-5-2018
  - Consider generating "Interaction tracking with React" - https://gist.github.com/bvaughn/8de925562903afd2e7a12554adcdda16
  - Consider adding `react-dom/profiling` "production" javascript profiler - may need to wait for umd variant of
    production js produced. - https://github.com/facebook/react/issues/13634

### High Priorities

* Add ability to `@Prop` to add enhancers to builder.

* Consider separating Arez react component infrastructure into a mixin with default methods.

### Medium Priorities

* Figure out a way to define dom factories in java that are optimized away in production such that
  `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
  compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
  Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
  element factories can be built by looking at html spec and auto-generating? The props classes should also be
  converted into `JsPropertyMap<Object>` instances so that the externs for react.js need not include all the props
  that can not be minimized under J2CL.
  - https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/react/index.d.ts
  - https://www.w3schools.com/tags/ref_standardattributes.asp
  - Consider typed refs that bind to underlying Elemental2 element.

### Low Priorities

* Port transition code ala
  - https://github.com/reactjs/react-transition-group
  - https://reactcommunity.org/react-transition-group/
  or maybe https://react-move.js.org/
  Actually the best options seems to be https://github.com/drcmda/react-spring

* In base class have configuration that warns on re-renders that produced duplicate values.

* build in https://github.com/maicki/why-did-you-update

* Consider adopting variable arguments ala https://fblitho.com/docs/props#variable-arguments

* HackerNews clone
  - https://hnpwa.com/
  - https://github.com/rwieruch/react-mobx-hackernews
  - https://github.com/clintonwoo/hackernews-react-graphql
  - https://github.com/prabirshrestha/hn-react-mobx
  - https://github.com/kristoferbaxter/react-hn

* Port React Material Web Components? - https://jamesmfriedman.github.io/rmwc/

* If we were to ever re-implement the component model at a basic level, an interesting approach would be to
  allow individual components to register actions to occur at each lifecycle stage. You could still implement
  current lifecycle model by generating registration mechanisms. `@PreRender` and `@PostRender` could replace
  `getSnapshotBeforeUpdate` and `componentDidUpdate`/`componentDidMount` for many cases.

* Investigate feasibility of https://github.com/sokra/rawact which compiles react components into native
  browser interactions in attempt to eliminate overhead of library. A similar framework is imba @ http://imba.io/
  which can learn about in https://scrimba.com/p/c6B9rAM

#### Documentation

* Add graph reflecting size of TodoMVC over time

* Reorganize documentation using the following sites as inspiration.
  - Reason React - https://reasonml.github.io/reason-react/
  - VueGWT - https://axellience.github.io/vue-gwt/introduction/
  - https://fblitho.com/

* Incorporate many of the ideas of Litho. Inadvertantly we seem to have created something that looks similar with
  slight differences. Probably because both libraries try to use idiomatic java patterns with a react-like engine.

* Add lifecycle image from https://twitter.com/dan_abramov/status/981712092611989509 and credit author

* Or most excellent image from https://medium.freecodecamp.org/why-react16-is-a-blessing-to-react-developers-31433bfc210a

* Add notes regarding fiber like - https://github.com/acdlite/react-fiber-architecture
  Also useful to extract notes from https://www.youtube.com/watch?v=ZCuYPiUIONs&app=desktop

* Create a jsbin-alike to display GWT code
  - https://github.com/jsbin/jsbin
  - http://jsbin.com/?html,css,js,console,output

* Prepare a screencast for React4j+Arez.
  - Probably use ScreenFLow software https://www.telestream.net/screenflow/
  - Possibly with custom scripting - see https://wickstrom.tech/programming/2018/10/26/writing-a-screencast-video-editor-in-haskell.html
  - See egghead training videos as well.
  - https://www.youtube.com/playlist?list=PLV5CVI1eNcJhc9Lxu83Zp4uyqP2yKV4xl&app=desktop
