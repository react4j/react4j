/** @param {function():void} render @return {!Array<string>} */
OriginalHostCasesBrowserTest.inputAttributeTrace = function(render) {
  const createElement = document.createElement;
  const observer = new MutationObserver(() => {});
  document.createElement = function(name, options) {
    const element = createElement.call(this, name, options);
    if (name === 'input') observer.observe(element, {attributes: true});
    return element;
  };
  try {
    render();
    return observer.takeRecords().map(record => record.attributeName);
  } finally {
    observer.disconnect();
    document.createElement = createElement;
  }
};

/** @param {function():void} render @return {!Array<string>} */
OriginalHostCasesBrowserTest.captureWarnings = function(render) {
  const previous = console.error;
  const warnings = [];
  console.error = function(format, ...values) {
    let index = 0;
    warnings.push(String(format).replace(/%s/g, () => String(values[index++])));
  };
  try {
    render();
    return warnings;
  } finally {
    console.error = previous;
  }
};
