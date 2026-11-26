goog.module('react4j.dom.DevToolsTest');
const DevToolsHookTest = goog.require('react4j.dom.DevToolsHookTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testHook() { DevToolsHookTest.run(); },
});
