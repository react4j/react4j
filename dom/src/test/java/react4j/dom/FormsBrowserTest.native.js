/** @param {!Element} element @param {string} type */
FormsBrowserTest.elementEvent = function(element, type) {
  element.dispatchEvent(new Event(type, {bubbles: true}));
};

/** @param {!Element} element */
FormsBrowserTest.focus = function(element) { element.focus(); };

/** @param {!Element} element */
FormsBrowserTest.blur = function(element) { element.blur(); };

/** @param {string} message @param {string} minified @param {function():void} action */
FormsBrowserTest.withoutExpectedWindowError = function(message, minified, action) {
  // React's development render replay also reports the expected exception to the browser.
  const previous = window.onerror;
  window.onerror = function(text, url, line, column, error) {
    if (String(text).includes(message) || String(text).includes(minified)) return true;
    return previous ? previous.call(window, text, url, line, column, error) : false;
  };
  try {
    action();
  } finally {
    window.onerror = previous;
  }
};

/** @param {!Element} parent @param {!Element} child */
FormsBrowserTest.append = function(parent, child) { parent.appendChild(child); };

/** @param {!Element} element @param {string} property @param {*} value */
FormsBrowserTest.setUntracked = function(element, property, value) {
  Object.getOwnPropertyDescriptor(Object.getPrototypeOf(element), property).set.call(element, value);
};

/** @param {function():void} action @return {function():void} */
FormsBrowserTest.unbound = function(action) {
  // The optimized browser bundle is a non-strict classic script. Create a strict callback
  // so this probe observes the supplied receiver without sloppy-mode coercion to globalThis.
  return Function('action', `return function() {
    'use strict';
    if (this !== undefined) throw new Error('Unbound change handler received a receiver');
    action();
  };`)(action);
};

/** @param {function():void} action @return {!Array<string>} */
FormsBrowserTest.captureWarnings = function(action) {
  const previous = console.error;
  const warnings = [];
  console.error = function(format, ...values) {
    let index = 0;
    warnings.push(String(format).replace(/%s/g, () => String(values[index++])));
  };
  try {
    action();
    return warnings;
  } finally {
    console.error = previous;
  }
};

/** @param {function():void} action @return {string} */
FormsBrowserTest.inputWriteTrace = function(action) {
  const create = document.createElement;
  const value = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value');
  const trace = [];
  const observer = new MutationObserver(() => {});
  const record = () => {
    for (const mutation of observer.takeRecords()) trace.push(mutation.attributeName);
  };
  document.createElement = function(name, options) {
    const node = create.call(document, name, options);
    if (name === 'input') {
      const input = /** @type {!HTMLInputElement} */ (node);
      observer.observe(node, {attributes: true});
      Object.defineProperty(node, 'value', {
        configurable: true,
        get() { return value.get.call(input); },
        set(text) { record(); trace.push('write:' + text); value.set.call(input, text); },
      });
    }
    return node;
  };
  try {
    action();
    record();
    return trace.join(',') + ',';
  } finally {
    document.createElement = create;
    observer.disconnect();
  }
};

/** @param {!Element} element @param {string} property @param {function():void} action @return {number} */
FormsBrowserTest.propertyWrites = function(element, property, action) {
  const own = Object.getOwnPropertyDescriptor(element, property);
  let descriptor = own;
  for (let prototype = Object.getPrototypeOf(element); !descriptor && prototype;
      prototype = Object.getPrototypeOf(prototype)) {
    descriptor = Object.getOwnPropertyDescriptor(prototype, property);
  }
  let writes = 0;
  Object.defineProperty(element, property, {
    configurable: true,
    get() { return descriptor.get.call(element); },
    set(value) { writes++; descriptor.set.call(element, value); },
  });
  try {
    action();
    return writes;
  } finally {
    if (own) Object.defineProperty(element, property, own);
    else Reflect.deleteProperty(element, property);
  }
};
