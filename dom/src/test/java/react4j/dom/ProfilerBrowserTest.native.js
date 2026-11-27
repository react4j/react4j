/** @param {function(!Array<*>):void} receive @return {function(...*):void} */
ProfilerBrowserTest.captureProfile = function(receive) {
  return (...args) => receive(args);
};
