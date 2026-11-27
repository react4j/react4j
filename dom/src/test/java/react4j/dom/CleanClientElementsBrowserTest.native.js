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
