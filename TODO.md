## TODO

This document is essentially a list of shorthand notes describing work yet to be completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

### Next Release

* Consider adding the ability to pass immutable `@Input` values in as constructor parameters. The
  `immutable` param on the `@Input` could simply be dropped and just anything passed in via constructor
  is by definition immutable. This would require a some rework in code generation, particularly when
  interacting with the dependency injectors but it could simplify a lot of code. A lot of places
  we have a `@PostConstruct` simply so that we can process an immutable prop.

* Add to build process so that every published packaged is attempted to be built using bazel to
  ensure it's dependencies align.

### Enhancements

- EventHandlers in Arez based components should somehow detect Arez.isSchedulerPaused() and persist any event and
  schedule onceoff action that will be re-run when scheduler is enabled. It is whether it would be possible to do
  this lower down in the react stack.
- EventHandlers should probably start profiler "interactions" by default with the ability to disable

