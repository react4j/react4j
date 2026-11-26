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

/**
 * @template View
 * @param {function(!Object<string, *>): !View} type
 * @return {function(!Object<string, *>): !View}
 */
RuntimeParityBrowserTest.namedFailure = function(type) {
  const wrapper = function(inputs) { return type(inputs); };
  Object.defineProperty(wrapper, 'name', {value: 'NativeFailure'});
  return wrapper;
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


/** @param {!Element} target @param {boolean} editable */
RuntimeParityBrowserTest.focusForMove = function(target, editable) {
  (/** @type {!HTMLElement} */ (target)).focus();
  if (editable) {
    target.ownerDocument.getSelection().setBaseAndExtent(target.firstChild, 5, target.firstChild, 2);
  } else {
    /** @type {!HTMLInputElement} */ (target).setSelectionRange(2, 5);
  }
};

/** @param {!Element} target @param {boolean} editable @return {boolean} */
RuntimeParityBrowserTest.focusSelectionPreserved = function(target, editable) {
  if (target.ownerDocument.activeElement !== target) return false;
  if (editable) {
    const selection = target.ownerDocument.getSelection();
    return selection.anchorNode === target.firstChild && selection.anchorOffset === 5 &&
        selection.focusNode === target.firstChild && selection.focusOffset === 2;
  }
  const input = /** @type {!HTMLInputElement} */ (target);
  return input.selectionStart === 2 && input.selectionEnd === 5;
};

/** @param {!Element} container @param {!Element} ancestor @param {function():void} update @return {boolean} */
RuntimeParityBrowserTest.observeMove = function(container, ancestor, update) {
  const observer = new MutationObserver(() => {});
  observer.observe(container, {childList: true});
  try {
    update();
    return observer.takeRecords().some(record => Array.from(record.removedNodes).includes(ancestor));
  } finally {
    observer.disconnect();
  }
};
