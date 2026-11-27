goog.module('react4j.dom.ComponentLifecycleTest');
const Lifecycle = goog.require('react4j.dom.ComponentLifecycleBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({testComponentLifecycle() { Lifecycle.run('production' === Mode.environment); }});
