'use strict';

module.exports = function createNoop({MemoryRenderer, clock, strictReplay, observe, unsupported, react}) {
  const defaultRoot = '<default>';
  const roots = new Map();
  const scheduled = new Map();
  const timers = new Map();
  let nextTask = 0;
  let timeOffset = 0;
  let now = () => clock() + timeOffset;
  let yielded = null;
  let shouldYield = () => false;
  const memory = new MemoryRenderer({
    now: () => now(),
    strictReplay,
    shouldYield: () => shouldYield(),
    schedule(task, delay) {
      const id = ++nextTask;
      if (delay === 0) scheduled.set(id, task);
      else {
        timers.set(id, setTimeout(() => {
          timers.delete(id);
          task();
        }, delay));
      }
      return id;
    },
    cancel(id) {
      scheduled.delete(id);
      if (timers.has(id)) {
        clearTimeout(timers.get(id));
        timers.delete(id);
      }
    },
  });

  function targetFor(id, concurrent) {
    if (!roots.has(id)) {
      const root = memory.createRoot(concurrent);
      const fixture = root.getContainer();
      fixture.rootID = id;
      roots.set(id, root);
    }
    return roots.get(id);
  }

  function getChildren(id = defaultRoot) {
    return roots.get(id)?.getChildren() ?? null;
  }

  function combine(children) {
    if (!children || children.length === 0) return null;
    if (children.length === 1) return children[0];
    return children.every(child => typeof child === 'string') ? children.join('') : children;
  }

  function toJSX(instance) {
    if (!instance.children) return instance.hidden ? '' : instance.text;
    const props = {prop: instance.prop};
    if (instance.hidden) props.hidden = true;
    const children = instance.text == null ? combine(instance.children.map(toJSX)) : instance.text;
    if (children !== null) props.children = children;
    return react.createElement(instance.type, props);
  }

  function submit(root, element, callback) {
    observe();
    root.render(element, callback || null);
  }

  function clearYields() {
    const result = yielded;
    yielded = null;
    return result;
  }

  function runNext(shouldPause) {
    const [id, task] = scheduled.entries().next().value;
    scheduled.delete(id);
    const previous = shouldYield;
    shouldYield = shouldPause;
    try {
      task();
    } finally {
      shouldYield = previous;
    }
  }

  function* flushAndYield(units = Infinity, drainAfterError = false) {
    let count = 0;
    let stopped = false;
    let firstFailure;
    while (!stopped && scheduled.size) {
      if (++count > 1000) unsupported('ReactNoop flush did not settle');
      let remaining = units;
      try {
        runNext(() => {
          if (yielded !== null) return true;
          if (remaining-- > 0) return false;
          stopped = true;
          return true;
        });
      } catch (error) {
        if (!drainAfterError) throw error;
        firstFailure ??= {error};
      }
      if (yielded !== null) yield clearYields();
    }
    if (firstFailure) throw firstFailure.error;
  }

  function flushUnitsOfWork(units) {
    const values = clearYields() || [];
    try {
      for (const batch of flushAndYield(units, units === Infinity)) values.push(...batch);
      return values;
    } catch (error) {
      // The caller can still inspect values yielded during a failed flush.
      yielded = values.concat(clearYields() || []);
      if (yielded.length === 0) yielded = null;
      throw error;
    }
  }

  function flush() {
    return flushUnitsOfWork(Infinity);
  }

  function flushTestRenderer(count) {
    clearYields();
    let turns = 0;
    while (scheduled.size && (yielded?.length ?? 0) < count) {
      if (++turns > 1000) unsupported('test renderer flush did not settle');
      runNext(() => (yielded?.length ?? 0) >= count);
    }
    return clearYields() || [];
  }

  const api = {
    render: (element, callback) => submit(targetFor(defaultRoot, true), element, callback),
    renderLegacySyncRoot: (element, callback) => submit(targetFor(defaultRoot, false), element, callback),
    renderToRootWithID: (element, id, callback) => submit(targetFor(id, true), element, callback),
    getOrCreateRootContainer: (id = defaultRoot, concurrent = false) => targetFor(id, concurrent).getContainer(),
    getChildren,
    getChildrenAsJSX(id) {
      const children = getChildren(id);
      const result = combine(children && children.map(toJSX));
      return Array.isArray(result) ? react.createElement(react.Fragment, {children: result}) : result;
    },
    unmountRootWithID(id) {
      const root = roots.get(id);
      if (!root) return;
      observe();
      root.unmount(() => roots.delete(id));
    },
    createPortal(children, fixture, key) {
      const root = roots.get(fixture.rootID);
      if (!root || root.getContainer() !== fixture) unsupported('ReactNoop portal container');
      observe();
      return memory.createPortal(children, root, key == null ? null : String(key));
    },
    batchedUpdates(action, ...args) {
      observe();
      return memory.batchedUpdates(() => action(...args));
    },
    interactiveUpdates(action, ...args) {
      observe();
      return memory.interactiveUpdates(() => action(...args));
    },
    hasScheduledCallback: () => scheduled.size !== 0,
    flush,
    flushAndYield: units => flushAndYield(units),
    flushUnitsOfWork,
    flushDeferredPri: (timeout = Infinity) => flushUnitsOfWork(timeout / 5 - 1),
    flushNextYield: () => flushAndYield().next().value || [],
    flushThrough(expected) {
      const actual = [];
      if (expected.length) {
        for (const batch of flushAndYield()) {
          actual.push(...batch);
          if (actual.length >= expected.length) break;
        }
      }
      expect(actual).toEqual(expected);
    },
    flushWithHostCounters() {
      memory.resetCounters();
      try {
        flush();
        const {hostDiffCounter, hostUpdateCounter} = memory.getCounters();
        return {hostDiffCounter, hostUpdateCounter};
      } finally {
        memory.resetCounters();
      }
    },
    advanceTime(ms) { timeOffset += ms; },
    yield(value) {
      if (yielded === null) yielded = [];
      yielded.push(value);
    },
    clearYields,
    testRenderer() {
      // Jest's test-renderer matchers consume JSON from committed host children.
      function toJSON(node) {
        if (node.suspended) return null;
        if (!node.children) return node.text;
        const {children, ...props} = node.props;
        const rendered = node.children.map(toJSON).filter(child => child !== null);
        return {type: node.type, props, children: rendered.length ? rendered : null};
      }
      const clear = () => clearYields() || [];
      return {
        unstable_yield: api.yield,
        unstable_clearYields: clear,
        unstable_setNowImplementation(implementation) { now = implementation; },
        create(element, options) {
          if (options?.createNodeMock) unsupported('test renderer createNodeMock');
          const root = memory.createTestRoot(Boolean(options?.unstable_isConcurrent));
          const update = tree => submit(root, tree);
          update(element);
          return {update, unstable_flushAll: () => flushTestRenderer(Infinity), unstable_clearYields: clear,
            unstable_flushNumberOfYields: flushTestRenderer,
            toJSON() {
              const children = root.getChildren().map(toJSON).filter(child => child !== null);
              return children.length > 1 ? children : children[0] ?? null;
            },
          };
        },
      };
    },
  };
  for (const operation of ['expire', 'flushExpired', 'deferredUpdates',
    'unbatchedUpdates', 'flushSync', 'dumpTree', 'findInstance']) {
    api[operation] = () => unsupported('ReactNoop.' + operation);
  }
  return api;
};
