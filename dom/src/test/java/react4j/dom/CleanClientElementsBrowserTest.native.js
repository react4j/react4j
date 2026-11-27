/** @param {!Element} node @param {number} index @return {?} */
CleanClientElementsBrowserTest.child = function(node, index) {
  return node.childNodes[index];
};

/** @param {!Element} node @return {number} */
CleanClientElementsBrowserTest.childCount = function(node) {
  return node.childNodes.length;
};

/** @param {!Element} node @param {string} namespace @param {string} name @return {?string} */
CleanClientElementsBrowserTest.attributeIn = function(node, namespace, name) {
  return node.getAttributeNS(namespace, name);
};

/** @param {string} name @return {?} */
CleanClientElementsBrowserTest.symbol = function(name) {
  return Symbol.for(name);
};

/** @param {function(!Object):!Object} factory @return {function(!Object):!Object} */
CleanClientElementsBrowserTest.constructable = function(factory) {
  function View(props) { return factory(props); }
  View.prototype.isReactComponent = {};
  return View;
};

/** @param {!Object} instance */
CleanClientElementsBrowserTest.mockRender = function(instance) {
  const render = instance['render'];
  const mocked = render.bind(instance);
  mocked['_isMockFunction'] = true;
  instance['render'] = mocked;
};
