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

/** @param {!Element} root @param {string} name @param {function(!Object,!Array<!Function>):void} handler */
EventsBrowserTest.dispatch = function(root, name, handler) {
  const mouseOptions = {bubbles: true, cancelable: true, altKey: true, ctrlKey: false,
    shiftKey: true, metaKey: false, button: 1, buttons: 2, clientX: 11, clientY: 17,
    screenX: 23, screenY: 29, detail: 3, view: window};
  const transfer = new DataTransfer();
  const cases = [
    ['onClick', () => new window.MouseEvent('click', mouseOptions), [mouseEvent, uIEvent]],
    ['onKeyDown', () => new window.KeyboardEvent('keydown', {bubbles: true, cancelable: true,
      key: 'Enter', keyCode: 13, charCode: 17, location: 2, repeat: true, altKey: true}), [keyboardEvent]],
    ['onFocus', () => new window.FocusEvent('focus', {bubbles: true, relatedTarget: document.body}), [focusEvent]],
    ['onCopy', () => new window.ClipboardEvent('copy', {bubbles: true, cancelable: true, clipboardData: transfer}), [clipboardEvent]],
    ['onCompositionEnd', () => new window.CompositionEvent('compositionend', {bubbles: true, data: 'typed'}), [compositionEvent]],
    ['onDragStart', () => new window.DragEvent('dragstart', Object.assign({}, mouseOptions, {dataTransfer: transfer})), [dragEvent, mouseEvent]],
    ['onWheel', () => new window.WheelEvent('wheel', Object.assign({}, mouseOptions, {deltaX: 11, deltaY: 17, deltaZ: 23, deltaMode: 1})), [wheelEvent, mouseEvent, uIEvent]],
    ['onTouchStart', () => new window.TouchEvent('touchstart', {bubbles: true, touches: [], targetTouches: [], changedTouches: [], altKey: true}), [touchEvent, uIEvent]],
    ['onScroll', () => new window.UIEvent('scroll', {bubbles: true, detail: 7, view: window}), [uIEvent]],
  ];
  const item = cases.find(item => item[0] === name);
  const props = {};
  props[name] = event => handler(event, [syntheticEvent, ...item[2]]);
  window['ReactDOM'].render(window['React'].createElement('button', props, 'events'), root);
  root.firstElementChild.dispatchEvent(item[1]());
};
/** @param {function(!Function,!Object):void} inspector */
EventsBrowserTest.inspectWindow = function(inspector) {
  inspector(abstractView, window);
  if (window.styleMedia) inspector(styleMedia, window.styleMedia);
};
/** @param {!Function} overlay @return {!Array<string>} */
EventsBrowserTest.methods = function(overlay) { return Object.getOwnPropertyNames(overlay); };
/** @param {!Function} overlay @param {string} method @param {!Object} value @return {*} */
EventsBrowserTest.invoke = function(overlay, method, value) { return overlay[method](value); };
/** @param {!Element} root */
EventsBrowserTest.unmount = function(root) { window['ReactDOM'].unmountComponentAtNode(root); };
