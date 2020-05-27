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

* Consider defaulting requireId=ENABLE in Arez to avoid errors with immutable props

* Rename `ReactComponent` to `React4jComponent`

* Consider eliminating the need to extend `react4j.Component` as can probably get away with just defining render
  method these days??? Marked via interface and/or annotation? We may also need a way for the component to force
  itself to render. Probably exposed via another annotation.

### Enhancements

* The message that indicates prop does not match immutable constraints should say exactly why. (i.e. Is not
  primitive, Arez component nor implements Keyed or if an ArezComponent indicate it needs a requireId=ENABLE etc)

* Add additional `@ReacComponent.type` value `STATELESS` that would result in render being inlined into caller
  without a component in production mode. Component must have no fields, arez elements and no lifecycle methods.
  The `shouldComponentUpdate()` is skipped for this scenario.

* Reactor the way the `dom` library creates host `ReactElement` instances to avoid copying props and adding
  special handling for `ref`, `key` and `children` and instead creating elements directly.

* Figure out a way to get the *Builders eliminated. May need a closure-compiler pass.

* Migrate to React 16.5.0 features
  - https://github.com/facebook/react/blob/master/CHANGELOG.md#1650-september-5-2018
  - Consider generating "Interaction tracking with React" - https://gist.github.com/bvaughn/8de925562903afd2e7a12554adcdda16
  - Consider adding `react-dom/profiling` "production" javascript profiler - may need to wait for umd variant of
    production js produced. - https://github.com/facebook/react/issues/13634

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

* Consider replacing the "backend" of the component model with hooks. The advantages are:
  - The DevTools could be updated to present Arez debug data in nested block.

* Integrate [React.warn](https://github.com/facebook/react/pull/15170) when it ships.

#### Documentation

* Reorganize documentation using the following sites as inspiration.
  - Reason React - https://reasonml.github.io/reason-react/
  - VueGWT - https://axellience.github.io/vue-gwt/introduction/
  - https://fblitho.com/

* Incorporate many of the ideas of Litho. Inadvertently we seem to have created something that looks similar with
  slight differences. Probably because both libraries try to use idiomatic java patterns with a react-like engine.

* Add lifecycle image from https://twitter.com/dan_abramov/status/981712092611989509 and credit author

* Or most excellent image from https://medium.freecodecamp.org/why-react16-is-a-blessing-to-react-developers-31433bfc210a

* Prepare a screencast for React4j+Arez.
  - Probably use ScreenFlow software https://www.telestream.net/screenflow/
  - Possibly with custom scripting - see https://wickstrom.tech/programming/2018/10/26/writing-a-screencast-video-editor-in-haskell.html
  - See egghead training videos as well.
  - https://www.youtube.com/playlist?list=PLV5CVI1eNcJhc9Lxu83Zp4uyqP2yKV4xl&app=desktop
