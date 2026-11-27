/** @param {!Element} element @param {string} namespace @param {string} name @return {?string} */
DomHostBrowserTest.attributeNS = function(element, namespace, name) {
  return element.getAttributeNS(namespace, name);
};

/** @param {!Element} parent @param {!Element} child */
DomHostBrowserTest.append = function(parent, child) { parent.appendChild(child); };

/** @param {!Element} parent @param {number} index @return {!Node} */
DomHostBrowserTest.child = function(parent, index) { return parent.childNodes[index]; };

/** @param {!Element} parent @return {number} */
DomHostBrowserTest.childCount = function(parent) { return parent.childNodes.length; };

/** @param {function():void} action */
DomHostBrowserTest.withoutSvgInnerHtml = function(action) {
  const descriptor = Object.getOwnPropertyDescriptor(Element.prototype, 'innerHTML');
  delete Element.prototype.innerHTML;
  Object.defineProperty(HTMLDivElement.prototype, 'innerHTML', descriptor);
  try {
    action();
  } finally {
    delete HTMLDivElement.prototype.innerHTML;
    Object.defineProperty(Element.prototype, 'innerHTML', descriptor);
  }
};

/** @param {!Element} root @param {string} markup */
DomHostBrowserTest.replaceMarkup = function(root, markup) { root.innerHTML = markup; };

/** @param {function():void} action @return {!Array<string>} */
DomHostBrowserTest.captureWarnings = function(action) {
  const original = console.error;
  const warnings = [];
  console.error = function(format, ...values) {
    let index = 0;
    warnings.push(String(format).replace(/%s/g, () => String(values[index++])));
  };
  try {
    action();
    return warnings;
  } finally {
    console.error = original;
  }
};

/** @param {function():void} action */
DomHostBrowserTest.withShadyRoot = function(action) {
  const create = document.createElement;
  document.createElement = function(tag, options) {
    const element = create.call(document, tag, options);
    element['shadyRoot'] = {};
    return element;
  };
  try {
    action();
  } finally {
    document.createElement = create;
  }
};

/** @param {!Element} element @param {string} attributeName @param {function():void} action @return {number} */
DomHostBrowserTest.attributeWrites = function(element, attributeName, action) {
  const previous = element.setAttribute;
  let count = 0;
  element.setAttribute = function(name, value) {
    if (name === attributeName) count++;
    previous.call(element, name, value);
  };
  try {
    action();
    return count;
  } finally {
    delete element.setAttribute;
  }
};

/** @param {!Element} element @param {function():void} action @return {!Array<number>} */
DomHostBrowserTest.countAttributeMutations = function(element, action) {
  const set = element.setAttribute;
  const remove = element.removeAttribute;
  let writes = 0;
  let removals = 0;
  element.setAttribute = function(name, value) {
    writes++;
    set.call(element, name, value);
  };
  element.removeAttribute = function(name) {
    removals++;
    remove.call(element, name);
  };
  try {
    action();
    return [writes, removals];
  } finally {
    delete element.setAttribute;
    delete element.removeAttribute;
  }
};

/** @return {!Object<string, *>} */
DomHostBrowserTest.styleProxy = function() {
  const values = {display: 'none', fontFamily: 'Arial', lineHeight: 1.2};
  const style = {};
  for (const name of Object.keys(values)) {
    Object.defineProperty(style, name, {
      enumerable: true,
      get() { return values[name]; },
      set(value) { values[name] = value; },
    });
  }
  return style;
};

/** @param {!Object} style @return {boolean} */
DomHostBrowserTest.setStyleProperty = function(style) { return Reflect.set(style, 'position', 'absolute'); };

/** @param {!Element} element @param {string} name @return {string} */
DomHostBrowserTest.styleProperty = function(element, name) {
  return /** @type {!HTMLElement} */ (element).style.getPropertyValue(name);
};

/** @param {function():void} action @return {!Array<string>} */
DomHostBrowserTest.styleWrites = function(action) {
  const style = CSSStyleDeclaration.prototype;
  const previous = style.setProperty;
  const writes = [];
  style.setProperty = function(name, value, priority = '') {
    writes.push(name + '=' + value);
    previous.call(this, name, value, priority);
  };
  try {
    action();
    return writes;
  } finally {
    style.setProperty = previous;
  }
};

/** @param {!Element} element @param {string} name @param {function():void} render @return {number} */
DomHostBrowserTest.propertyWrites = function(element, name, render) {
  const descriptor = Object.getOwnPropertyDescriptor(element, name);
  let value = element[name];
  let writes = 0;
  Object.defineProperty(element, name, {
    configurable: true,
    get() { return value; },
    set(next) { value = next; writes++; }
  });
  try {
    render();
    return writes;
  } finally {
    if (descriptor) {
      Object.defineProperty(element, name, descriptor);
    } else {
      delete element[name];
    }
  }
};

/** @param {!Element} element @param {string} type */
DomHostBrowserTest.event = function(element, type) {
  element.dispatchEvent(new Event(type));
};

/** @param {function():void} action @return {string} */
DomHostBrowserTest.observeCreateOptions = function(action) {
  const create = document.createElement;
  let name = '';
  document.createElement = function(tag, options) {
    if (tag === 'div' && options) name = options['is'];
    return create.call(document, tag, options);
  };
  try {
    action();
    return name;
  } finally {
    document.createElement = create;
  }
};

/** @param {!Element} root @param {function():void} capture @param {function():void} bubble @param {function():void} click */
DomHostBrowserTest.documentClickOrder = function(root, capture, bubble, click) {
  const document = root.ownerDocument;
  document.addEventListener('click', capture, true);
  document.addEventListener('click', bubble);
  try {
    click();
  } finally {
    document.removeEventListener('click', capture, true);
    document.removeEventListener('click', bubble);
  }
};
