---
title: Arez
---

**WARNING**: This document is no longer up to date. We hope to fix this in the medium term.

The combination of [Arez](https://arez.github.io) and React4j creates a powerful toolkit. Arez manages
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
`ReactArezComponent`. This will result in the `render()` method being invoked
within the scope of a read-only, tracking (Arez) transaction. Changes to the observable state accessed within the
scope of the `render()` will schedule the component for re-rendering.

Below is a `Footer` component extracted from a [TodoMVC](http://todomvc.com/) implementation. It accesses the
observable state `AppData.model.totalCount()`, `AppData.viewService.getFilterMode()` and
`AppData.model.completedCount()` and will re-render each time any of these values change.

{@file_content: file=react4j/examples/arez/step1/Footer.java start_line=@ReactComponent "end_line=^}"}

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

However let's assume that this component needs to be optimized and walk through the steps that would be required to
optimize the component to reduce the scope and frequency of re-renders.

### Use @Memoize

If you turn on "Highlight Updates" in React's DevTools you will notice that the whole component re-renders any time
a Todo is toggled from "complete" to "not complete" or vice-versa. However the html output only changes if the
number of completed Todos changes from 0 to not zero or from not zero to zero.

To eliminate these unnecessary renders, the simplest approach is to extract the expression
`AppData.model.completedCount() > 0` into a separate `@Memoize` method. The `render()` method will only be scheduled
to render if the value returned from the `@Memoize` method changes.

This method will look like:

{@file_content: file=react4j/examples/arez/step2/Footer.java start_line=@Memoize "end_line=^}" include_end_line=false}

Using computed properties is one of the easiest and least intrusive mechanisms for optimizing components.

### Extract Components

If we return to React's DevTools and turn "Highlight Updates" on again. The next thing you will notice is that the
component is re-rendered any time a Todo is added or removed as the value for the expression
`AppData.model.totalCount()` changes. Unfortunately `@Memoize` will not help us here as the html output changes
every time a re-render occurs. However we can decide to limit the scope of the rendering by extracting a component
that encapsulates the html that changes.

{@file_content: file=react4j/examples/arez/step2/FooterTodoCount.java start_line=@ReactComponent}

This component can be rendered via an expression such as `React.createElement( FooterTodoCount_.TYPE )`.

The `FooterTodoCount` component will still be re-rendered every time a Todo is added or removed but the scope
of the re-render is much smaller and thus the amount of work that React4j has to do is much smaller.

We could also extract another component to manage the links and only re-render this new component when the
`filterMode` observable property changes but we have decided against this as it is a relatively infrequent event.
The final `Footer` component looks something like:

{@file_content: file=react4j/examples/arez/step2/Footer.java start_line=@ReactComponent "end_line=^}"}

### On Optimizing

Ultimately measuring performance and optimizing when needed to keep within your performance budget is the ideal
goal. It is important to know which parts of your application need to be fast and which parts are
less important to optimize. In some cases, the application is small enough to never need optimization while
in others optimizing components by default may be a good option (i.e. if the cost of optimization is lower
than the cost of determining which parts of the application to optimize).

## Best Practices

Arez and React4j is such a powerful combination that many of the best practices that you use when building a
React4j application no longer make sense after you integrate Arez. However this section will try to give some
helpful suggestions that simplify your development experience. You should also checkout the Arez
[FAQ Section](faq.html#arez-integration).

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

* The component would otherwise be a generic component and need not extend `ReactArezComponent`.
* The state never needs to be shared with any other component.

### Avoid writing "business logic" in your React4j components

In an ideal world your Arez/React4j application, React4j should just be providing the view and Arez should
provide the business logic. The business logic method in the Arez components are then called from React4j
components. This makes it much easier to reuse, refactor and test the business logic. In most cases
the business logic can be tested outside the browser in pure java. Another advantage is that it makes the
application much easier to understand.

### Separate network interactions from React4j components and other arez business logic components

Network interactions can be notoriously difficult to test. They are business logic and should not be put in
your React4j components and instead should be triggered from arez components.

One approach is to extract the service API calls behind an interface and pass the service interface into the
constructor of the arez component. This way the unit tests can pass in a mock API service during testing.

Consider the example where you have an action that wants to transition to a view listing employees and wants
to load all the employee data for the view. A typical example using the "extract a remote service interface"
strategy would be:

{@file_content: file=react4j/examples/arez/best_practice/step1/EmployeeService.java start_line=@ArezComponent "end_line=^}"}

Another approach that is even easier to test and arguably easier to understand is to have the the `@Action`
annotated method set state the defines the "intent" to perform a remote service and then have a separate arez
component that uses an `@Autorun` method that observes the "intent" and performs remote call when the intent
indicates that it is required.

So this results in some minor modifications to the employee service so that the action is implemented as follows:

{@file_content: file=react4j/examples/arez/best_practice/step2/EmployeeService.java "start_line=private boolean _loadEmployeeData" "end_line=EXAMPLE ENDS HERE" include_end_line=false}

Then there would be a separate arez component to observe the intent and perform the remote call:

{@file_content: file=react4j/examples/arez/best_practice/step2/EmployeeDataLoader.java start_line=@ArezComponent}

This approach where you separate intent and have another component that performs the remote call means that
your unit tests are much more focused and simpler to understand. It has the disadvantage that it requires more
verbose code constructs and can result in more abstraction and indirection. In a small application with a single
developer this can have a negative effect. Larger applications with larger teams may benefit from the higher
level of abstraction.

### Avoid arez annotations other than @Memoize and @Action in React4j components

Following the above best practices, you will find you rarely if ever need to annotate any methods in a
`ReactArezComponent` subclass with any Arez annotations other than `@Memoize` and `@Action`.
