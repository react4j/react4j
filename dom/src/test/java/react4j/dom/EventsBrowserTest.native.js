const abstractView = goog.require('react4j.dom.events.AbstractView.$Overlay');
const clipboardEvent = goog.require('react4j.dom.events.ClipboardEvent.$Overlay');
const compositionEvent = goog.require('react4j.dom.events.CompositionEvent.$Overlay');
const dragEvent = goog.require('react4j.dom.events.DragEvent.$Overlay');
const focusEvent = goog.require('react4j.dom.events.FocusEvent.$Overlay');
const keyboardEvent = goog.require('react4j.dom.events.KeyboardEvent.$Overlay');
const mouseEvent = goog.require('react4j.dom.events.MouseEvent.$Overlay');
const styleMedia = goog.require('react4j.dom.events.StyleMedia.$Overlay');
const syntheticEvent = goog.require('react4j.dom.events.SyntheticEvent.$Overlay');
const touchEvent = goog.require('react4j.dom.events.TouchEvent.$Overlay');
const uIEvent = goog.require('react4j.dom.events.UIEvent.$Overlay');
const wheelEvent = goog.require('react4j.dom.events.WheelEvent.$Overlay');

/** @param {!Element} target @param {string} name */
EventsBrowserTest.dispatch = function(target, name) {
  const mouseOptions = {bubbles: true, cancelable: true, altKey: true, ctrlKey: false,
    shiftKey: true, metaKey: false, button: 1, buttons: 2, clientX: 11, clientY: 17,
    screenX: 23, screenY: 29, detail: 3, view: window};
  const transfer = new DataTransfer();
  const events = {
    'onClick': () => new window.MouseEvent('click', mouseOptions),
    'onKeyDown': () => new window.KeyboardEvent('keydown', {bubbles: true, cancelable: true,
      key: 'Enter', keyCode: 13, charCode: 17, location: 2, repeat: true, altKey: true}),
    'onLegacyKeyDown': () => new window.KeyboardEvent('keydown', {bubbles: true, key: 'Left', keyCode: 37, charCode: 17}),
    'onFocus': () => new window.FocusEvent('focus', {relatedTarget: document.body}),
    'onCopy': () => new window.ClipboardEvent('copy', {bubbles: true, cancelable: true, clipboardData: transfer}),
    'onCompositionStart': () => new window.CompositionEvent('compositionstart', {bubbles: true, data: 't'}),
    'onCompositionUpdate': () => new window.CompositionEvent('compositionupdate', {bubbles: true, data: 'ty'}),
    'onCustomCompositionEnd': () => new CustomEvent('compositionend', {bubbles: true, detail: {'data': 'custom'}}),
    'onNativeBeforeInput': () => new InputEvent('beforeinput', {bubbles: true, data: 'native'}),
    'onTextInput': () => new InputEvent('textInput', {bubbles: true, data: 'typed'}),
    'onSpaceTextInput': () => new InputEvent('textInput', {bubbles: true, data: ' '}),
    'onSpaceKeyPress': () => new KeyboardEvent('keypress', {bubbles: true, charCode: 32, which: 32}),
    'onCompositionEnd': () => new window.CompositionEvent('compositionend', {bubbles: true, data: 'typed'}),
    'onDragStart': () => new window.DragEvent('dragstart', Object.assign({}, mouseOptions, {dataTransfer: transfer})),
    'onWheel': () => new window.WheelEvent('wheel', Object.assign({}, mouseOptions, {deltaX: 11, deltaY: 17, deltaZ: 23, deltaMode: 1})),
    'onTouchStart': () => new window.TouchEvent('touchstart', {bubbles: true, touches: [], targetTouches: [], changedTouches: [], altKey: true}),
    'onAnimationEnd': () => new AnimationEvent('animationend', {bubbles: true, animationName: 'fade'}),
    'onTransitionEnd': () => new TransitionEvent('transitionend', {bubbles: true, propertyName: 'opacity', elapsedTime: 0.25, pseudoElement: ''}),
    'onCancel': () => new Event('cancel'),
    'onClose': () => new Event('close'),
    'onBlur': () => new FocusEvent('blur'),
    'onScroll': () => new window.UIEvent('scroll', {detail: 7, view: window}),
  };
  target.dispatchEvent(events[name]());
};
/** @param {string} name @return {!Array<!Function>} */
EventsBrowserTest.eventOverlays = function(name) {
  const overlays = {
    'onClick': [mouseEvent, uIEvent],
    'onKeyDown': [keyboardEvent],
    'onFocus': [focusEvent],
    'onCopy': [clipboardEvent],
    'onCompositionEnd': [compositionEvent],
    'onDragStart': [dragEvent, mouseEvent],
    'onWheel': [wheelEvent, mouseEvent, uIEvent],
    'onTouchStart': [touchEvent, uIEvent],
    'onScroll': [uIEvent],
  };
  return [syntheticEvent, ...overlays[name]];
};
/** @param {function(!Function,!Object):void} inspector */
EventsBrowserTest.inspectWindow = function(inspector) {
  inspector(abstractView, window);
  if (window.styleMedia) inspector(styleMedia, window.styleMedia);
};
/** @param {*} overlay @return {!Array<string>} */
EventsBrowserTest.methods = function(overlay) { return Object.getOwnPropertyNames(/** @type {!Object} */ (overlay)); };
/** @param {*} overlay @param {string} method @param {*} value @return {*} */
EventsBrowserTest.invoke = function(overlay, method, value) { return overlay[method](value); };

