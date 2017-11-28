## TODO

### High Priority

* Props could just be fields or abstract accessors on the component and React4j can generate props. Same with
  state and context? Seems like `@Inject` for fields with different qualifiers should be sufficient.

* Port transition code ala
  - https://github.com/reactjs/react-transition-group
  - https://reactcommunity.org/react-transition-group/
  or maybe https://react-move.js.org/

* Add picture from https://hackernoon.com/reactjs-component-lifecycle-methods-a-deep-dive-38275d9d13c0

* Integrate Logo into website. Maybe decide on color palette and update icon to match color palette.

* Consider ways or reorganizing - Use react+reason-react as inspiration? Also EmberJS as inspiration - https://www.emberjs.com/

* Extract TodoMVC and port a hackernews clone ala https://github.com/reasonml-community/reason-react-hacker-news

* Document Context
  - how to write a provider
  - how to write a consumer
  - pitfalls of a consumer (link to React docs? - https://reactjs.org/docs/context.html)

### Medium Priorities

* Figure out how components can interact with with dagger
* Figure out a way to define dom factories in java that are optimized away in production such that
   `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
   compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
   Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
   element factories can be built by looking at html spec and auto-generating? Probably get away from writing build
   at end by overloading methods
   Looks like a good way to create factories is described at https://blog.jayway.com/2012/02/07/builder-pattern-with-a-twist/
   with an existing annotation processor approach at https://github.com/ltearno/builder-generator where we could make some
   parameters and potentially children required. Would need to synthesize from annotations. Maybe something like

```java
@ReactComponent
class TodoEntry
  extends ReactArezComponent<BaseProps, TodoEntry.State>
{
  @Prop(
    attributes = {
      @Attribute(name = "foo", type = Integer.class, mandatory = true),
      @Attribute(name = "className", type = String.class, type = ClassNameAttribute.class )
    }
  )
  static class State
    extends AbstractState // AbstractState is generated
  {
  }
```

* Generate all the html props based on typings at https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/react/v15/index.d.ts
* Props and state fields should be read-only abstractions. Mutations create new objects?
* Somehow declare props as interfaces in components and have implementation generated?

* For html props we could just generated via ruby and existing patterns after looking at pages like:
  - https://www.w3schools.com/tags/ref_standardattributes.asp

### Low Priorities

* In base class have configuration that warns on re-renders that produced duplicate values. Note: that the tool to do
  this no longer works in React 16 but the ReactJS team expect something will replace it in the future.
* build in https://github.com/maicki/why-did-you-update
* Implement the "children" utilities in java or bind to native implementation.

* Add TodoMVC to web site in both production and development mode so it is easy to describe it in the documentation.

* EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff autorun that will be re-run when scheduler is enabled. (The autorun will need to dispose itself
  and will need to be marked as runImmediately=false)

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
