## TODO

### High Priority

* Props could just be fields or abstract accessors on the component and React4j can generate props. Same with
  state and context?

* Implement context:
  - contextTypes defined on component constructor - (key -> validation function)
  - constructor(props, context)
  - componentWillReceiveProps(nextProps, nextContext)
  - shouldComponentUpdate(nextProps, nextState, nextContext)
  - componentWillUpdate(nextProps, nextState, nextContext)

* Port transition code ala https://github.com/reactjs/react-transition-group

* Add picture from https://hackernoon.com/reactjs-component-lifecycle-methods-a-deep-dive-38275d9d13c0

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

### Low Priorities

* In base class have configuration that warns on re-renders that produced duplicate values. Note: that the tool to do
  this no longer works in React 16 but the ReactJS team expect something will replace it in the future.
* build in https://github.com/maicki/why-did-you-update
* Add support for componentDidCatch() (React16)
* Add support for portals (React16)
* Implement the "children" utilities in java or bind to native implementation.
* Figure out a way to support `getInitialProps()` on components.
  - set "defaultProps" property on constructor function (same way that we set name). All we need to do is detect
    if static property of correct name present on component and if so copy it across as part of annotation processing.
    https://reactjs.org/docs/react-component.html#defaultprops

* Add TodoMVC to web site in both production and development mode so it is easy to describe it in the documentation.

#### Arez DevTools

Arez probably needs the equivalent of Mobx DevTools. We already support a reasonable console logging but need
mechanisms to enable and disable. Possibly we also need to support looking at dependencies of components. Do we
do this by caching arez components on WeakHashmap in componentDidMount/componentWillUnmount and then supporting
accessing transitive dependency tree via UI?

Possibly we also need the ability to browse the repositories in the application. Register repositories on
startup and then browse via tables?
