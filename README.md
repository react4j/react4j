# gwt-react-playground

A simple project to experiment with gwt-react

## TODO

* Integrate Arez
* Develop React-Arez extension

* Consider using annotations and generator to generate components
* Add ReactConfig ala ArezConfig so some stuff is optimized out in production.
* Add mechanism for exporting methods with useful displayNames in props
* Overload React methods so can accept arrays
* Move to Arez browser config

* Extract Guards and some of ArezConfig into separate library that can be included here.
  Add invariant checks to make sure people are doing the right thing. 
  (i.e. Make sure call setState in correct places setInitialState only in constructor etc.)
