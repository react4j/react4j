goog.module('react4j.verification.RuntimeTest');
const Config = goog.require('react4j.ReactConfigTest');
const Core = goog.require('react4j.RuntimeTest');
const Dom = goog.require('react4j.dom.RuntimeTest');
const Events = goog.require('react4j.dom.EventsTest');
const Example = goog.require('react4j.RuntimeBrowserTest');
const Isolated = goog.require('react4j.testing.IsolatedCases');
const Mode = goog.require('react4j.browser.Mode');
const Properties = goog.require('react4j.dom.PropertiesTest');
/** @return {!Promise<void>} */
async function run() {
  if ('development' === Mode.environment) {
    Dom.checkDevTools();
  }
  Config.browserDefaults('production' === Mode.environment);
  Example.mapProbe();
  Properties();
  Events();
  await Core();
  await Dom.run('production' === Mode.environment);
  if ('development' === Mode.environment) {
    Dom.checkSuspense();
  }
}
window['runRuntimeCoverageTests'] = run;
/** Runs lifecycle diagnostics in a fresh realm. */
window['runLifecycleCoverageTests'] = () => Dom.lifecycle('production' === Mode.environment);
/** Runs scheduling diagnostics in a fresh realm. */
window['runSchedulingCoverageTests'] = () => Dom.scheduling('production' === Mode.environment);
/** Exercises the Java reconciler through the in-memory host. */
window['runReconcilerCoverageTests'] = () => Isolated.run(Mode.environment);
/** Verifies native element construction. */
window['runRuntimeMapProbe'] = Example.mapProbe;
