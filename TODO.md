## TODO

### Very High Priority

* Move to React 16.3.0 - See https://reactjs.org/blog/2018/03/29/react-v-16-3.html
  - Implement `getDerivedStateFromProps` static method (via an annotation on a static method?)
  - Implement `getSnapshotBeforeUpdate` lifecycle method.
  - Add `snapshot` parameter to `componentDidUpdate`
  - Add support for the new ref proposal.
    - Support refs in generated builders.
    - Consider typed refs for DOM factories

### High Priorities

* Add default values for state. Can be instance methods, static methods or static final fields?

* Change state setters so that if during construct then sets initial state otherwise schedules state update.

* Add ability to `@Prop` to add enhancers to builder.

* Add support for methods annotated with `@OnPropChanged`

* Add support for methods annotated with `@PropValidate` method. These would be optimized out in production mode.
  In development mode the types, and requiredness should already be checked but this would allow additional custom
  validation.

* Rather than using `@Dependency` to force the disposal of a component when a prop is disposed, we should instead
  set boolean flag that just indicates that we should skip the render? We probably need to add in the equivalent
  of XonDispose observers that just set this flag if props go bad. We can then unset this flag in shouldComponentUpdate

### Medium Priorities

* Move the Props and State objects back to being JsType backed under the cover rather than values looked up by
  strings. This will significantly optimize the output size.

* Figure out a way to define dom factories in java that are optimized away in production such that
  `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
  compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
  Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
  element factories can be built by looking at html spec and auto-generating?
  - https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/react/v15/index.d.ts
  - https://www.w3schools.com/tags/ref_standardattributes.asp

* Add decorations to `@Props` that indicate whether they will be looked at in `PureComponent` scenario. i.e.
  Could decide to skip event handlers to reduce overhead and changes due to parent re-render. Or maybe just
  default to skipping `@FunctionalInterface` annotated ot `@JsFunction`

* Components that have no `@State` methods, no fields, no lifecycle methods and are not subclasses of
  `ReactArezComponent` could be made into stateless components when translating to React. This could also
  be enforced by a `stateless` parameter on the `@ReactComponent` annotation of type `Feature`.

### Low Priorities

* Complete `react4j-cryptotracker` example.

* Port transition code ala
  - https://github.com/reactjs/react-transition-group
  - https://reactcommunity.org/react-transition-group/
  or maybe https://react-move.js.org/

* EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff autorun that will be re-run when scheduler is enabled. (The autorun will need to dispose itself
  and will need to be marked as runImmediately=false)

* React `classSet` addon equiv. Also see scalajs variant - https://github.com/japgolly/scalajs-react/blob/master/doc/USAGE.md#react-extensions

* Introduce performance tools as in https://github.com/nitin42/react-perf-devtool into tool chain

* In base class have configuration that warns on re-renders that produced duplicate values. Note: that the tool to do
  this no longer works in React 16 but the ReactJS team expect something will replace it in the future.

* build in https://github.com/maicki/why-did-you-update

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

#### Documentation

* Add documentation links to `react-widget` and `react-windowportal`

* Link TodoMVC from web site in both production and development mode.

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
