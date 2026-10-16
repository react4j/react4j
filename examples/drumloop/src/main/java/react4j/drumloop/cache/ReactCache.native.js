// React Suspense requires the original JavaScript promise or rejection value.
/**
 * @param {*} value
 * @return {*}
 */
ReactCache.throwValue = function(value) {
  throw value;
};
