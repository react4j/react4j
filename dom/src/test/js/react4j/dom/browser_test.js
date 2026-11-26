goog.module('react4j.dom.BrowserTest');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const Parity = goog.require('react4j.dom.RuntimeParityBrowserTest');
const Runtime = goog.require('react4j.dom.RuntimeBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testControlsAndEvents() { Events.runControlsAndEvents(); },
  testHostProperties() { Events.runHostProperties(); },
  testBeforeInputFallback() { Events.runBeforeInputFallback(); },
  testBeforeInput() { Events.runBeforeInput(); },
  testEnterLeave() { Events.runEnterLeave(); },
  testUncontrolled() { Events.runUncontrolled(); },
  testKeyboardVariants() { Events.runKeyboardVariants('production' === Mode.environment); },
  testDomEventVariants() { Events.runDomEventVariants(); },
  testEvents() { Events.runBehavior(); },
  testEventPayloads() { Events.runPayloads(); },
  testParity() {
    TestCase.getActiveTestCase().promiseTimeout = 15000;
    // React 16 development replays caught render errors through window.onerror.
    const previous = window.onerror;
    /**
     * @param {(!Event|string)} message
     * @param {string=} url
     * @param {number=} line
     * @param {number=} column
     * @param {*=} error
     * @return {boolean|undefined}
     */
    function onError(message, url, line, column, error) {
      if ('string' === typeof message && (message.endsWith('A descendant failed') || message.endsWith(' parity failure'))) {
        return true;
      }
      if (previous) {
        previous.apply(window, arguments);
      }
      return undefined;
    }
    window.onerror = onError;
    return Promise.resolve()
        .then(() => Parity.run('production' === Mode.environment))
        .finally(() => {
          window.onerror = previous;
        });
  },
  testRuntime() { return Runtime.run(); },
});
