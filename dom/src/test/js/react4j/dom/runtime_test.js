goog.module('react4j.dom.RuntimeTest');
const DevToolsHookTest = goog.require('react4j.dom.DevToolsHookTest');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const Parity = goog.require('react4j.dom.RuntimeParityBrowserTest');
const Test = goog.require('react4j.dom.RuntimeBrowserTest');
/** @param {boolean} production @return {!Promise<void>} */
async function run(production) {
  await Test.run();
  Events.runBehavior();
  Events.runEnterLeave();
  Events.runBeforeInput();
  Events.runBeforeInputFallback();
  Events.runControlsAndEvents();
  Events.runUncontrolled();
  Events.runDomEventVariants();
  Events.runKeyboardVariants(production);
  Events.runHostProperties();
  await Parity.run(production);
}
exports = {run, checkDevTools: DevToolsHookTest.run, checkSuspense: DevToolsHookTest.checkSuspense};
