goog.module('react4j.utilitydemo.BrowserTest');
const Test = goog.require('react4j.utilitydemo.UtilityCssBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 20000; },
  testPackagedStylesAndState() { return Test.testPackagedStylesAndState(); },
});
