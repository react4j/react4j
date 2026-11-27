IsolatedErrorHandlingTest.raise = function(error) {
  throw error;
};

IsolatedErrorHandlingTest.thrownBy = function(task) {
  try {
    task();
  } catch (error) {
    return error;
  }
  return null;
};