/** @param {!Element} element @param {string} value */
EventsBrowserTest.writeValue = function(element, value) {
  /** @type {!HTMLTextAreaElement} */ (element).value = value;
  element.dispatchEvent(new Event('input', {bubbles: true}));
};

/** @param {!Element} element @return {boolean} */
EventsBrowserTest.isFocused = function(element) { return element.ownerDocument.activeElement === element; };

/** @param {!Element} element @param {number} start @param {number} end */
EventsBrowserTest.setSelection = function(element, start, end) {
  /** @type {!HTMLInputElement} */ (element).setSelectionRange(start, end);
  element.dispatchEvent(new KeyboardEvent('keyup', {bubbles: true, key: 'ArrowLeft'}));
};

/** @param {!Element} element @param {string} namespace @param {string} name @return {?string} */
EventsBrowserTest.attributeIn = function(element, namespace, name) { return element.getAttributeNS(namespace, name); };

/** @param {!Element} element @param {string} name @return {string} */
EventsBrowserTest.styleProperty = function(element, name) {
  return /** @type {!HTMLElement} */ (element).style.getPropertyValue(name);
};

/** @param {!Element} input */
EventsBrowserTest.focus = function(input) { /** @type {!HTMLElement} */ (input).focus(); };

/** @param {!Element} input */
EventsBrowserTest.blur = function(input) { /** @type {!HTMLElement} */ (input).blur(); };

/** @param {!Element} element @param {string} type @param {?Element} related */
EventsBrowserTest.movement = function(element, type, related) {
  const event = type.startsWith('pointer') ?
      new PointerEvent(type, {bubbles: true, relatedTarget: related, pointerType: 'pen'}) :
      new MouseEvent(type, {bubbles: true, relatedTarget: related});
  element.dispatchEvent(event);
};


/** @param {function():void} probe @return {number} */
EventsBrowserTest.releasedWarnings = function(probe) {
  const original = console.error;
  let warnings = 0;
  console.error = function(...args) {
    if (String(args[0]).includes('on a released/nullified synthetic event')) {
      warnings++;
    } else {
      original.apply(console, args);
    }
  };
  try {
    probe();
    return warnings;
  } finally {
    console.error = original;
  }
};

/** @param {!Element} element @param {string} type @param {string} key @param {number} keyCode @param {number} charCode */
EventsBrowserTest.keyboard = function(element, type, key, keyCode, charCode) {
  element.dispatchEvent(new KeyboardEvent(type, {bubbles: true, key, keyCode, charCode, shiftKey: true}));
};

/** @param {!Element} element @param {string} type @param {number} keyCode */
EventsBrowserTest.legacyKey = function(element, type, keyCode) {
  const event = new Event(type, {bubbles: true});
  Object.assign(event, {'keyCode': keyCode, 'altKey': true, 'ctrlKey': true, 'metaKey': true, 'shiftKey': true});
  element.dispatchEvent(event);
};


/** @param {!Element} element @param {number} button */
EventsBrowserTest.mouseButton = function(element, button) {
  element.dispatchEvent(new MouseEvent('click', {bubbles: true, button}));
};

/** @param {!Element} element */
EventsBrowserTest.textClick = function(element) {
  element.firstChild.dispatchEvent(new MouseEvent('click', {bubbles: true}));
};

/** @param {!Element} element */
EventsBrowserTest.editableSelection = function(element) {
  const selection = element.ownerDocument.getSelection();
  selection.collapse(element.firstChild, 2);
  selection.extend(element.firstChild, 4);
  element.dispatchEvent(new KeyboardEvent('keyup', {bubbles: true}));
};


/** @param {function():void} mount */
EventsBrowserTest.withoutTextInput = function(mount) {
  const previous = Object.getOwnPropertyDescriptor(window, 'TextEvent');
  delete window['TextEvent'];
  try {
    mount();
  } finally {
    if (previous) Object.defineProperty(window, 'TextEvent', previous);
  }
};

/** @param {!Element} input @param {number} which @param {boolean} ctrl @param {boolean} alt
 * @param {boolean} meta @param {?string} chars */
EventsBrowserTest.fallbackKeyPress = function(input, which, ctrl, alt, meta, chars) {
  const ev = new Event('keypress', {bubbles: true});
  Object.assign(ev, {'which': which, 'ctrlKey': ctrl, 'altKey': alt, 'metaKey': meta, 'char': chars});
  input.dispatchEvent(ev);
};
