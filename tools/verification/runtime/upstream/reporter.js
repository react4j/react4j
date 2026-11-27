// Compatible with the Node 10 used by stock React 16.6.
'use strict';
const fs = require('fs');
const path = require('path');
const counters = {calls: 0, adaptations: new Set()};
require(path.join(process.cwd(), 'scripts/jest/setupTests.js'));

let assertions = 0;
let uncheckedWarnings = 0;
if (process.env.NODE_ENV === 'production') {
  const matchers = require(path.join(process.cwd(), 'scripts/jest/matchers/toWarnDev'));
  for (const [name, matcher] of Object.entries(matchers)) {
    expect.extend({[name](...args) {
      const result = matcher.apply(this, args);
      uncheckedWarnings++;
      return result;
    }});
  }
}

afterEach(() => { assertions = expect.getState().assertionCalls - uncheckedWarnings; });
function record(event) {
  fs.appendFileSync(process.env.REACT4J_CASE_LOG, JSON.stringify(event) + '\n');
}
jasmine.getEnv().addReporter({
  specStarted(result) {
    counters.calls = 0;
    counters.adaptations.clear();
    assertions = 0;
    uncheckedWarnings = 0;
    record({start:result.fullName, id:result.id});
  },
  specDone(result) {
    record({id:result.id, name:result.fullName, status:result.status, calls:counters.calls,
      assertions, adaptations:Array.from(counters.adaptations),
      failure:result.failedExpectations.map(f => f.message).join('\n').slice(0, 2000)});
  },
});
module.exports = counters;
