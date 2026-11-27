goog.module('react4j.dom.FiberTest');
const Classes = goog.require('react4j.dom.ClassLifecycleBrowserTest');
const Fiber = goog.require('react4j.dom.FiberReconciliationTest');
const Memory = goog.require('react4j.dom.MemoryBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const Profiler = goog.require('react4j.dom.ProfilerBrowserTest');
const ReconcilerDiagnostics = goog.require('react4j.dom.ReconcilerDiagnosticsBrowserTest');
const WrappedViews = goog.require('react4j.dom.WrappedViewsBrowserTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testPersistentHostAndUserTiming() { Memory.run(); },
  testProfiler() { Profiler.run('production' === Mode.environment); },
  testWrappedViews() { return WrappedViews.run('production' === Mode.environment); },
  testClassLifecycles() { Classes.run('production' === Mode.environment); },
  testFiberIdentity() { return Fiber.run(); },
  testInterruptedSuspense() { Fiber.interruptedSuspense(); },
  testOrderedStateQueue() { return Fiber.orderedStateQueue(); },
  testLegacyStateCallbacks() { Fiber.legacyStateCallbacks(); },
  testExpiredRoots() { Fiber.expiredRoots(); },
  testReconcilerDiagnostics() { ReconcilerDiagnostics.run('production' === Mode.environment); },
});
