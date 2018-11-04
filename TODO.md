## TODO

### Next Release

* Change all remaining lifecycle hooks so they have less name cruft. (i.e. `didUpdate()` rather than `componentDidUpdate()`)
  Or maybe we just add annotations to hook into various steps.

* Add some way to define effects which is just method called after render that returns a disposable to stop action.
  Possibly look at observed props and if they change then dispose and re-run? i.e. could be wrapped in `@Observe`
  method that calls dispose on previous return if any. (From react 17)

### Next Next Release

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
  - https://elijahmanor.com/react-devtools-profiler/
  - Consider adding `react-dom/profiling` "production" javascript profiler - may need to wait for umd variant of
    production js produced. - https://github.com/facebook/react/issues/13634

* Migrate to React 16.4.0 features - See https://reactjs.org/blog/2018/05/23/react-v-16-4.html
  - Pointer Events

* Move to React 16.3.0 - See https://reactjs.org/blog/2018/03/29/react-v-16-3.html
  - Implement `getSnapshotBeforeUpdate` lifecycle method.
  - Add `snapshot` parameter to `componentDidUpdate`

### High Priorities

* Add ability to `@Prop` to add enhancers to builder.

* Consider separating Arez react component infrastructure into a mixin with default methods.

### Medium Priorities

* Figure out a way to define dom factories in java that are optimized away in production such that
  `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
  compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
  Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
  element factories can be built by looking at html spec and auto-generating?
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
  allow individual components to register actions to occur at each lifecucle stage. You could still implement
  current lifecycle model by generating registration mechanisms.

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

* Reorganize documentation using the following sites as inspiration.
  - Reason React - https://reasonml.github.io/reason-react/
  - VueGWT - https://axellience.github.io/vue-gwt/introduction/
  - https://fblitho.com/

* Incorporate many of the ideas of Litho. Inadvertantly we seem to have created something that looks similar with
  slight differences. Probably because both libraries try to use idiomatic java patterns with a react-like engine.

* Add lifecycle image from https://twitter.com/dan_abramov/status/981712092611989509 and credit author

* Or most excellent image from https://medium.freecodecamp.org/why-react16-is-a-blessing-to-react-developers-31433bfc210a

* Create a jsbin-alike to display GWT code
  - https://github.com/jsbin/jsbin
  - http://jsbin.com/?html,css,js,console,output

* Prepare a screencast for React4j+Arez.
  - See egghead training videos as well.
  - https://www.youtube.com/playlist?list=PLV5CVI1eNcJhc9Lxu83Zp4uyqP2yKV4xl&app=desktop
