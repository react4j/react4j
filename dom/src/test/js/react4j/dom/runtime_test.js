goog.module('react4j.dom.RuntimeTest');
const Children = goog.require('react4j.dom.ChildReconciliationTest');
const DevToolsHookTest = goog.require('react4j.dom.DevToolsHookTest');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const Fiber = goog.require('react4j.dom.FiberReconciliationTest');
const OriginalHost = goog.require('react4j.dom.OriginalHostCasesBrowserTest');
const Parity = goog.require('react4j.dom.RuntimeParityBrowserTest');
const Test = goog.require('react4j.dom.RuntimeBrowserTest');
/** @param {boolean} production @return {!Promise<void>} */
async function run(production) {
  Events.runNestingDiagnostics(production);
  Events.runFormDiagnostics(production);
  Children.run();
  await Fiber.run();
  Fiber.interruptedSuspense();
  OriginalHost.run();
  await Test.run();
  Events.runBehavior();
  Events.runEnterLeave();
  Events.runBeforeInput();
  Events.runBeforeInputFallback();
  Events.runControlsAndEvents();
  Events.runUncontrolled();
  Events.runDomEventVariants();
  Events.runInvalidListeners(production);
  Events.runPayloads();
  Events.runKeyboardVariants(production);
  Events.runHostProperties();
  await Parity.run(production);
}
exports = {run, checkDevTools: DevToolsHookTest.run, checkSuspense: DevToolsHookTest.checkSuspense};
