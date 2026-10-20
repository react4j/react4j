goog.module('react4j.browser.Application');
const AssetsExample = goog.require('react4j.css.example.AssetsExample');
const react4j = goog.require('react4j');

/** @define {string} */
const text = goog.define('react4j.browser.text', 'default');
/** @define {boolean} */
const flag = goog.define('react4j.browser.flag', false);
/** @define {number} */
const numericValue = goog.define('react4j.browser.number', 0);

AssetsExample.render(document.getElementById('app'));
window['applicationSettings'] = {
  'text': text,
  'flag': flag,
  'number': numericValue,
  'debug': goog.DEBUG,
  'environment': react4j.environment,
  'viewNames': react4j.enable_component_names,
};
