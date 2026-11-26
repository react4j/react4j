/** @param {function():void} throwError @return {*} */
Renderer.nativeThrown = function(throwError) {
  try {
    throwError();
  } catch (error) {
    return error;
  }
};

/** @param {function():void} callback @param {*} instance */
Renderer.invokeCallback = function(callback, instance) {
  callback.call(instance);
};
