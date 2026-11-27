IsolatedStrictClassTest.captureLowPriorityWarnings = function(action) {
  const previous = console.warn;
  const warnings = [];
  console.warn = message => { warnings.push(String(message)); };
  try {
    action();
  } finally {
    console.warn = previous;
  }
  return warnings.join('\u001e');
};
