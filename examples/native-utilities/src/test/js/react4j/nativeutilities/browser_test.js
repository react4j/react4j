goog.module('react4j.nativeutilities.BrowserTest');
const Test = goog.require('react4j.nativeutilities.NativeUtilityBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 20000; },
  testStylesStateAndContainer() { return Test.testStylesStateAndContainer(); },
  testScopedPopoversAndEntry() { return Test.testScopedPopoversAndEntry(); },
});
