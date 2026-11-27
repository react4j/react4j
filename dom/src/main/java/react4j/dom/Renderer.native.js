/** @param {function():void} callback @param {*} instance */
Renderer.invokeCallback = function(callback, instance) {
  callback.call(instance);
};
