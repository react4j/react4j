goog.module('react4j.BrowserTest');
const Test = goog.require('react4j.RuntimeBrowserTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({testRuntime() { return Test.run(); }});
