goog.module('react4j.drumloop.BrowserTest');
const CacheTest = goog.require('react4j.drumloop.cache.ReactCacheTest');
const Test = goog.require('react4j.drumloop.DrumloopBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 20000; },
  testCacheContracts() { return CacheTest.run(); },
  testPackagedApplication() { return Test.testPackagedApplication(); },
});
