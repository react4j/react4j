OtherHostBrowserTest.event = function(target, name) {
  target.dispatchEvent(new Event(name, {bubbles: true}));
};

OtherHostBrowserTest.callbackReceiver = function(capture) {
  return function() { capture(this); };
};

OtherHostBrowserTest.writes = function(target, action) {
  const observer = new MutationObserver(() => {});
  observer.observe(target, {attributes: true, childList: true, characterData: true, subtree: true});
  try {
    action();
    return observer.takeRecords().map(record =>
        record.type === 'attributes' ? 'attributes:' + record.attributeName : record.type);
  } finally {
    observer.disconnect();
  }
};
