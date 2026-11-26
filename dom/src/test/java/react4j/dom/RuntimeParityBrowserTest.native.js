/** @param {!Promise<?>} pending @return {*} */
RuntimeParityBrowserTest.throwPending = function(pending) { throw pending; };

// React 16 retries an interrupted class mount using its type as a constructor.
/**
 * @template View
 * @param {function(!Object<string, *>): !View} type
 * @return {function(!Object<string, *>): !View}
 */
RuntimeParityBrowserTest.constructible = function(type) {
  return function(inputs) { return type(inputs); };
};

/** @param {!Element} host @return {!Comment} */
RuntimeParityBrowserTest.mountPoint = function(host) {
  const marker = host.ownerDocument.createComment(' react-mount-point-unstable ');
  host.appendChild(marker);
  return marker;
};

/** @return {!DocumentFragment} */
RuntimeParityBrowserTest.documentFragment = function() {
  return document.createDocumentFragment();
};

/** @return {!Document} */
RuntimeParityBrowserTest.detachedDocument = function() {
  return document.implementation.createHTMLDocument();
};

/** @return {!Comment} */
RuntimeParityBrowserTest.ordinaryComment = function() {
  return document.createComment('not a mount point');
};

/** @param {function(function():void):void} callback @return {!Promise<?>} */
RuntimeParityBrowserTest.rejectingPromise = function(callback) {
  return new Promise((resolve, reject) => callback(() => reject(new Error('Pending work rejected'))));
};

/** @param {function():void} task */
RuntimeParityBrowserTest.nextBrowserTask = function(task) {
  setTimeout(task, 0);
};

/** @return {number} */
RuntimeParityBrowserTest.spendTime = function() {
  const start = performance.now();
  let elapsed = 0;
  while (elapsed < 2) {
    elapsed = performance.now() - start;
  }
  return elapsed;
};
