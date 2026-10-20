goog.module('react4j.todomvc.BrowserTest');
const Test = goog.require('react4j.todomvc.TodoBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 60000; },
  testRawApplication() { return Test.testRawApplication(); },
  testArezApplication() { return Test.testArezApplication(); },
  testStingApplication() { return Test.testStingApplication(); },
});
