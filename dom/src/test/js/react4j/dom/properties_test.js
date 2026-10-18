goog.module('react4j.dom.PropertiesTest');
const anchorProps = goog.require('react4j.dom.proptypes.html.AnchorProps.$Overlay');
const audioProps = goog.require('react4j.dom.proptypes.html.AudioProps.$Overlay');
const btnProps = goog.require('react4j.dom.proptypes.html.BtnProps.$Overlay');
const colProps = goog.require('react4j.dom.proptypes.html.ColProps.$Overlay');
const cssProps = goog.require('react4j.dom.proptypes.html.CssProps.$Overlay');
const formProps = goog.require('react4j.dom.proptypes.html.FormProps.$Overlay');
const htmlGlobalFields = goog.require('react4j.dom.proptypes.html.HtmlGlobalFields.$Overlay');
const htmlProps = goog.require('react4j.dom.proptypes.html.HtmlProps.$Overlay');
const iFrameProps = goog.require('react4j.dom.proptypes.html.IFrameProps.$Overlay');
const imgProps = goog.require('react4j.dom.proptypes.html.ImgProps.$Overlay');
const inputProps = goog.require('react4j.dom.proptypes.html.InputProps.$Overlay');
const labelProps = goog.require('react4j.dom.proptypes.html.LabelProps.$Overlay');
const optGroupProps = goog.require('react4j.dom.proptypes.html.OptGroupProps.$Overlay');
const optionProps = goog.require('react4j.dom.proptypes.html.OptionProps.$Overlay');
const selectProps = goog.require('react4j.dom.proptypes.html.SelectProps.$Overlay');
const sourceProps = goog.require('react4j.dom.proptypes.html.SourceProps.$Overlay');
const tdProps = goog.require('react4j.dom.proptypes.html.TdProps.$Overlay');
const textAreaProps = goog.require('react4j.dom.proptypes.html.TextAreaProps.$Overlay');
const thProps = goog.require('react4j.dom.proptypes.html.ThProps.$Overlay');

/**
 * @param {boolean} condition
 * @param {string} message
 */
function check(condition, message) {
  if (!condition) throw new Error(message);
}

/**
 * @param {string} type
 * @return {*}
 */
function sample(type) {
  if ('java_lang_String' === type) return 'sample';
  if ('boolean' === type) return true;
  if ('int' === type || 'double' === type || 'float' === type) return 13;
  if (type.startsWith('arrayOf_')) return ['first', 'second'];
  if (type.endsWith('Handler') || type.endsWith('RefConsumer')) return () => {};
  if ('jsinterop_base_Any' === type) return 17;
  if ('react4j_ReactNode' === type) return 'child';
  if ('react4j_dom_proptypes_html_CssProps' === type) return {};
  throw new Error('Unhandled fluent parameter ' + type);
}

/**
 * @param {!Function} overlay
 * @return {number}
 */
function roundtrips(overlay) {
  let count = 0;
  for (const method of Object.getOwnPropertyNames(overlay)) {
    if (!method.startsWith('m_') || !method.includes('__$devirt__')) continue;
    const signature = method.split('__');
    const name = signature[0].substring(2);
    const types = signature.slice(3, -1);
    const object = {};
    let args = types.map(sample);
    let property = name;
    let expected = args.length ? args[0] : true;
    if ('self' === name) property = '';
    else if ('className' === name) expected = 'first second';
    else if ('dangerouslySetInnerHTML' === name) expected = {'__html': 'sample'};
    else if ('key' === name || (('height' === name || 'width' === name) && 'int' === types[0])) expected = String(expected);
    else if ('from' === name) property = 'form';
    else if ('preLoad' === name) property = 'preload';
    else if ('placeHolder' === name) property = 'placeholder';
    else if ('readonly' === name) property = 'readOnly';
    else if ('cssFloat' === name) property = 'float';
    else if ('webkitMask' === name) property = 'WebkitMask';
    else if ('webkitMaskSize' === name) property = 'WebkitMaskSize';
    else if ('data' === name) { args = ['shared', 'true']; property = 'data-shared'; expected = 'true'; }
    else if ('customProperty' === name) { args = ['--accent', '#04c']; property = '--accent'; expected = '#04c'; }
    else if ('prop' === name) { args = ['data-extra', 17]; property = 'data-extra'; expected = 17; }
    const result = overlay[method](object, ...args);
    check(result === object, 'Fluent identity: ' + method);
    if (property) {
      if ('dangerouslySetInnerHTML' === name) {
        check(object[property]['__html'] === expected['__html'], method);
      } else {
        check(object[property] === expected, 'Native property ' + property + ': ' + method);
      }
    }
    if (1 === types.length && 'boolean' === types[0]) {
      overlay[method](object, false);
      check(false === object[property], 'False update: ' + method);
    }
    count++;
  }
  check(count > 0, 'No fluent API methods discovered');
  return count;
}

/** Exercises every authored fluent method against native JavaScript property bags. */
function run() {
  let count = 0;
  for (const overlay of [anchorProps, audioProps, btnProps, colProps, cssProps, formProps, htmlGlobalFields, htmlProps, iFrameProps, imgProps, inputProps, labelProps, optGroupProps, optionProps, selectProps, sourceProps, tdProps, textAreaProps, thProps]) {
    count += roundtrips(overlay);
  }
  check(count > 500, 'Incomplete property API exercise: ' + count);
}
exports = run;
