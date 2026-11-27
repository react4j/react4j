IsolatedSuspenseTest.suspend = function(pending) {
  throw pending;
};

IsolatedSuspenseTest.failure = function(message) {
  return new Error(message);
};
