/** @param {function():*} read @return {*} */
ReactCacheTest.catchThrown = function(read) {
  try { read(); } catch (error) { return error; }
  throw new Error('Expected read to throw');
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
