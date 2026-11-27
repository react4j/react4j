goog.module('react4j.dom.UpdateSchedulingTest');
const Mode = goog.require('react4j.browser.Mode');
const Scheduling = goog.require('react4j.dom.UpdateSchedulingBrowserTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({testUpdateScheduling() { Scheduling.run('production' === Mode.environment); }});
