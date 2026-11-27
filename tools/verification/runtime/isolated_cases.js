goog.module('react4j.testing.IsolatedCases');
const Context = goog.require('react4j.testing.IsolatedContextTest');
const ErrorLogging = goog.require('react4j.testing.IsolatedErrorLoggingTest');
const Incremental = goog.require('react4j.testing.IsolatedIncrementalTest');
const LegacyContext = goog.require('react4j.testing.IsolatedLegacyContextTest');
const ProfilerDevTools = goog.require('react4j.testing.IsolatedProfilerDevToolsTest');
const Reflection = goog.require('react4j.testing.IsolatedReflectionTest');
const Scheduling = goog.require('react4j.testing.IsolatedSchedulingTest');
const SideEffects = goog.require('react4j.testing.IsolatedSideEffectsTest');
const Suspense = goog.require('react4j.testing.IsolatedSuspenseTest');
const Tests = goog.require('react4j.testing.IsolatedReconcilerTest');
const Triangle = goog.require('react4j.testing.IsolatedTriangleTest');
const Updates = goog.require('react4j.testing.IsolatedUpdateQueueTest');
const Wrapped = goog.require('react4j.testing.IsolatedWrappedViewsTest');

/**
 * @param {string} mode
 * @return {number}
 */
exports.run = function(mode) {
  let count = 0;
  /**
   * @param {string} name
   * @param {function():void} action
   */
  function run(name, action) {
    try {
      action();
      count++;
    } catch (/** @type {!Error} */ error) {
      throw new Error(mode + ' ' + name + ': ' + error.message, {cause: error});
    }
  }
  for (let i = 0; i < 20; i++) run('ReactFragment spec' + i, () => Tests.fragment(i));
  for (let i = 0; i < 5; i++) run('ReactTopLevelFragment spec' + i, () => Tests.topLevelFragment(i));
  for (let i = 0; i < 2; i++) run('ReactTopLevelText spec' + i, () => Tests.topLevelText(i === 1));
  for (let i = 0; i < 8; i++) run('ErrorBoundaryReconciliation spec' + i,
    () => Tests.errorBoundary(i < 4, (i % 4) >= 2, (i % 2) === 0));
  for (let i = 0; i < 4; i++) run('ReactPersistent spec' + i, () => Tests.persistence(i));
  for (let i = 0; i < 3; i++) run('ReactPersistentUpdatesMinimalism spec' + i, () => Tests.minimalism(i, true));
  for (let i = 0; i < 9; i++) run('ReactLazy spec' + i, () => Wrapped.lazy(i, mode === 'development'));
  for (let i = 0; i < 8; i++) run('ReactMemo ' + (i < 4 ? 'normal' : 'lazy') + ' case ' + (i % 4),
    () => Wrapped.memo(i >= 4, i % 4));
  for (let i = 0; i < 2; i++) run('ReactFiberHostContext spec' + i, () => Scheduling.hostContext(i === 1));
  for (let i = 0; i < 4; i++) run('ReactExpiration spec' + i, () => Scheduling.expiration(i));
  for (let i = 0; i < 11; i++) run('ReactIncrementalScheduling spec' + i, () =>
    Scheduling.scheduling(i, mode === 'development'));
  for (let i = 0; i < 3; i++) run('ReactIncrementalUpdatesMinimalism spec' + i, () => Tests.minimalism(i, false));
  for (let i = 0; i < 12; i++) run('ReactIncrementalUpdates spec' + i, () =>
    Updates.updates(i, mode === 'development'));
  for (let i = 0; i < 4; i++) run('ReactSuspensePlaceholder spec' + i, () => Suspense.placeholder(i >= 2, i % 2 === 0));
  for (let i = 0; i < 10; i++) run('ReactSuspense spec' + i, () => Suspense.suspense(i));
  run('ReactIncrementalReflection deferred mount', () => Reflection.deferredMount(mode === 'development'));
  run('ReactIncrementalReflection deferred unmount', () => Reflection.deferredUnmount(mode === 'development'));
  run('ReactIncrementalReflection committed host', () => Reflection.findCommittedHost(mode === 'development'));
  for (let i = 0; i < 4; i++) run('ReactIncrementalErrorLogging spec' + i, () =>
    ErrorLogging.errors(i, mode === 'development'));
  run('ReactIncrementalErrorReplay host failure', () => ErrorLogging.replay(true));
  run('ReactIncrementalErrorReplay successful retry', () => ErrorLogging.replay(false));
  run('ReactSuspenseWithNoopRenderer spec0', () => Suspense.noop(0));
  run('ReactSuspenseWithNoopRenderer spec1', () => Suspense.noop(1));
  run('ReactSuspenseWithNoopRenderer spec2', () => Suspense.noop(2));
  run('ReactSuspenseWithNoopRenderer spec8', () => Suspense.noop(8));
  run('ReactSuspenseWithNoopRenderer spec13', () => Suspense.noop(13));
  run('ReactSuspenseWithNoopRenderer spec14', () => Suspense.noop(14));
  run('ReactSuspenseWithNoopRenderer spec15', () => Suspense.noop(15));
  run('ReactSuspenseWithNoopRenderer spec16', () => Suspense.noop(16));
  for (let i = 0; i < 4; i++) run('ReactIncrementalTriangle spec' + i, () => Triangle.cases(i));
  for (let i = 0; i <= 22; i++) {
    if (i <= 13 || i >= 16) {
      run('ReactIncrementalSideEffects spec' + i, () => SideEffects.cases(i, mode === 'development'));
    }
  }
  for (let i = 0; i <= 49; i++) {
    if (i <= 6 || (i >= 14 && i <= 21) || i === 27 || i === 28 || i === 31 || i === 32 || i >= 47) {
      run('ReactIncremental spec' + i, () => Incremental.cases(i));
    }
  }
  for (let i = 33; i <= 46; i++) {
    if (i !== 42) run('ReactIncremental legacy context spec' + i, () => LegacyContext.cases(i, mode === 'development'));
  }
  for (let i = 0; i < 45; i++) {
    run('ReactNewContext spec' + i, () => Context.cases(i % 15, Math.floor(i / 15)));
  }
  for (let i = 45; i <= 56; i++) {
    run('ReactNewContext spec' + i, () => Context.additional(i, mode === 'development'));
  }
  run('ReactProfiler DevTools automatic profiling', () => ProfilerDevTools.autoProfile());
  run('ReactProfiler DevTools recovery timing', () => ProfilerDevTools.recoverTimingAfterError());
  run('ReactProfiler DevTools committed interactions', () => ProfilerDevTools.committedInteractions());
  run('ReactProfiler interleaved automatic root profiling', () => ProfilerDevTools.interleavedRootTiming());
  run('ReactTestRenderer default host refs', () => ProfilerDevTools.nativeRefDefault());
  run('ReactTestRenderer host mocks', () => ProfilerDevTools.nodeMocks());
  run('ReactTestRenderer mocked root instance', () => ProfilerDevTools.mockedRootInstance());
  run('ReactTestRenderer mocked updates and unmount', () => ProfilerDevTools.mockedUpdatesAndUnmount());
  run('ReactTestRenderer independent context', () => ProfilerDevTools.secondaryContext(mode === 'production'));
  return count;
};
