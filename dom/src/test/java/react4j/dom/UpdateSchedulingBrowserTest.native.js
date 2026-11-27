UpdateSchedulingBrowserTest.captureWarnings = function(action) {
  const messages = [];
  const previous = console.error;
  console.error = message => { messages.push(String(message)); };
  try {
    action();
    return messages;
  } finally {
    console.error = previous;
  }
};

UpdateSchedulingBrowserTest.dispatchInput = function(input, value) {
  Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set.call(input, value);
  input.dispatchEvent(new MouseEvent('input', {bubbles: true}));
};
