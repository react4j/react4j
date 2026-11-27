/** @param {function():void} throwError @return {*} */
Reconciler.nativeThrown = function(throwError) {
  try {
    throwError();
  } catch (error) {
    return error;
  }
};

/** @return {?Performance} */
Reconciler.userTimingReporter = function() {
  const timing = globalThis.performance;
  if (!timing || typeof timing.mark !== 'function' || typeof timing.measure !== 'function' ||
      typeof timing.clearMarks !== 'function' || typeof timing.clearMeasures !== 'function') return null;
  return timing;
};

/** @param {*} value @return {*} */
Reconciler.childIterator = function(value) {
  if (typeof value !== 'object' || value == null) return null;
  const iterator = value[Symbol.iterator] || value['@@iterator'];
  return typeof iterator === 'function' ? iterator : null;
};

/** @param {*} value @param {*} iterator @return {!Array<*>} */
Reconciler.iterateChildren = function(value, iterator) {
  const cursor = iterator.call(value);
  if (cursor == null) throw new Error('An iterable object provided no iterator.');
  const children = [];
  for (;;) {
    const step = cursor.next();
    if (step == null || (typeof step !== 'object' && typeof step !== 'function')) {
      throw new TypeError('Iterator result is not an object.');
    }
    if (step.done) break;
    children.push(step.value);
  }
  return children;
};

/** @param {*} value @param {string} addendum */
Reconciler.invalidChildObject = function(value, addendum) {
  const description = Object.prototype.toString.call(value) === '[object Object]'
      ? 'object with keys {' + Object.keys(/** @type {!Object} */ (value)).join(', ') + '}'
      : String(value);
  throw new Error('Objects are not valid as a React child (found: ' + description + ').' + addendum);
};

/** @param {*} value @return {!Array<string>} */
Reconciler.objectKeys = function(value) {
  return Object.keys(/** @type {!Object} */ (value));
};

/** @param {*} value @param {string} key @return {boolean} */
Reconciler.ownsProperty = function(value, key) {
  return Object.prototype.hasOwnProperty.call(/** @type {!Object} */ (value), key);
};

/** @param {*} value @return {*} */
Reconciler.throwRaw = function(value) {
  throw value;
};

/** @param {!Object} element @param {*} target @param {!Object} props @return {!Object} */
Reconciler.retarget = function(element, target, props) {
  return {
    '$$typeof': element['$$typeof'],
    'type': target,
    'key': null,
    'ref': element['ref'],
    'props': props,
    '_owner': element['_owner'],
    '_source': element['_source'],
  };
};

/** @return {boolean} */
Reconciler.supportsNonExtensibleKeys = function() {
  try {
    const key = Object.preventExtensions({});
    return new Map([[key, true]]).get(key) === true && new Set([key]).has(key);
  } catch (error) {
    return false;
  }
};
