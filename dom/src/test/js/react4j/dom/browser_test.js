goog.module('react4j.dom.BrowserTest');
const Runtime = goog.require('react4j.dom.RuntimeBrowserTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testRuntime() { return Runtime.run(); },
});
