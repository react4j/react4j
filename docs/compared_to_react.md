---
title: Compared to React
---

**WARNING**: This document is no longer up to date. We hope to fix this in the medium term.

React4j is based on [React](https://reactjs.org) so it is natural to ask how they compare and
what are the differences between the two libraries. React4j is written in Java rather than
javascript so there are some features thar just don't make sense to implement in a Java
environment. There are also some features of React that exist for legacy reasons so are not
implemented in React4j.

### The constructor versus postConstruct

In React, components perform initialization in their constructor except for "Stateless Function Components" (SFCs)
which do not need initialization as they are stateless. However, we found that in many react applications there are
a lot of SFCs or stateful components that perform no initialization or only perform event handler binding (which
is not used in React4j). As such we found it to be unnecessary boilerplate and complexity.

In React4j, we decided that components need not define a constructor and could instead make use of the default
constructor automatically provided by the runtime. If you wanted to perform per-component initialization you can
override the lifecycle method `postConstruct`. In practice we found it was rarely used.

### Removal of componentWillMount lifecycle method

In React, the `componentWillMount` method is invoked after a component has been constructed. It is invoked
immediately after the ES6 constructor completes. It is however considered a legacy artifact that was initially
introduced before the ES6 component model was available and is no longer useful for ES6 based components.
The equivalent functionality is provided by `postConstruct` lifecycle method in React4j and `componentWillMount`
has been removed from the React4j API.

### forceUpdate versus scheduleRender

In React, [`forceUpdate(...)`](https://reactjs.org/docs/react-component.html#forceupdate) tells the runtime
to schedule the component for re-rendering. If this method is invoked it will skip the `shouldComponentUpdate()`
method. It is not an uncommon pattern for a component to schedule a re-render by invoking `setState({})` if you
still want the component to call `shouldComponentUpdate()` lifecycle method. To unify these two mechanisms for
scheduling a render, React4j decided to define the annotation
{@link: react4j.annotations.ScheduleRender @ScheduleRender} with a parameter that controls whether
`forceUpdate()` will be invoked or `setState({})` will be invoked.

### getInitialProps versus defaultProps

In React, a static property named [`defaultProps`](https://reactjs.org/docs/react-component.html#defaultprops) on the
class or constructor tells the react runtime the default props for a component. The props provided by the developer
are merged into these props. The equivalent in React4j is the `getInitialProps()` static method on the component
class. This method is called when the component constructor function is created and the results are set as the default
props for the component. The method must be static, non-private, have no parameters, throw no exceptions and must
return a value that is compatible with the prop type for the component.

### Automatic shouldComponentUpdate lifecycle

In React4j, the equivalent of react's `shouldComponentUpdate()` lifecycle method is generated unless the component
is marked as `STATELESS`. A {@link: react4j.annotations.ReactComponent.Type#STATEFUL STATEFUL} component will
re-render if the components container re-renders and passes a prop with a different value from the previous render.
A {@link: react4j.annotations.ReactComponent.Type#STATEFUL STATEFUL} component will also re-render if the component
explicitly invokes `scheduleRender()`. A {@link: react4j.annotations.ReactComponent.Type#TRACKING TRACKING} or
{@link: react4j.annotations.ReactComponent.Type#MAYBE_TRACKING MAYBE_TRACKING} component will also re-render if
any Arez dependency accessed during the previous render is changed.

### JSX

Most react code uses [JSX](https://facebook.github.io/jsx/) to build component hierarchies. JSX is an xml-like
syntax extension that is processed by a transpilers to transform the xml-like expression tokens into standard
ECMAScript.

Consider the following JSX to render a drop-down list:

```xml
<Dropdown>
  A dropdown list
  <Menu>
    <MenuItem>Do Something</MenuItem>
    <MenuItem>Do Something Fun!</MenuItem>
    <MenuItem>Do Something Else</MenuItem>
  </Menu>
</Dropdown>
```

This would be transpiled into:

```javascript
React.createElement(Dropdown, null, "A dropdown list",
  React.createElement(Menu, null,
    React.createElement(MenuItem, null, "Do Something"),
    React.createElement(MenuItem, null, "Do Something Fun!"),
    React.createElement(MenuItem, null, "Do Something Else")
  )
)
```

React also supports html inside JSX which would transform:

```xml
<section>
  <h1>My Title</h1>
  <p>My text...</p>
</section>
```

into

```javascript
React.createElement("section", null,
  React.createElement("h1", null, "My Title"),
  React.createElement("p", null, "My text...")
);
```

React4j does not have the option to use a transpiler so has to use the createElement approach directly. An
introduction to this approach can be found in the article
["Learn Raw React — no JSX, no Flux, no ES6, no Webpack..."](http://jamesknelson.com/learn-raw-react-no-jsx-flux-es6-webpack/).
And the java code is very similar to the transpiled output.

However this approach can be too verbose, so most code actually uses a factory based approach such as the code
that follows. In some cases this is more verbose than the equivalent JSX and sometimes it is more succinct.

{@file_content: file=react4j/examples/factory_example/FactoryExample.java "start_line=^  {" "end_line=^  }" include_start_line=false include_end_line=false}

### Stateless Function Components

Stateless function components or SFCs are often used in React but not so much in React4j. The reasoning for this is
in the answer to the FAQ question ["Where is the equivalent of React's stateless function components?"](faq.html#where-is-the-equivalent-of-reacts-stateless-function-components)
