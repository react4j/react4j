goog.module('react4j.dom.EmitterTest');
const ConcurrentEvents = goog.require('react4j.dom.ConcurrentEventBrowserTest');
const EmitterRegistration = goog.require('react4j.dom.EmitterRegistrationBrowserTest');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testRegistration() { EmitterRegistration.runEmitterRegistration(); },
  testPropagation() { Events.runEmitterPropagation(); },
  testSubscriptions() { Events.runEmitterSubscriptions(); },
  testSyntheticLifecycle() { Events.runSyntheticLifecycle('production' === Mode.environment); },
  testSyntheticSpecializations() { Events.runSyntheticSpecializations(); },
  testKeyboardEvents() { Events.runStockKeyboardEvents(); },
  testEnterLeave() { Events.runStockEnterLeave(); },
  testSelectEvents() { Events.runStockSelectEvents(); },
  testSimpleClicks() { Events.runStockSimpleClicks(); },
  testEventListeners() { Events.runStockEventListeners(); },
  testConcurrentChanges() { return ConcurrentEvents.controlledChanges('production' === Mode.environment); },
  testNestedDispatch() { ConcurrentEvents.nestedDispatch(); },
  testInteractiveUpdates() { return ConcurrentEvents.interactiveUpdates('production' === Mode.environment); },
  testMixedPriorities() { return ConcurrentEvents.mixedPriorities(); },
  testChangeEvents() { Events.runStockChangeEvents(); },
  testBeforeInput() { Events.runStockBeforeInput(); },
  testLegacyBeforeInput() { Events.runLegacyBeforeInput(); },
  testComponentTree() { Events.runStockComponentTree('production' === Mode.environment); },
});
