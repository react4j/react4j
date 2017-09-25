# gwt-react-playground

A simple project to experiment with gwt-react

## TODO

* Develop React-Arez extension
* Consider using annotations and generator to generate components
* Add mechanism for exporting methods with useful displayNames in props
* Overload React methods so can accept arrays
* Add invariant checks to make sure people are doing the right thing when interacting with react.
  (i.e. Make sure call setState in correct places setInitialState only in constructor etc.)
* Figure out a way to bundle in react as part of the build with both the optimized and non-optimized variants.
* Also figure out a way that we can get it shipped as part of gwt resources (so it passes through other linker
  step ala AppCache compression etc)
