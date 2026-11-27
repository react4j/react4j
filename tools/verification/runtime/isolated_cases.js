goog.module('react4j.testing.IsolatedCases');
const Scheduling = goog.require('react4j.testing.IsolatedSchedulingTest');
const Suspense = goog.require('react4j.testing.IsolatedSuspenseTest');
const Tests = goog.require('react4j.testing.IsolatedReconcilerTest');
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
  return count;
};
