goog.module('react4j.verification.Fixture');
const Fixture = goog.require('react4j.verification.fixture.MeasurementFixture');
const Mixed = goog.require('react4j.verification.mixed.MixedPlatformFixture');
/** @return {!Array<*>} */
window['runCoverageFixture'] = function() {
  const values = [Fixture.nativeRoundtrip(), Fixture.multiline(), Fixture.skipped(false)];
  try { Fixture.throwing(); } catch (e) { values.push('throw'); }
  Fixture.deferred();
  Fixture.deferredClass();
  values.push(Fixture.decisions(1));
  values.push(Mixed.browserValue());
  values.push(Fixture.assertion(true));
  try { Fixture.assertion(false); } catch (e) { values.push('assert'); }
  values.push(Fixture.enhanced([2, 3]), Fixture.enhanced([]), Fixture.ambiguous(false, true));
  return values;
};
