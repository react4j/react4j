goog.module('react4j.css.integration.LivenessLink');
const Entry = goog.require('react4j.css.integration.LivenessEntry');
/** @return {string} */
window['cssClasses'] = () => Entry.classes();
