## TODO

### High Priority

* Add ability to `@Prop` to add enhancers to builder.

* Ensure prop names can have numbers in it - particular field based `@PropDefault`s should derive the name.

* Possibly support refs in generated builders.

* Add mechanism by which plugins can be used to enhance the component generation process. Something that
  could hook into the component lifecycle methods? The goal is to add `@ReplicantData` or `@GraphQL` annotations
  to component and have code generator enhance the component in some fashion to load data as appropriate.
  Could this be like in UI-binder where constructors have to invoke factory? (That would make it easier to
  integrate into code generator) Or do we generate a chain of classes `MyComponent` -> `Graphql_MyComponent` ->
  `React_Graphql_MyComponent` -> `Arez_React_Graphql_MyComponent` ?

* Add decorations to `@Props` that indicate whether they will be looked at in `PureComponent` scenario. i.e.
  Could decide to skip event handlers to reduce overhead and changes due to parent re-render. Or maybe just
  default to skipping `@FunctionalInterface` annotated ot `@JsFunction`

* Components that have not `@State` methods, no lifecycle methods, no `@Ref` methods and no state on the instance
  could be made into stateless components when translating to React. Potentially we can also enforce stateless-ness
  by parameter to `@ReactComponent` annotation.

* Add support for methods annotated with `@OnPropChanged`

* Add support for methods annotated with `@PropValidate` method. These would be optimized out in production mode.
  In development mode the types, and requiredness should already be checked but this would allow additional custom
  validation.

* Add `@Ref` annotation to field in component. This will generate a cached helper function similar to EventHandler
  annotation that can be passed as prop.

* Rename EventHandler annotation with callback and allow it's use as an arbitrary callback mechanism. This
  includes for ref callbacks as well event handlers as well as the so-called render props. This will require
  customization of how we wrap callbacks in `@Action`s. Ref callbacks and render props can obviously not be
  wrapped where EventHandlers should be by default. Alternatively add a separate `@Callback` annotation that
  assumes the callers context rather than the components context (i.e. does not wrap them in `@Action` annotations)

* Separate implementation details and user facing code in enhanced class into `MyComponent_` (User facing code)
  and `React4j_MyComponent` (implementation details)

### Medium Priorities

* Figure out a way to define dom factories in java that are optimized away in production such that
  `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
  compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
  Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
  element factories can be built by looking at html spec and auto-generating?
  - https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/react/v15/index.d.ts
  - https://www.w3schools.com/tags/ref_standardattributes.asp

* State fields should be read-only abstractions. Mutations create new objects?

### Low Priorities

* Complete `react4j-cryptotracker` example.

* Port transition code ala
  - https://github.com/reactjs/react-transition-group
  - https://reactcommunity.org/react-transition-group/
  or maybe https://react-move.js.org/

* Port a hackernews clone
  - https://github.com/clintonwoo/hackernews-react-graphql
  - https://github.com/reasonml-community/reason-react-hacker-news

* Port a medium clone. a.k.a. The "realworld example app".
  - https://github.com/gothinkster/react-mobx-realworld-example-app

* See if we can remove ReactElement.

* Add `@State` annotated methods that are required to come as a pair of abstract getters and setters? These would
  potentially work on top of the underlying react state system.

* Consider changing object returned from `state()` into `JsPropertyMap<Object>`

* Add support for fragments as described in
  https://reactjs.org/blog/2017/11/28/react-v16.2.0-fragment-support.html

* In base class have configuration that warns on re-renders that produced duplicate values. Note: that the tool to do
  this no longer works in React 16 but the ReactJS team expect something will replace it in the future.
* build in https://github.com/maicki/why-did-you-update
* Implement the "children" utilities in java or bind to native implementation.

* Upgrade and build TodoMVC as part of the release process.

* EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff autorun that will be re-run when scheduler is enabled. (The autorun will need to dispose itself
  and will need to be marked as runImmediately=false)

* React `classSet` addon equiv. Also see scalajs variant - https://github.com/japgolly/scalajs-react/blob/master/doc/USAGE.md#react-extensions

* Introduce performance tools as in https://github.com/nitin42/react-perf-devtool into tool chain

#### Arez DevTools

Arez probably needs the equivalent of Mobx DevTools. We already support a reasonable console logging but need
mechanisms to enable and disable. Possibly we also need to support looking at dependencies of components. Do we
do this by caching arez components on WeakHashmap in componentDidMount/componentWillUnmount and then supporting
accessing transitive dependency tree via UI?

Possibly we also need the ability to browse the repositories in the application. Register repositories on
startup and then browse via tables?

* See https://github.com/gaearon/redux-devtools

Once this is done remove the setting of dependencies in state as can trigger infinite state updates in some scenarios.

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

#### Window Portal Notes

* https://github.com/beardedtim/react-child-window/blob/master/index.jsx
* https://www.w3schools.com/jsref/met_win_open.asp
* https://www.npmjs.com/package/react-portal

Copying styles from source document -

* https://hackernoon.com/using-a-react-16-portal-to-do-something-cool-2a2d627b0202

#### Documentation

* Link TodoMVC from web site in both production and development mode.

* Integrate Logo into website. Update color palette of icon and website to match.

* Add search capability to website

* Reorganize documentation using the following sites as inspiration.
  - Reason React - https://reasonml.github.io/reason-react/
  - EmberJS - https://www.emberjs.com/
  - VueGWT - https://axellience.github.io/vue-gwt/introduction/
  - https://mobx.js.org
  - https://redux.js.org/

* Add picture from https://hackernoon.com/reactjs-component-lifecycle-methods-a-deep-dive-38275d9d13c0

* Create a jsbin-alike to display GWT code
  - https://github.com/jsbin/jsbin
  - http://jsbin.com/?html,css,js,console,output
