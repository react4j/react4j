ErrorBoundariesBrowserTest.fail = function(message) {
  throw new Error(message);
};
ErrorBoundariesBrowserTest.newError = function(message) {
  return new Error(message);
};

ErrorBoundariesBrowserTest.throwError = function(error) {
  throw error;
};

ErrorBoundariesBrowserTest.captureWarnings = function(action) {
  const original = console.error;
  const warnings = [];
  console.error = (...args) => { warnings.push(args.join(' ')); };
  try {
    action();
  } finally {
    console.error = original;
  }
  return warnings.join('\n');
};

ErrorBoundariesBrowserTest.caughtMessage = function(action) {
  try {
    action();
  } catch (error) {
    return /** @type {!Error} */ (error).message;
  }
  throw new Error('Expected a renderer error');
};
