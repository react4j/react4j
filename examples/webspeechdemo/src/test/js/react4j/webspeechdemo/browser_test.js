goog.module('react4j.webspeechdemo.BrowserTest');
const Test = goog.require('react4j.webspeechdemo.SpeechBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 20000; },
  testNativeBrowserApiAndPackagedPage() { return Test.testNativeBrowserApiAndPackagedPage(); },
  testVoiceChangesAndSpeechLifecycle() { return Test.testVoiceChangesAndSpeechLifecycle(); },
  testUnsupportedSpeechApi() { return Test.testUnsupportedSpeechApi(); },
});
