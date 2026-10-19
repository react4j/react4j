goog.module('react4j.ConfigurationTest');
const Mode = goog.require('react4j.browser.Mode');
const Test = goog.require('react4j.ReactConfigTest');
const testSuite = goog.require('goog.testing.testSuite');
/** @define {string} */
const profile = goog.define('react4j.test.configuration', 'defaults');
testSuite({
  testConfiguration() { Test.run(profile, 'production' === Mode.environment); },
});
