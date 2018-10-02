## TODO

### Next Release

* Collections returned from props should be made immutable.

* Components that have no fields, no `@Callback` methods, no lifecycle methods and are not subclasses of
  `ReactArezComponent` could be made into stateless components when translating to React. This could also
  be enforced by a `stateless` parameter on the `@ReactComponent` annotation of type `Feature`. An even
  better optimization - at least in production would be to eliminate the component altogether and effectively
  have the `build()` method on the builder call the render method directly. Caching could also be enabled based
  on props.

* Consider adding a `type=STATELESS|PURE|STATEFUL|AUTODETECT` to component. `STATELESS` would be inlined into
  caller without a component in production mode, `PURE` would have SCU automagically created assuming
  `Object.equals()`, `STATEFUL` == `AREZ`. `AUTODETECT` will be `STATELESS` if no fields, lifecycle methods,
  `@State` methods or `@Observed`/`@Computed` annotated methods and no prop is an arez component. `AUTODETECT`
  will be `PURE` if it satisfies `STATELESS` and all props are primitives or know simple compares. Otherwise
  it is `STATEFUL`

### Very High Priority

* Consider remove `@State` and all related infrastructure. Arez should be sufficient.

* Add helper to autoload js assets

* Make the name of the assets based off the version of the underlying react library. i.e. Name them `react-16.5.0.js`
  rather than `react.js` so cache is never in conflict.

* Consider whether can turn off remove `@Callback` annotation altogether.
- It seems we probably want to turn off `@Action` wrapping for `@Callback`? Unless (see next point)
- EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff autorun that will be re-run when scheduler is enabled. (The autorun will need to dispose itself
  and will need to be marked as runImmediately=false)
_ EventHandlers should probably start scopes for profiler by default with the ability to optionally disable

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
  - Add support for the new ref proposal.
    - Support `React.createRef()` for refs as well as callbacks. They should be typed as well:
      See https://www.fullstackreact.com/articles/using-refs-in-react/

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

* In base class have configuration that warns on re-renders that produced duplicate values.

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
