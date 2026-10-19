/** @param {string} tag @return {!Element} */
Browser.append = function(tag) {
  const element = document.createElement(tag);
  document.body.appendChild(element);
  return element;
};
/** @param {number} milliseconds @return {!Promise<void>} */
Browser.delay = function(milliseconds) {
  return new Promise(resolve => setTimeout(resolve, milliseconds));
};
/** @param {*} value @return {string} */
Browser.json = function(value) { return JSON.stringify(value); };
