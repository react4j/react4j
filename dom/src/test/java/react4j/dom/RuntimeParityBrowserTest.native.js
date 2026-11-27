/** @param {!Promise<?>} pending @return {*} */
RuntimeParityBrowserTest.throwPending = function(pending) { throw pending; };

/** @param {!Promise<?>} pending @param {function():void} subscribe @return {!Promise<?>} */
RuntimeParityBrowserTest.observeSubscriptions = function(pending, subscribe) {
  const then = pending.then;
  pending.then = function(resolve, reject) {
    subscribe();
    return then.call(pending, resolve, reject);
  };
  return pending;
};

// React 16 retries an interrupted class mount using its type as a constructor.
/**
 * @template View
 * @param {function(!Object<string, *>): !View} type
 * @return {function(!Object<string, *>): !View}
 */
RuntimeParityBrowserTest.constructible = function(type) {
  return function(inputs) { return type(inputs); };
};

/** @param {function(!Object):void} receive @return {function():void} */
RuntimeParityBrowserTest.rootCallback = function(receive) {
  return /** @this {!Object} */ function() { receive(this); };
};

/** @param {function():void} render @return {!Array<string>} */
RuntimeParityBrowserTest.captureKeyWarnings = function(render) {
  const previous = console.error;
  const warnings = [];
  console.error = function(format, ...values) {
    if (String(format).includes('Encountered two children with the same key')) {
      let index = 0;
      warnings.push(values.length ? String(format).replace(/%s/g, () => String(values[index++])) : String(format));
    } else {
      previous.apply(console, arguments);
    }
  };
  try {
    render();
    return warnings;
  } finally {
    console.error = previous;
  }
};

/** @param {string} expected @param {function(*):void} log @return {function():void} */
RuntimeParityBrowserTest.observeCaughtErrorLogs = function(expected, log) {
  const report = console.error;
  const onError = window.onerror;
  console.error = function(message) {
    if (('string' === typeof message && message.startsWith('The above error occurred')) ||
        (message && typeof message.message === 'string' && message.message.endsWith(expected))) {
      log(message);
    } else {
      report.apply(console, arguments);
    }
  };
  window.onerror = function(message) {
    // Keep React's development replay from reaching the test runner without
    // canceling the event, which would suppress React's caught-error log.
    if ('string' === typeof message && message.endsWith(expected)) return undefined;
    return onError ? onError.apply(window, arguments) : undefined;
  };
  return () => {
    console.error = report;
    window.onerror = onError;
  };
};

/** @param {function():void} action @return {*} */
RuntimeParityBrowserTest.thrownBy = function(action) {
  try {
    action();
    return null;
  } catch (error) {
    return error;
  }
};

/** @param {string} message @return {!Promise<?>} */
RuntimeParityBrowserTest.nextUncaughtError = function(message) {
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      window.removeEventListener('error', onError, true);
      reject(new Error('Expected an uncaught error ending in ' + message));
    }, 5000);
    /** @param {!Event} event */
    function onError(event) {
      const detail = /** @type {!ErrorEvent} */ (event).message;
      if ('string' === typeof detail && detail.endsWith(message)) {
        event.preventDefault();
        event.stopImmediatePropagation();
        clearTimeout(timer);
        window.removeEventListener('error', onError, true);
        resolve();
      }
    }
    window.addEventListener('error', onError, true);
  });
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

/** @param {!Object} target @param {*} key @param {*} value */
RuntimeParityBrowserTest.setStateKey = function(target, key, value) {
  target[key] = value;
};

/** @param {!Object} target @param {*} key @return {*} */
RuntimeParityBrowserTest.getStateKey = function(target, key) { return target[key]; };

/** @param {function():string} getter @return {!Object} */
RuntimeParityBrowserTest.withStateGetter = function(getter) {
  return Object.defineProperty({}, 'marker', {enumerable:true, get:getter});
};


/** @param {!Element} iframe @return {!Element} */
RuntimeParityBrowserTest.frameBody = function(iframe) {
  const frame = /** @type {!HTMLIFrameElement} */ (iframe);
  return /** @type {!Element} */ (frame.contentDocument.body);
};

/** @param {!Element} target @param {number} start @param {number} end */
RuntimeParityBrowserTest.selectTextOffsets = function(target, start, end) {
  const doc = target.ownerDocument;
  target.focus();
  // Initialize each point using DOM range coordinates, including boundaries.
  const textNodes = [];
  const walker = doc.createTreeWalker(target, NodeFilter.SHOW_TEXT);
  for (let node = walker.nextNode(); node; node = walker.nextNode()) textNodes.push(node);
  function point(offset) {
    let total = 0;
    for (const node of textNodes) {
      const length = node.textContent.length;
      if (offset <= total + length) return {node, offset: offset - total};
      total += length;
    }
    throw new Error('Offset outside selection fixture.');
  }
  const a = point(start);
  const b = point(end);
  doc.getSelection().setBaseAndExtent(a.node, a.offset, b.node, b.offset);
};

/** @param {!Element} target @return {string} */
RuntimeParityBrowserTest.readTextOffsets = function(target) {
  const doc = target.ownerDocument;
  if (doc.activeElement !== target) return 'unfocused';
  const selection = doc.getSelection();
  return selection.anchorNode.textContent + ':' + selection.anchorOffset + '|' +
      selection.focusNode.textContent + ':' + selection.focusOffset;
};

/** @param {!Element} target @return {boolean} */
RuntimeParityBrowserTest.isActive = function(target) {
  return target.ownerDocument.activeElement === target;
};

/** @param {!Element} owner @param {!Element} target @param {function():void} observed @param {function():void} update */
RuntimeParityBrowserTest.blurDuringInsert = function(owner, target, observed, update) {
  const append = owner.appendChild;
  const insert = owner.insertBefore;
  function blur() {
    target.blur();
    observed();
  }
  owner.appendChild = function(node) {
    blur();
    return append.call(owner, node);
  };
  owner.insertBefore = function(node, before) {
    blur();
    return insert.call(owner, node, before);
  };
  try {
    update();
  } finally {
    delete owner.appendChild;
    delete owner.insertBefore;
  }
};

/** @param {!Element} root @param {function():void} mount */
RuntimeParityBrowserTest.autofocusProbe = function(root, mount) {
  const focus = HTMLElement.prototype.focus;
  let notified = false;
  HTMLElement.prototype.focus = function() {
    if (this.tagName !== 'INPUT' || !root.contains(this)) throw new Error('Focus ran before the input was mounted.');
    notified = true;
  };
  try {
    mount();
    if (!notified) throw new Error('AutoFocus did not run.');
  } finally {
    HTMLElement.prototype.focus = focus;
  }
};
