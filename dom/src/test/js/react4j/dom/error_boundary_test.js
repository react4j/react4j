goog.module('react4j.dom.ErrorBoundaryTest');
const Boundaries = goog.require('react4j.dom.ErrorBoundariesBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');

const production = 'production' === Mode.environment;
/** @type {!Object<string, function():void>} */
const tests = {
  'testMountWithoutBoundary'() { Boundaries.mountWithoutBoundary(); },
  'testUpdateWithoutBoundary'() { Boundaries.updateWithoutBoundary(); },
  'testUnmountWithoutBoundary'() { Boundaries.unmountWithoutBoundary(); },
  'testRejectsBadRoot'() { Boundaries.rejectsBadRoot(); },
  'testFirstLayoutError'() { Boundaries.firstCommitError(false); },
  'testUnbatchedInitialMountError'() { Boundaries.unbatchedInitialMountError(); },
  'testFirstSnapshotError'() { Boundaries.firstCommitError(true); },
  'testWarnsLegacyWithoutState'() { Boundaries.warnsLegacyWithoutState(production); },
  'testInvokesBothCatchMethods'() { Boundaries.invokesBothCatchMethods(); },
};
for (let mode = 0; mode < 2; mode++) {
  const derived = 0 === mode;
  const prefix = derived ? 'testDerived' : 'testLegacy';
  tests[prefix + 'IsolatedRoots'] = () => Boundaries.isolatedRoots(derived);
  tests[prefix + 'CaptureRender'] = () => Boundaries.recoverMountError(derived, production, 'render');
  tests[prefix + 'CaptureConstructor'] = () => Boundaries.recoverMountError(derived, production, 'constructor');
  tests[prefix + 'CaptureWillMount'] = () => Boundaries.recoverMountError(derived, production, 'componentWillMount');
  tests[prefix + 'CaptureContextProviderMount'] = () => Boundaries.captureContextProviderMount(derived, false);
  tests[prefix + 'CaptureModuleProviderMount'] = () => Boundaries.captureContextProviderMount(derived, true);
  tests[prefix + 'MountErrorMessage'] = () => Boundaries.mountErrorMessage(derived, production);
  tests[prefix + 'UnhandledBoundary'] = () => Boundaries.unhandledBoundary(derived, production);
  tests[prefix + 'SuccessfulMount'] = () => Boundaries.successfulMount(derived);
  for (let index = 10; index <= 13; index++) {
    tests[prefix + 'NestedMount' + index] = () => Boundaries.nestedMountError(derived, production, index);
  }
  tests[prefix + 'AbortedCallbackRefs'] = () => Boundaries.abortedRefs(derived, production, false);
  tests[prefix + 'AbortedObjectRefs'] = () => Boundaries.abortedRefs(derived, production, true);
  for (let index = 17; index <= 21; index++) {
    tests[prefix + 'UpdateRenderError' + index] = () => Boundaries.updateRenderError(derived, production, index);
  }
  tests[prefix + 'UpdateRefs'] = () => Boundaries.updateRefs(derived, production);
  tests[prefix + 'UnmountErrorDuringUpdate'] = () => Boundaries.unmountErrorDuringUpdate(derived, production, false);
  tests[prefix + 'NestedUnmountErrorDuringUpdate'] = () => Boundaries.unmountErrorDuringUpdate(derived, production, true);
  tests[prefix + 'NestedUnmountBoundary'] = () => Boundaries.nestedUnmountBoundary(derived, production);
  tests[prefix + 'RecoverFromErrorState'] = () => Boundaries.repeatAndRetry(derived, production, true);
  tests[prefix + 'RepeatedErrors'] = () => Boundaries.repeatAndRetry(derived, production, false);
  for (let index = 28; index <= 30; index++) {
    tests[prefix + 'ConsistentChildren' + index] = () => Boundaries.consistentChildren(derived, production, index);
  }
  for (let index = 31; index <= 35; index++) {
    tests[prefix + 'DownstreamOrCommitError' + index] = () => Boundaries.downstreamAndCommitError(derived, production, index);
  }
}
testSuite(tests);
