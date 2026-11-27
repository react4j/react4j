/** @param {function():void} throwError @return {*} */
Reconciler.nativeThrown = function(throwError) {
  try {
    throwError();
  } catch (error) {
    return error;
  }
};
