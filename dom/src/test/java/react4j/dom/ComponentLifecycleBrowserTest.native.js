ComponentLifecycleBrowserTest.captureWarnings = function(action) {
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
