IsolatedErrorLoggingTest.observeLogs = function(action, log) {
  const original = console.error;
  console.error = log;
  try {
    action();
  } finally {
    console.error = original;
  }
};

IsolatedErrorLoggingTest.thrownBy = function(action) {
  try {
    action();
  } catch (error) {
    return error;
  }
  return null;
};

IsolatedErrorLoggingTest.failWith = function(message) {
  throw new Error(message);
};
