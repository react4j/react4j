/** @param {function(function():void):void} reject @param {function():void} done @return {!Promise<?>} */
SubscriptionBrowserTest.promiseRejection = function(reject, done) {
  return new Promise((resolve, token) => reject(() => token(new Error('rejected')))).catch(done);
};
