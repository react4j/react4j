'use strict';

const assert = require('node:assert/strict');
const fs = require('node:fs');

assert.equal(typeof document, 'undefined');
const source = fs.readFileSync(process.argv[2], 'utf8');
for (const mode of ['development', 'production']) {
  const scope = Object.create(globalThis);
  scope.globalThis = scope;
  scope.goog = {};
  scope.CLOSURE_DEFINES = {'react4j.environment': mode, 'react4j.check_invariants': 'false'};
  const load = new Function('scope', 'with (scope) { return function(goog) {' +
    source + '\nreturn name => goog.module.get(name); }; }');
  const moduleFor = load(scope).call(scope, scope.goog);
  assert.ok(!moduleFor('react4j.dom.DomHost'));
  const cases = moduleFor('react4j.testing.IsolatedCases');
  console.log(mode + ': ' + cases.run(mode) + ' isolated reconciler cases passed');
}
