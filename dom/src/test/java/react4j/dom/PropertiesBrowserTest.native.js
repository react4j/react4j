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

/** @return {!Array<!Function>} */
PropertiesBrowserTest.overlays = function() {
  return [anchorProps, audioProps, btnProps, colProps, cssProps, formProps, htmlGlobalFields, htmlProps, iFrameProps, imgProps, inputProps, labelProps, optGroupProps, optionProps, selectProps, sourceProps, tdProps, textAreaProps, thProps];
};
/** @param {!Function} overlay @return {!Array<string>} */
PropertiesBrowserTest.methods = function(overlay) { return Object.getOwnPropertyNames(overlay); };
/** @param {!Function} overlay @param {string} method @param {!Object} value @param {!Array<*>} args @return {*} */
PropertiesBrowserTest.invoke = function(overlay, method, value, args) { return overlay[method](value, ...args); };
