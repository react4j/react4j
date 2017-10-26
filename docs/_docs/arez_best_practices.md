---
title: Arez
category: General
order: 3
---

The combination of [Arez](http://realityforge.org/arez) and React4j creates a powerful toolkit. Arez manages
the application state and React4j transforms the application state into a view. The React4jArez library provides
the mechanisms for synchronizing the application state with the view. Both React4j and Arez are based on functional
principles and trade higher memory usage for faster execution speed.

React reduces the number of expensive DOM updates through the use of a virtual DOM. The application builds a future
view and this is reconciled against the current view and any differences are applied to the DOM.

Arez is built around a data flow graph where state modifications flow through the graph updating nodes only as
required. Data nodes are always up to date and perform the minimum amount of work based on the graph definition.

The React4jArez library bridges the two systems, triggering view updates when the state updates. Changes to observable
data that a `ReactArezComponent` component uses during rendering will schedule the component for re-rendering. The
developer controls the scope of the re-render by controlling the size of the component.

## Getting Started

The simplest approach to defining an Arez enabled component is simply to extend
[ReactArezComponent]({% api_url arez.ReactArezComponent %}). This will result in the `render()` method being invoked
within the scope of a read-only, tracking (Arez) transaction. Changes to the observable state accessed within the
scope of the `render()` will schedule the component for re-rendering.

Extending `ReactArezComponent` will also make the `props()` and `state()` methods observable which means that they
can only be accessed within the scope of a transaction such as in the `render()` method or in methods annotated with
Arez's `@Action` annotation. React4j will already schedule a re-render when props or state are modified but making
these observable means that Arez methods such as `@Autorun` methods on the component can observe and react to
changes in `props` or `state` values.

As most event handlers within a `ReactArezComponent` component will either access or mutate observable state, the
helper methods generated as a result of annotating a method with `@EventHandler` will default to being annotated with
arez's `@Action` annotation. If you do not wish an event handler to be run as an action you can annotate the event
handler with the [@NoAutoAction]({% api_url arez.NoAutoAction %}) annotation. This can be useful if you want to
explicitly control the actions parameters (i.e. to make the action run in a read-only transaction) or you want to
optimize a frequently called event handler by removing the overhead associated with annotating a method with
`@Action`.

Below is a `Footer` component extracted from a [TodoMVC](http://todomvc.com/) implementation. It accesses the
observable state `AppData.model.totalCount()`, `AppData.viewService.getFilterMode()` and
`AppData.model.completedCount()` and will re-render each time any of these values change.

<div class="example">
{% highlight java %}
{% file_content react4j/examples/arez/step1/Footer.java "start_line=/^@ReactComponent/" "end_line=/^}/" %}
{% endhighlight %}
</div>

---

## Optimizing the component

However this is not the most efficient component. There are several scenarios where the component wil re-render
but produce identical output. This is inefficient as React4j will take time to re-render the component to the
virtual DOM and then additional time to reconcile the virtual DOM against the actual DOM.

Whether this inefficiency has any impact on the user experience will depend upon the application. In particular
it will depend on how frequently the observable data changes, what other parts of the view are updated when the
same observable data changes and how dynamic and complex the remainder of the view is. It is often the case that
re-rendering the entire component is perfectly fine and will have no impact on the users experience, as in the
case with a [TodoMVC](http://todomvc.com/) implementation.

However let's assume that this component needs to be optimizes and walk through the steps that would be required to
optimize the component to reduce the scope and frequency of re-renders.

### Use @Computed

If you turn on "Highlight Updates" in React's DevTools you will notice that the whole component re-renders any time
a Todo is toggled from "complete" to "not complete" or vice-versa. However the html output only changes if the
number of completed Todos changes from 0 to not zero or from not zero to zero.

To eliminate these unnecessary renders, the simplest approach is to extract the expression
`AppData.model.completedCount() > 0` into a separate `@Computed` method. The `render()` method will only be scheduled
to render if the value returned from the `@Computed` method changes.

This method will look like:

<div class="example">
{% highlight java %}
{% file_content react4j/examples/arez/step2/Footer.java "start_line=/@Computed/" "end_line=/^}/" include_end_line=false strip_block=true %}
{% endhighlight %}
</div>

Using computed properties is one of the easiest and least intrusive mechanisms for optimizing components.

### Extract Components

If we return to React's DevTools and turn "Highlight Updates" on again. The next thing you will notice is that the
component is re-rendered any time a Todo is added or reoved as the value for the expression
`AppData.model.totalCount()` changes. Unfortunately `@Computed` will not help us here as the html output changes
every time a re-render occurs. However we can decide to limit the scope of the rendering by extracting a component
that encapsulates the html that changes.

<div class="example">
{% highlight java %}
{% file_content react4j/examples/arez/step2/FooterTodoCount.java "start_line=/@ReactComponent/" %}
{% endhighlight %}
</div>

This component can be rendered via an expression such as `React.createElement( FooterTodoCount_.TYPE )`.

The `FooterTodoCount` component will still be re-rendered every time a Todo is added or removed but the scope
of the re-render is much smaller and thus the amount of work that React4j has to do is much smaller.

We could also extract another component to manage the links and only re-renders when the `filterMode` observable
property changes but decided against this as it is a relatively infrequent event. The final `Footer` component looks
something like:

<div class="example">
{% highlight java %}
{% file_content react4j/examples/arez/step2/Footer.java "start_line=/^@ReactComponent/" "end_line=/^}/" %}
{% endhighlight %}
</div>

### On Optimizing

Ultimately measuring performance and optimizing when needed to keep within your performance budget is the ideal
goal. It is important to know which parts of your application need to be fast and which parts are
less important to optimize. In some cases, the application is small enough to never need optimization while
in others optimizing components by default may be a good option (i.e. if the cost of optimization is lower
than the cost of determining which parts of the application to optimize).

## Best Practices

Arez and React4j is such a powerful combination that many of the best practices that you use when building a
React4j application no longer make sense after you integrate Arez. However this section will try to give some
helpful suggestions that simplify your development experience.

### UI state should be modelled with Arez Observable State

Often applications start by just modelling the application domain classes as observable state. So an application
has observable entities to model `Employee`, `Sale`, `Customer` etc. However it is extremely useful to model user
interface state such as which tab is visible, the current application place or route, whether a button is visible
etc. using observable state. This provides a single, unified mechanism for reacting to changes and updating the user
interface.

In an ideal world, we should be able to persist the arez observable state, relaunch the web page and load the
observable state and the application should appear just as it was before the relaunch.

The question often arises, when should you use React4j component level state. In most cases this state is no
longer necessary however it sometimes makes sense to use it if:

* The component would otherwise be a generic component and need not extend `ReactArezComponent` otherwise.
* The state never needs to be shared with any other component.
