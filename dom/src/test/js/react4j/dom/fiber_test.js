goog.module('react4j.dom.FiberTest');
const Fiber = goog.require('react4j.dom.FiberReconciliationTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({testFiberIdentity() { return Fiber.run(); }, testInterruptedSuspense() { Fiber.interruptedSuspense(); }});
