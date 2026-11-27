'use strict';

const assert = require('assert').strict;
const {readFileSync} = require('fs');

assert.equal(typeof document, 'undefined');
const source = readFileSync(process.argv[2], 'utf8');
const scope = Object.create(globalThis);
scope.globalThis = scope;
scope.goog = {};
scope.CLOSURE_DEFINES = {'react4j.environment': 'production', 'react4j.check_invariants': 'false'};
const load = new Function('scope', 'with (scope) { return function(goog) {' +
  source + '\nreturn name => goog.module.get(name); }; }');
const moduleFor = load(scope).call(scope, scope.goog);
assert.ok(!moduleFor('react4j.dom.DomHost'));
const MemoryRenderer = moduleFor('react4j.testing.MemoryRenderer');
const tasks = new Map();
let lastId = 0;
let stop = false;
const api = new MemoryRenderer({
  now: () => 0, shouldYield: () => stop,
  strictReplay: () => false,
  schedule(task, delay) { assert.equal(delay, 0); tasks.set(++lastId, task); return lastId; },
  cancel(id) { tasks.delete(id); },
});
function turn() {
  const [id, task] = tasks.entries().next().value;
  tasks.delete(id);
  task();
}
function flush() {
  for (let turns = 0; tasks.size; turns++) {
    assert.ok(turns < 100, 'scheduled work should settle');
    turn();
  }
}
const el = (type, key, props) => ({$$typeof: Symbol.for('react.element'), type, key, ref: null, props});
const root = api.createRoot(false);
const target = api.createRoot(false);
const first = el('item', 'a', {children: 'Hello'});
const second = el('item', 'b', {hidden: true, prop: 2});
const portal = api.createPortal(el('portal', null, {children: 'Elsewhere'}), target, 'portal');
let commits = 0;
root.render([first, second, portal], () => commits++);
assert.equal(commits, 1);
const records = root.getChildren();
assert.deepEqual(records, [
  {type: 'item', children: [], prop: undefined, hidden: false},
  {type: 'item', children: [], prop: 2, hidden: true},
]);
const [a, b] = records;
assert.equal(a.text, 'Hello');
assert.equal(a.parent, root.getContainer());
assert.equal(target.getChildren()[0].text, 'Elsewhere');
api.resetCounters();
root.render([second, el('item', 'a', {children: [42, el('leaf', null, {})]})], null);
assert.equal(records, root.getChildren());
assert.equal(records[0], b);
assert.equal(records[1], a);
assert.equal(a.text, null);
assert.equal(a.children[0].text, '42');
assert.equal(a.children[0].parent, a);
assert.equal(target.getChildren().length, 0);
assert.deepEqual(api.getCounters(), {hostDiffCounter: 1, hostUpdateCounter: 1});
root.unmount(null);
assert.equal(records.length, 0);
assert.equal(a.parent, null);
const async = api.createRoot(true);
async.render([first, second], () => commits++);
assert.equal(async.getChildren().length, 0);
stop = true;
turn();
assert.equal(async.getChildren().length, 0);
assert.equal(commits, 1);
assert.equal(tasks.size, 1);
stop = false;
flush();
assert.equal(async.getChildren()[0].text, 'Hello');
assert.equal(commits, 2);
async.unmount(() => commits++);
assert.equal(async.getChildren().length, 2);
flush();
assert.equal(async.getChildren().length, 0);
assert.equal(commits, 3);

const test = api.createTestRoot(false);
test.render(first, null);
assert.equal(test.getChildren()[0].children[0].text, 'Hello');
const HostFixture = moduleFor('react4j.testing.HostFixture');
const rootContainer = {};
const rootContext = {};
const childContext = {};
const hostEvents = [];
const fixture = new HostFixture({
  getRootHostContext(container) {
    assert.equal(container, rootContainer);
    return rootContext;
  },
  getChildHostContext(context, type, container) {
    assert.equal(container, rootContainer);
    assert.equal(context, type === 'outer' ? rootContext : childContext);
    return childContext;
  },
  shouldSetTextContent: () => false,
  createInstance(type, props, container, context) {
    assert.equal(container, rootContainer);
    assert.equal(context, type === 'outer' ? rootContext : childContext);
    return type === 'outer' ? undefined : null;
  },
  finalizeInitialChildren: () => null,
  appendInitialChild: (parent, child) => hostEvents.push([parent, child]),
  prepareForCommit: container => hostEvents.push(['prepare', container]),
  appendChildToContainer: (container, child) => hostEvents.push([container, child]),
  resetAfterCommit: container => hostEvents.push(['reset', container]),
  now: () => 0,
});
const hostRoot = fixture.createContainer(rootContainer);
fixture.updateContainer(el('outer', null, {children: el('inner', null, {})}), hostRoot, null);
assert.deepEqual(hostEvents, [
  [undefined, null], ['prepare', rootContainer], [rootContainer, undefined], ['reset', rootContainer],
]);
console.log('MemoryRenderer smoke passed without a browser DOM');
