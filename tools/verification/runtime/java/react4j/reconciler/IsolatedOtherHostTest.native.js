IsolatedOtherHostTest.countArguments = function(observe) {
  return function(...args) { observe(args.length); };
};
