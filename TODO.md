## TODO

This document is essentially a list of shorthand notes describing work yet to be completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

### Next Release

* Consider adding the ability to pass immutable `@Prop` values in as constructor parameters. The
  `immutable` param on the `@Prop` could simply be dropped and just anything passed in via constructor
  is by definition immutable. This would require a some rework in code generation, particularly when
  interacting with the dependency injectors but it could simplify a lot of code. A lot of places
  we have a `@PostConstruct` simply so that we can process an immutable prop.

* Add ability to `@Prop` to add enhancers to builder. Convert several existing special cased methods in builder
  (See TODOs in Generator.java) with the enhancers.

* Add to build process so that every published packaged is attempted to be built using bazel to
  ensure it's dependencies align.

### Enhancements

* The message that indicates prop does not match immutable constraints should say exactly why. (i.e. Is not
  primitive, Arez component nor implements Keyed or if an ArezComponent indicate it needs a requireId=ENABLE etc)

* Add additional `@ReacComponent.type` value `STATELESS` that would result in render being inlined into caller
  without a component in production mode. Component must have no fields, arez elements and no lifecycle methods.
  The `shouldComponentUpdate()` is skipped for this scenario.

- EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff action that will be re-run when scheduler is enabled. It is whether it would be possible to do
  this lower down in the react stack.
- EventHandlers should probably start profiler "interactions" by default with the ability to disable

* In base class have configuration that warns on re-renders that produced duplicate values through something
  like [why-did-you-update](https://github.com/maicki/why-did-you-update) or
  [why-did-you-render](https://github.com/welldone-software/why-did-you-render)

* Add some way to define effects which is just method called after render that returns a disposable to stop action.
  Possibly look at Observe props and if they change then dispose and re-run? i.e. could be wrapped in `@Observe`
  method that calls dispose on previous return if any. (From react 17)

* Consider making the methods annotated with `@PostRender`, `@PostUpdate` and `@PostMount` take a parameter that
  will push the call into a task that is invoked at a later time. Roughly we want to be able to take an effect and
  push it outside the commit phase of react rendering and have it run later

* Collections returned from props should be made immutable.
