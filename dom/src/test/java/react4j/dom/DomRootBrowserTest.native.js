/** @param {!Element} parent @param {!Element} child */
DomRootBrowserTest.append = function(parent, child) {
  parent.appendChild(child);
};

/** @param {function(!Object):void} receive @return {function():void} */
DomRootBrowserTest.callbackReceiver = function(receive) {
  return /** @this {!Object} */ function() { receive(this); };
};

/** @param {!Element} frame @return {!Element} */
DomRootBrowserTest.frameBody = function(frame) {
  return /** @type {!Element} */ (/** @type {!HTMLIFrameElement} */ (frame).contentDocument.body);
};

/** @param {!Element} element @param {string} markup */
DomRootBrowserTest.setMarkup = function(element, markup) {
  element.innerHTML = markup;
};

/** @param {!Element} element @param {number} index @return {!Node} */
DomRootBrowserTest.childNode = function(element, index) {
  return element.childNodes[index];
};

/** @return {!DocumentFragment} */
DomRootBrowserTest.fragment = function() {
  return document.createDocumentFragment();
};

/** @param {!Node} parent @param {!Node} child */
DomRootBrowserTest.appendNode = function(parent, child) {
  parent.appendChild(child);
};

/** @return {!Element} */
DomRootBrowserTest.detached = function() {
  return document.createElement('div');
};

/** @param {!Array<?Object>} elements @return {!Iterator<?Object>} */
DomRootBrowserTest.generator = function*(elements) {
  yield* elements;
};

/** @param {!Array<?Object>} elements @param {boolean} modern @return {!Object} */
DomRootBrowserTest.iterable = function(elements, modern) {
  return {[modern ? Symbol.iterator : '@@iterator']: () => DomRootBrowserTest.generator(elements)};
};

/** @return {!Map<string, number>} */
DomRootBrowserTest.map = function() {
  return new Map([['foo', 0], ['bar', 1]]);
};

/** @return {!Document} */
DomRootBrowserTest.detachedDocument = function() {
  const detached = document.implementation.createHTMLDocument('');
  detached.open();
  detached.write('<!doctype html><html><meta charset=utf-8><title>test doc</title>');
  detached.close();
  return detached;
};

/** @param {!Element} element @return {number} */
DomRootBrowserTest.childCount = function(element) {
  return element.childNodes.length;
};

/** @param {!Element} element @param {function():void} render @return {number} */
DomRootBrowserTest.appended = function(element, render) {
  const original = element.appendChild;
  let calls = 0;
  element.appendChild = function(node) {
    calls++;
    return original.call(element, node);
  };
  try {
    render();
    return calls;
  } finally {
    delete element.appendChild;
  }
};
