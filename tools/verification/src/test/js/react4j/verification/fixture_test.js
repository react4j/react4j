goog.module('react4j.verification.Fixture');
const Fixture = goog.require('react4j.verification.fixture.MeasurementFixture');
/** @return {!Array<*>} */
window['runCoverageFixture'] = function() {
  const values = [Fixture.nativeRoundtrip(), Fixture.multiline(), Fixture.skipped(false)];
  try { Fixture.throwing(); } catch (e) { values.push('throw'); }
  Fixture.deferred();
  Fixture.deferredClass();
  values.push(Fixture.decisions(1));
  values.push(Fixture.assertion(true));
  try { Fixture.assertion(false); } catch (e) { values.push('assert'); }
  values.push(Fixture.enhanced([2, 3]), Fixture.enhanced([]), Fixture.ambiguous(false, true));
  Fixture.completedVoid();
  try { Fixture.throwingVoid(true); } catch (e) { values.push('void throw'); }
  Fixture.deferredVoid();
  Fixture.constructorMetadata();
  values.push(Fixture.pattern(null), Fixture.pattern('abc'), Fixture.pattern(true));
  return values;
};
