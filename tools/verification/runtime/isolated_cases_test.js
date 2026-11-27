goog.module('react4j.testing.IsolatedCasesTest');
const Cases = goog.require('react4j.testing.IsolatedCases');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testIsolatedReconcilerCases() { Cases.run(Mode.environment); },
});
