goog.module('react4j.dom.EmitterTest');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testRegistration() { Events.runEmitterRegistration(); },
  testPropagation() { Events.runEmitterPropagation(); },
  testSubscriptions() { Events.runEmitterSubscriptions(); },
  testSyntheticLifecycle() { Events.runSyntheticLifecycle('production' === Mode.environment); },
  testSyntheticSpecializations() { Events.runSyntheticSpecializations(); },
});
