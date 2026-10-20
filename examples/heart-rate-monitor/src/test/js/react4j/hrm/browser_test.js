goog.module('react4j.hrm.BrowserTest');
const Test = goog.require('react4j.hrm.HeartRateBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 20000; },
  testPackagedDashboardAndBluetoothLifecycle() { return Test.testPackagedDashboardAndBluetoothLifecycle(); },
  testCancelledPairingCanRetry() { return Test.testCancelledPairingCanRetry(); },
  testUnavailableBluetoothPreservesUnsupportedBehavior() { return Test.testUnavailableBluetoothPreservesUnsupportedBehavior(); },
});
