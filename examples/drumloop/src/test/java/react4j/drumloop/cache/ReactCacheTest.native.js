/** @param {function():*} read @return {!Object} */
ReactCacheTest.catchRead = function(read) {
  try { read(); } catch (error) { return {thrown: true, value: error}; }
  return {thrown: false, value: null};
};
/** @param {string} value @return {!Promise<string>} */
ReactCacheTest.resolve = function(value) { return Promise.resolve(value); };
/** @param {string} error @return {!Promise<string>} */
ReactCacheTest.reject = function(error) { return Promise.reject(error); };
// The loader's generic return is checked against the native Promise constructor.
/** @return {!Object} */
ReactCacheTest.newThenable = function() {
  return new Promise(function() {});
};
