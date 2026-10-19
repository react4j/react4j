/** @param {string} tag @return {!Element} */
Browser.append = function(tag) {
  const element = document.createElement(tag);
  document.body.appendChild(element);
  return element;
};
/** @param {number} milliseconds @return {!Promise<void>} */
Browser.delay = function(milliseconds) {
  return new Promise(resolve => setTimeout(resolve, milliseconds));
};
/** @param {*} value @return {string} */
Browser.json = function(value) { return JSON.stringify(value); };
/** @param {function():boolean} condition @param {number} timeout @param {string} message @return {!Promise<void>} */
Browser.until = async function(condition, timeout, message) {
  const deadline = Date.now() + timeout;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error(message);
    await new Promise(resolve => setTimeout(resolve, 25));
  }
};
/** @param {function(function():void):void} action @return {!Promise<void>} */
Browser.callback = function(action) { return new Promise(resolve => action(resolve)); };
/** @return {!Object} */
Browser.object = function() { return {}; };
/** @param {number} value @return {*} */
Browser.number = function(value) { return value; };
/** @param {boolean} value @return {*} */
Browser.bool = function(value) { return value; };
/** @return {!Function} */
Browser.noop = function() { return () => {}; };
/** @param {!Object} object @param {string} name @return {*} */
Browser.property = function(object, name) { return object[name]; };
/** @param {*} first @param {*} second @return {boolean} */
Browser.same = function(first, second) { return first === second; };
/** @param {!Element} element @param {string} property @return {string} */
Browser.computed = function(element, property) { return getComputedStyle(element).getPropertyValue(property); };
/** @param {!Element} element @param {string} property @return {string} */
Browser.inlineStyle = function(element, property) { return element.style.getPropertyValue(property); };
