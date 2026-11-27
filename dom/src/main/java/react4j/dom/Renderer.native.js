/** @param {function():void} callback @param {*} instance */
Renderer.invokeCallback = function(callback, instance) {
  callback.call(instance);
};

const unmountKey = Symbol.for('react4j.dom.unmount');

/** @param {!Node} container @param {function():boolean} unmount */
Renderer.markRoot = function(container, unmount) {
  container[unmountKey] = {'owner': Renderer, 'unmount': unmount};
};

/** @param {!Node} container @return {?function():boolean} */
Renderer.rootUnmount = function(container) {
  const registration = container[unmountKey];
  return registration ? registration['unmount'] : null;
};

/** @param {!Node} container */
Renderer.unmarkRoot = function(container) {
  const registration = container[unmountKey];
  if (registration && registration['owner'] === Renderer) {
    delete container[unmountKey];
  }
};
