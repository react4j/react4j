goog.module('react4j.verification.RuntimeTest');
const Example = goog.require('react4j.RuntimeCoverageExample');
/** Runs real React element operations. */
function run() {
  if (7 !== Example.multilineElements()) {
    throw new Error('React element roundtrip failed');
  }
  window['coverageDone'] = true;
}
run();
