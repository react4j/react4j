goog.module('react4j.verification.RuntimeTest');
const Core = goog.require('react4j.RuntimeTest');
const Dom = goog.require('react4j.dom.RuntimeTest');
const Events = goog.require('react4j.dom.EventsTest');
const Example = goog.require('react4j.RuntimeBrowserTest');
const Properties = goog.require('react4j.dom.PropertiesTest');
/** @return {!Promise<void>} */
async function run() {
  Example.mapProbe();
  Properties();
  Events();
  await Core();
  await Dom();

}
window['runRuntimeCoverageTests'] = run;
/** Verifies native element construction. */
window['runRuntimeMapProbe'] = Example.mapProbe;
