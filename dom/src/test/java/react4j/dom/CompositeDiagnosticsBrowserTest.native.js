CompositeDiagnosticsBrowserTest.observeArguments = function(observer) {
  return function() {
    observer(arguments.length);
  };
};
