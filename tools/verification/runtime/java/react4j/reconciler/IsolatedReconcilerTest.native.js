IsolatedReconcilerTest.iterable = function(elements) {
  return new Set(elements);
};

IsolatedReconcilerTest.captureWarnings = function(action) {
  const original = console.error;
  const warnings = [];
  console.error = (...args) => { warnings.push(args.join(' ')); };
  try {
    action();
  } finally {
    console.error = original;
  }
  return warnings.join('\u001e');
};
