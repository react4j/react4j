goog.module('react4j.dom.HostShadyTest');
const Host = goog.require('react4j.dom.DomHostBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testUnnamedShadyDom() { Host.unnamedShadyDom('production' === Mode.environment); },
});
