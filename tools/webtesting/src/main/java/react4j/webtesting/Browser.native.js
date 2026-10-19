/** @param {string} tag @return {!Element} */
Browser.append = function(tag) {
  const element = document.createElement(tag);
  document.body.appendChild(element);
  return element;
};
/** @param {string} tag @return {!Element} */
Browser.appendHead = function(tag) {
  const element = document.createElement(tag);
  document.head.appendChild(element);
  return element;
};
/** @param {!Object} root @param {string} selector @return {!Element} */
Browser.query = function(root, selector) {
  const element = root.querySelector(selector);
  if (!element) throw new Error('Missing element: ' + selector);
  return element;
};
/** @param {!Object} root @param {string} selector @return {boolean} */
Browser.exists = function(root, selector) { return !!root.querySelector(selector); };
/** @param {string} id @return {!Element} */
Browser.byId = function(id) { return document.getElementById(id); };
/** @return {boolean} */
Browser.hasFocus = function() { return document.hasFocus(); };
/** @param {string} url @return {!Promise<string>} */
Browser.fetchText = async function(url) { return (await fetch(url)).text(); };
/** @param {string} url @return {!Promise<*>} */
Browser.fetchJson = async function(url) { return (await fetch(url)).json(); };
/** @param {string} url @return {!HTMLIFrameElement} */
Browser.frame = function(url) {
  const frame = /** @type {!HTMLIFrameElement} */ (document.createElement('iframe'));
  frame['testLoaded'] = new Promise(resolve => { frame.onload = () => resolve(); });
  frame.src = url;
  document.body.appendChild(frame);
  return frame;
};
/** @param {!HTMLIFrameElement} frame @return {!Promise<void>} */
Browser.loaded = function(frame) { return frame['testLoaded']; };
/** @param {string} path @return {string} */
Browser.absoluteUrl = function(path) { return new URL(path, location.href).href; };
/** @param {!Document} page @return {!Array<string>} */
Browser.resources = function(page) { return page.defaultView.performance.getEntriesByType('resource').map(entry => entry.name); };
/** @param {!Document} page @param {string} font @return {!Promise<!Array<!FontFace>>} */
Browser.loadFonts = function(page, font) { return page.fonts.load(font); };
/** @param {!Document} page @return {!Promise<*>} */
Browser.fontsReady = function(page) { return page.fonts.ready; };
/** @param {!Element} element @return {!Array<!Animation>} */
Browser.animations = function(element) { return element.getAnimations(); };
/** @param {!Element} style @return {!Array<!CSSRule>} */
Browser.rules = function(style) { return Array.from(style.sheet.cssRules); };
/** @param {!CSSRule} rule @return {!Array<!CSSRule>} */
Browser.childRules = function(rule) { return Array.from(rule.cssRules); };
/** @param {*} value @return {string} */
Browser.string = function(value) { return String(value); };
/** @param {string} pattern @param {string} value @return {boolean} */
Browser.matchesRegex = function(pattern, value) { return new RegExp(pattern).test(value); };
/** @param {string} pattern @param {string} value @return {?Array<string>} */
Browser.match = function(pattern, value) { return value.match(new RegExp(pattern)); };
/** @param {number} milliseconds @return {!Promise<void>} */
Browser.delay = function(milliseconds) {
  return new Promise(resolve => setTimeout(resolve, milliseconds));
};
/** @param {*} value @return {string} */
Browser.json = function(value) { return JSON.stringify(value); };
/** @param {function():boolean} condition @param {number} timeout @param {number} interval @param {string} message @return {!Promise<void>} */
Browser.until = async function(condition, timeout, interval, message) {
  const deadline = Date.now() + timeout;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error(message);
    await new Promise(resolve => setTimeout(resolve, interval));
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
/** @param {*} object @param {string} name @return {*} */
Browser.property = function(object, name) { return object[name]; };
/** @param {*} first @param {*} second @return {boolean} */
Browser.same = function(first, second) { return first === second; };
/** @param {!Element} element @param {string} property @return {string} */
Browser.computed = function(element, property) { return getComputedStyle(element).getPropertyValue(property); };
/** @param {!Element} element @param {string} property @return {string} */
Browser.inlineStyle = function(element, property) { return element.style.getPropertyValue(property); };
