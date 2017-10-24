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
