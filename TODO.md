## TODO

### High Priority

* Add picture from https://hackernoon.com/reactjs-component-lifecycle-methods-a-deep-dive-38275d9d13c0

### Medium Priorities

* Figure out how components can interact with with dagger
* Add multiple render methods that return different values. Enhance code generator to ensure that
  the developer only overloads a single variant. Variants include;
  - renderAsElement
  - renderAsString (React16?)
  - renderAsChildren (React16?)
  - renderAsList (React16?)
  - renderAsArray (React16?)
  - Perhaps we go down the same path as Elemental uses for unions?
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
* Somehow declare props as interfaces in components and have implementation generated?
* In base class have configuration that warns on re-renders that produced duplicate values

### Low Priorities

* Document differences between React and React4j. Document the scheduleRender and scheduleStateUpdate difference.
* Should Props and state fields be read-only abstractions?
* build in https://github.com/maicki/why-did-you-update
* Add support for componentDidCatch() (React16)
* Add support for portals (React16)
* Figure out a way to support `getInitialProps()` on components.
  - set "defaultProps" property on constructor function (same way that we set name). All we need to do is detect
    if static property of correct name present on component and if so copy it across as part of annotation processing.
    https://reactjs.org/docs/react-component.html#defaultprops
* It is unclear why we need to wrap setState in ReactArezComponent as that will surely trigger a re-render naturally -
  is it only for other things like @Autorun?
