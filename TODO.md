## TODO

### Next Release

* `key` is valid on fragments elements.

* Host `ReactElement` instances should be created within the `dom` library and we could create the instances
  directly rather than copying props and adding special handling for `ref`, `key` and `children`.

* Figure out a way to get the *Builders eliminated. May need a closure-compiler pass.

* Support `@OnChildError` annotation that stands in for `componentDidCatch` lifecycle method.

* Change remaining lifecycle hooks to be annotation driven. i.e.
  If we do this then the annotation processor could completely take over responsibility for implementing lifecycle
  steps rather than part of it being in `react4j.Component`.

* Add some way to define effects which is just method called after render that returns a disposable to stop action.
  Possibly look at Observe props and if they change then dispose and re-run? i.e. could be wrapped in `@Observe`
  method that calls dispose on previous return if any. (From react 17)

* Consider making the methods annotated with `@PostRender`, `@PostUpdate` and `@PostMount` take a parameter that
  will push the call into a task that is invoked at a later time. Roughly we want to be able to take an effect and
  push it outside the commit phase of react rendering and have it run later

* Rather than creating `Lifecycle` and `LiteLifecycle` could just define an interface per lifecycle and define them
  statically in internal package somewhere.

* Should `@PreRender` and `@PostRender` be `@PreCommit` and `@PostCommit` ? or `@PrePaint` and `@PostPaint`?
  or better yet `@PreMutation` and `@PostMutation`?

* An alternative approach to the above is to rename `Component.render()` to `Component.view()` and thus `render` == `updateDom`

* Consider making non-arez components into Arez components that do not track state. This would make it possible
  to use `@CascadeDispose`, `@PostConstruct`, `@PreDispose` and `@PostDispose` annotations as well as better
  `@Inject` support without bloating `react4j` with similar code generation and annotations.

* Should generate an error when any method or field has an Arez annotation when not an arez component

* Collections returned from props should be made immutable.

* Consider renaming `@Prop` to `@Input`

* Should be possible to mark a prop as immutable and if it changes it is a new component. This is essentially
  deriving the key from the props. Maybe we should have a mechanisms for generating a key from immutable props.
  Props would be marked as immutable and if they change it would result in a new key? The key would be synthesized
  at construction time. Primitive types and known wrapper types could be handled explicitly while other types would
  be expected to implement a set of of interfaces to get key component (i.e. `arez.component.Identifiable`,
  `react4j.KeyPart`). If any Key contributing props exist then it would not be possible to explicitly set key.

* Howto: Offscreen rendering?

* Generate documentation for components from annotations. This documentation could use the prop types to give
  basic documentation overview and then use special annotations to give extended documentation and/or reference
  examples that will be both output as documentation and fed into basic test infrastructure. This ensures that the
  examples will continue to work as the library is evolved. There is a few examples like this in react world ...
  stylguidist??

* Consider adding a `type=STATELESS|PURE|STATEFUL|AREZ|AUTODETECT` to component.
  - `STATELESS` => inlined into caller without a component in production mode.
  - `PURE` => autogenerate SCU assuming `Js.isTripleEqual()` for props implies no re-render.
  - `STATEFUL` => can use fields or lifecycle methods. Can also use `scheduleRender()`
  - `AREZ` => `STATEFUL` + can use `@Observable`, `@Memoize`, `@Observe`.
  - `AUTODETECT` will be `STATELESS` if no fields, lifecycle methods or `@Observe`/`@Memoize` annotated methods
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

* Add ability to `@Prop` to add enhancers to builder. Convert several existing special cased methods in builder
  (See TODOs in Generator.java) with the enhancers.

* Consider separating Arez react component infrastructure into a mixin with default methods.

* Consider a reactive event streaming API integration. [Yolk](https://github.com/garbles/yolk) is a good example
  of how something like this could work. I wonder how hard it would be to make it so?

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

* Animation/Transition capabilities are getting more urgent.
  - https://github.com/reactjs/react-transition-group/blob/master/src/Transition.js
  - https://popmotion.io/pose/
  - Source transition components from VueJS. See https://www.udemy.com/vuejs-2-the-complete-guide for good example

* In base class have configuration that warns on re-renders that produced duplicate values.

* build in https://github.com/maicki/why-did-you-update

* Consider adopting variable arguments ala https://fblitho.com/docs/props#variable-arguments

* If we were to ever re-implement the component model at a basic level, an interesting approach would be to
  allow individual components to register actions to occur at each lifecycle stage. We could also use the
  strategies in [ivi](https://github.com/localvoid/ivi) or whatever is winning the
  [uibench](https://localvoid.github.io/uibench/) benchmark at the time. [Nerv](https://github.com/NervJS/nerv)
  has some interesting benchmarks at https://github.com/NervJS/nerv/tree/master/benchmarks

* Investigate feasibility of https://github.com/sokra/rawact which compiles react components into native
  browser interactions in attempt to eliminate overhead of library. A similar framework is imba @ http://imba.io/
  which can learn about in https://scrimba.com/p/c6B9rAM - actually imba + Embers AOT template compiler seem
  like a very very very interesting approach.

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
