'use strict';

module.exports = function createNoop(
  {MemoryRenderer, strictReplay, replayOutsideStrictMode, replayFailedRender, userTimingEnabled, registerReflection,
    profilerEnabled, profilerInteractions, observe, unsupported, react},
  persistent = false,
) {
  const defaultRoot = '<default>';
  const roots = new Map();
  const scheduled = new Map();
  const timers = new Map();
  let nextTask = 0;
  let timeOffset = 0;
  let now = () => timeOffset;
  let yielded = null;
  let shouldYield = () => false;
  const memory = new MemoryRenderer({
    now: () => now(),
    strictReplay,
    replayOutsideStrictMode,
    replayFailedRender,
    userTimingEnabled,
    profilerEnabled,
    profilerInteractions,
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
  registerReflection(component => memory.isMounted(component, persistent));

  function targetFor(id, concurrent) {
    if (!roots.has(id)) {
      const root = persistent ? memory.createPersistentRoot(concurrent) : memory.createRoot(concurrent);
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
    let remaining = units;
    while (!stopped && scheduled.size) {
      if (++count > 1000) unsupported('ReactNoop flush did not settle');
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
    findInstance(value) {
      observe();
      return memory.findInstance(value, persistent) ?? null;
    },
    batchedUpdates(action, ...args) {
      observe();
      let result;
      memory.batchedUpdates(() => { result = action(...args); });
      return result;
    },
    interactiveUpdates(action, ...args) {
      observe();
      let result;
      memory.interactiveUpdates(() => { result = action(...args); });
      return result;
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
        if (persistent) {
          const {hostDiffCounter, hostCloneCounter} = memory.getPersistentCounters();
          return {hostDiffCounter, hostCloneCounter};
        }
        const {hostDiffCounter, hostUpdateCounter} = memory.getCounters();
        return {hostDiffCounter, hostUpdateCounter};
      } finally {
        memory.resetCounters();
      }
    },
    flushSync(action, ...args) {
      observe();
      yielded = [];
      memory.flushSync(() => action(...args));
      return yielded;
    },
    deferredUpdates(action, ...args) {
      observe();
      let result;
      memory.deferredUpdates(() => { result = action(...args); });
      return result;
    },
    unbatchedUpdates(action, ...args) {
      observe();
      let result;
      memory.unbatchedUpdates(() => { result = action(...args); });
      return result;
    },
    flushExpired() {
      observe();
      memory.flushExpired();
      return clearYields() || [];
    },
    expire(ms) {
      api.advanceTime(ms);
      return api.flushExpired();
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
        return Object.defineProperty({type: node.type, props, children: rendered.length ? rendered : null},
          '$$typeof', {value: Symbol.for('react.test.json')});
      }
      const clear = () => clearYields() || [];
      const wrappedTags = new Set([0, 1, 3, 5, 11, 14]);
      const fixtureType = fiber => fiber.type?.originalType ?? fiber.type?.componentClass ?? fiber.type;

      function childFibers(parent) {
        const children = [];
        for (let fiber = parent.child; fiber; fiber = fiber.sibling) {
          if (wrappedTags.has(fiber.tag) || fiber.tag === 6) children.push(fiber);
          else children.push(...childFibers(fiber));
        }
        return children;
      }

      function toTree(fiber) {
        if (fiber.tag === 6) return String(fiber.memoizedProps);
        const rendered = childFibers(fiber).flatMap(child => {
          const tree = toTree(child);
          return Array.isArray(tree) ? tree : [tree];
        });
        if (fiber.tag === 5) {
          return {nodeType: 'host', type: fixtureType(fiber), props: {...fiber.memoizedProps}, instance: null, rendered};
        }
        if (fiber.tag === 0 || fiber.tag === 1) {
          return {nodeType: 'component', type: fixtureType(fiber), props: {...fiber.memoizedProps},
            instance: fiber.tag === 1 ? fiber.stateNode : null, rendered: combine(rendered)};
        }
        return rendered;
      }

      function findAll(node, predicate, {deep = true} = {}) {
        const matched = predicate(node);
        const result = matched ? [node] : [];
        if (deep || !matched) {
          for (const child of node.children) {
            if (typeof child !== 'string') result.push(...findAll(child, predicate, {deep}));
          }
        }
        return result;
      }

      function one(matches) {
        if (matches.length !== 1) {
          throw new Error(matches.length ? 'Expected 1 but found ' + matches.length + ' instances' : 'No instances found');
        }
        return matches[0];
      }

      function propsMatch(candidate, expected) {
        return candidate.props && Object.keys(expected).every(key => candidate.props[key] === expected[key]);
      }

      return {
        unstable_yield: api.yield,
        unstable_clearYields: clear,
        unstable_setNowImplementation(implementation) { now = implementation; },
        create(element, options) {
          let root = memory.createTestRoot(Boolean(options?.unstable_isConcurrent));
          if (typeof options?.createNodeMock === 'function') {
            root.getContainer().createNodeMock = options.createNodeMock;
          }
          const wrappers = new WeakMap();
          const update = tree => {
            if (root) submit(root, tree);
          };
          function wrap(fiber) {
            if (fiber.tag === 6) return String(fiber.memoizedProps);
            let wrapper = wrappers.get(fiber) || (fiber.alternate && wrappers.get(fiber.alternate));
            if (!wrapper) {
              wrapper = {
                _fiber: fiber,
                _currentFiber() {
                  if (this._fiber._unmounted) throw new Error('Unable to find node on an unmounted component.');
                  let top = this._fiber;
                  while (top.return) top = top.return;
                  const current = root?.getTestTree();
                  if (top !== current && current && this._fiber.alternate) {
                    let next = this._fiber.alternate;
                    while (next.return) next = next.return;
                    if (next === current) this._fiber = this._fiber.alternate;
                    else throw new Error('Unable to find node on an unmounted component.');
                  } else if (top !== current) {
                    throw new Error('Unable to find node on an unmounted component.');
                  }
                  return this._fiber;
                },
                get type() { return fixtureType(this._currentFiber()); },
                get props() { return this._currentFiber().memoizedProps; },
                get instance() {
                  const current = this._currentFiber();
                  if (current.tag === 5) return root.getPublicHost(current.stateNode);
                  return current.tag === 1 ? current.stateNode : null;
                },
                get children() { return childFibers(this._currentFiber()).map(wrap); },
                get parent() {
                  let parent = this._currentFiber().return;
                  while (parent && !wrappedTags.has(parent.tag)) parent = parent.return;
                  return !parent || parent.tag === 3 && childFibers(parent).length < 2 ? null : wrap(parent);
                },
                find(predicate) { return one(this.findAll(predicate, {deep: false})); },
                findByType(type) { return one(this.findAllByType(type, {deep: false})); },
                findByProps(props) { return one(this.findAllByProps(props, {deep: false})); },
                findAll(predicate, options) { return findAll(this, predicate, options); },
                findAllByType(type, options) { return findAll(this, node => node.type === type, options); },
                findAllByProps(props, options) { return findAll(this, node => propsMatch(node, props), options); },
              };
            }
            wrapper._fiber = fiber;
            wrappers.set(fiber, wrapper);
            return wrapper;
          }
          update(element);
          return {update, unstable_flushAll: () => flushTestRenderer(Infinity), unstable_clearYields: clear,
            unstable_flushNumberOfYields: flushTestRenderer,
            unstable_flushSync(action) {
              clearYields();
              let result;
              memory.flushSync(() => { result = action(); });
              return result;
            },
            toJSON() {
              if (!root) return null;
              const children = root.getChildren().map(toJSON).filter(child => child !== null);
              return children.length > 1 ? children : children[0] ?? null;
            },
            toTree() {
              const fiber = root?.getTestTree();
              return fiber ? combine(toTree(fiber)) : null;
            },
            getInstance() {
              const child = root?.getTestTree()?.child;
              if (child?.tag === 5) return root.getPublicHost(child.stateNode);
              return child?.tag === 1 ? child.stateNode : null;
            },
            unmount() {
              if (!root) return;
              observe();
              root.unmount(null);
              root = null;
            },
            get root() {
              const fiber = root?.getTestTree();
              const children = fiber ? childFibers(fiber) : [];
              if (children.length === 0) throw new Error("Can't access .root on unmounted test renderer");
              return wrap(children.length === 1 ? children[0] : fiber);
            },
          };
        },
      };
    },
  };
  api.dumpTree = () => unsupported('ReactNoop.dumpTree');
  return api;
};
