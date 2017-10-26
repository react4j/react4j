---
title: Arez Best Practices
category: General
order: 3
---

The combination of [Arez](http://realityforge.org/arez) and React4j creates a powerful toolkit. Arez manages
the application state and React4j transforms the application state into a view. The React4jArez library provides
the mechanisms for synchronizing the application state with the view. Both React4j and Arez are based on functional
principles and trade higher memory usage for faster execution speed.

React reduces the number of expensive DOM updates through the use of a virtual DOM. The application builds a future
view and this is reconciled against the current view and only differences are applied to the actual DOM.

Arez is built around a data flow graph where state modifications flow through the graph updating nodes only as
required. Data nodes are always up to date and perform the minimum amount of work based on the graph definition.

The React4jArez library bridges the two systems, triggering view updates when the state updates. The library aims
for an optimal solution so that for a React4j component only changes to data that is accessed during the render
will cause the component to re-render. The developer controls the scope of the re-render by controlling the size
of the component.

## Getting Started

The simplest approach to defining an Arez enabled component is simply to extend
[ReactArezComponent]({% api_url arez.ReactArezComponent %}). This will result in the `render()` method being invoked
within the scope of a read-only, tracking (Arez) transaction. Changes to the observable state accessed within the
scope of the `render()` will schedule the component for re-rendering.

Extending `ReactArezComponent` will also make the `props()` and `state()` methods observable which means that they
can only be accessed within the scope of a transaction such as in the `render()` method or in methods annotated with
Arez's `@Action` annotation. React4j will already schedule a re-render when props or state are modified but making
these observable means that Arez components such as `@Autorun` methods on the component can observe and react to
changes in `props` or `state` values.

As most event handlers within a `ReactArezComponent` component will either access or mutate observable state, the
helper methods generated as a result of annotating a method with `@EventHandler` will default to being annotated with
arez's `@Action` annotation. If you do not wish an event handler to be run as an action you can annotate the event
handler with the [@NoAutoAction]({% api_url arez.NoAutoAction %}) annotation. This can be useful if you want to
explicitly control the actions parameters (i.e. to make the action run in a read-only transaction) or you want to
optimize a frequently called event handkler by removing the overhead associated with annotating a method with
`@Action`.

Below is a `Footer` component extracted from a [TodoMVC](http://todomvc.com/) implementation. It accesses the
observable state `AppData.model.totalCount()`, `AppData.viewService.getFilterMode()` and
`AppData.model.completedCount()` and will re-render each time any of these values change.

<div class="example">
{% highlight java %}
{% file_content react4j/examples/arez/step1/Footer.java "start_line=/^@ReactComponent/" "end_line=/^}/" %}
{% endhighlight %}
</div>

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
