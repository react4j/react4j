/** @param {!Element} target @return {!Text} */
DomPortalBrowserTest.createTextNode = function(target) {
  return target.ownerDocument.createTextNode('hi');
};

/** @param {function():void} action @param {boolean} add */
DomPortalBrowserTest.documentCapture = function(action, add) {
  if (add) {
    document.addEventListener('click', action, true);
  } else {
    document.removeEventListener('click', action, true);
  }
};
