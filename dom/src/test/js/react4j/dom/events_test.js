goog.module('react4j.dom.EventsTest');
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

/**
 * @param {boolean} condition
 * @param {string} message
 */
function check(condition, message) {
  if (!condition) throw new Error(message);
}

/**
 * @param {!Function} overlay
 * @param {!Object} value
 */
function getters(overlay, value) {
  let count = 0;
  for (const method of Object.getOwnPropertyNames(overlay)) {
    if (!method.startsWith('m_') || !method.includes('__$devirt__')) continue;
    const name = method.split('__')[0].substring(2);
    let property;
    if ('isTrusted' === name) property = name;
    else if (name.startsWith('get')) property = name.substring(3);
    else if (name.startsWith('is')) property = name.substring(2);
    else throw new Error('Unknown event accessor ' + method);
    property = property[0].toLowerCase() + property.substring(1);
    check(overlay[method](value) === value[property], 'Native event property: ' + method);
    count++;
  }
  check(count > 0, 'No event accessors');
}

/** Verifies Java event accessors inside real React event dispatch. */
function run() {
  const react = window['React'];
  const dom = window['ReactDOM'];
  const root = document.createElement('div');
  document.body.appendChild(root);
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
  try {
    for (const [name, event, overlays] of cases) {
      let received = 0;
      const props = {};
      props[name] = e => {
        received++;
        getters(syntheticEvent, e);
        for (const overlay of overlays) getters(overlay, e);
        check(false === e.isDefaultPrevented(), 'Initial default state');
        e.preventDefault();
        check(true === e.isDefaultPrevented(), 'Default prevention');
        e.stopPropagation();
        check(true === e.isPropagationStopped(), 'Propagation stop');
        e.persist();
      };
      dom.render(react.createElement('button', props, 'events'), root);
      root.firstElementChild.dispatchEvent(event());
      check(1 === received, name + ' was not dispatched');
    }
    getters(abstractView, window);
    if (window.styleMedia) getters(styleMedia, window.styleMedia);
  } finally {
    dom.unmountComponentAtNode(root);
    root.remove();
  }
}
exports = run;
