---
title: Compared to React
category: General
order: 2
---

React4j is based on [React](https://reactjs.org) so it is natural to ask how they compare and
what are the differences between the two libraries. React4j is written in Java rather than
javascript so there are some features thar just don't make sense to implement in a Java
environment. There are also some features of React that exist for legacy reasons so are not
implemented in React4j.

### setState versus scheduleStateUpdate

In React, [`setState(...)`](https://reactjs.org/docs/react-component.html#setstate) enqueues changes to the
component state and tells React that this component and its children need to be re-rendered with the updated
state. This can be confusing to new users who sometimes try to read the state property immediately after calling
`setState(...)` and the "old" value is returned. To avoid this confusion React4j decided to rename this method
to [scheduleStateUpdate()]({% api_url core.Component scheduleStateUpdate(S)%}). This continues to be the primary
method you use to update the user interface in response to event handlers and server responses.

### forceUpdate versus scheduleRender

In React, [`forceUpdate(...)`](https://reactjs.org/docs/react-component.html#forceupdate) tells the runtime
to schedule the component for re-rendering. If this method is invoked it will skip the `shouldComponentUpdate()`
method. It is not an uncommon pattern for a component to schedule a re-render by invoking `setState({})` if you
still want the component to call `shouldComponentUpdate()` lifecycle method. To unify these two mechanisms for
scheduling a render, React4j decided to define the method to [scheduleRender(boolean force)]({% api_url core.Component scheduleRender(boolean) %}).
If you pass `force = true` then `forceUpdate()` will be invoked on the underlying react component otherwise
`setState({})` will be invoked. In both React and React4j, explicitly scheduling a re-render should be avoided
but if you need to do it you can.

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

<div class="example">
{% highlight java %}
{% file_content react4j/examples/factory_example/FactoryExample.java "start_line=/^  {/" "end_line=/^  }/" include_start_line=false include_end_line=false strip=true %}
{% endhighlight %}
</div>

### Stateless Function Components

Stateless function components or SFCs are often used in React but not so much in React4j. The reasoning for this is
in the answer to the FAQ question ["Where is the equivalent of React's stateless function components?"]({{ site.baseurl }}/faq/#where-is-the-equivalent-of-reacts-stateless-function-components)

### Context

React4j does not support the ["context"](https://reactjs.org/docs/context.html) API from React. The context api
allows you to pass data through the component tree without having to pass the props down manually at every level.
However the react docs state at length that this is an experimental API likely to change in the future.

The main use cases for this API seems to be to pass environment settings and global services into components.
Java already has several well thought out solutions in this space, the most popular candidate for GWT applications
being [Dagger](https://google.github.io/dagger/). Rather than adopt the context API, React4j has decided to encourage
the use of java native solutions such as Dagger.
