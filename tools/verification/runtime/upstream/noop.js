'use strict';

module.exports = function createNoop({dom, moduleFor, member, observe, unsupported, react}) {
  const defaultRoot = '<default>';
  const roots = new Map();
  const hosts = new WeakMap();
  const scheduled = [];
  const events = moduleFor('react4j.dom.DomEvents');
  const stateFor = member(events, 'm_state__');
  const create = member(dom, 'm_createRoot__');
  const render = member(dom, 'm_render__react4j_Renderable__java_lang_Object__react4j_dom_ReactDOM_RenderCallbackFn');
  let yielded = null;

  function project(node) {
    let record = hosts.get(node);
    if (node.nodeType === 3) {
      if (!record) {
        record = {text: '', hidden: false};
        hosts.set(node, record);
      }
      record.text = node.nodeValue;
      return record;
    }
    const state = node.nodeType === 1 && events[stateFor](node);
    if (!state) unsupported('ReactNoop host outside the Java tree');
    const props = state[member(state, 'f_inputs__')];
    if (!record) {
      record = {type: state[member(state, 'f_tag__')], children: [], prop: undefined, hidden: false};
      Object.defineProperty(record, 'text', {value: null, writable: true});
      hosts.set(node, record);
    }
    const directText = typeof props.children === 'string' || typeof props.children === 'number';
    record.text = directText ? node.textContent : null;
    record.prop = props.prop;
    record.hidden = props.hidden === true || node.style.display === 'none';
    record.children.splice(0, record.children.length, ...(directText ? [] : Array.from(node.childNodes, project)));
    return record;
  }

  function getChildren(id = defaultRoot) {
    const target = roots.get(id);
    if (!target) return null;
    const children = target.fixture.children;
    children.splice(0, children.length, ...Array.from(target.container.childNodes, project));
    return children;
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

  function targetFor(id, concurrent) {
    if (!roots.has(id)) {
      const container = document.createElement('div');
      const target = {container, fixture: {rootID: id, children: []}};
      if (concurrent) {
        const root = dom[create](container);
        const prototype = Object.getPrototypeOf(root);
        const schedule = member(prototype, 'm_scheduleSlice__');
        target.root = root;
        root[schedule] = () => {
          // Capture the actual Java scheduler's host callback. Its enqueue
          // order, coalescing, and subsequent slices remain Java behavior.
          const setTimer = global.setTimeout;
          global.setTimeout = (callback, delay) => {
            if (delay !== 0) unsupported('ReactNoop delayed scheduler turn');
            scheduled.push({target, callback});
          };
          try {
            prototype[schedule].call(root);
          } finally {
            global.setTimeout = setTimer;
          }
        };
      }
      roots.set(id, target);
    }
    return roots.get(id);
  }

  function submit(element, id, concurrent, callback) {
    const target = targetFor(id, concurrent);
    observe();
    if (target.root) target.root.render(element, callback || null);
    else dom[render](element, target.container, callback || null);
  }

  function clearYields() {
    const result = yielded;
    yielded = null;
    return result;
  }

  function flush() {
    let count = 0;
    let failure = null;
    const failedRoots = new Set();
    while (scheduled.length !== 0) {
      const {target, callback} = scheduled.shift();
      if (failedRoots.has(target)) continue;
      if (++count > 1000) unsupported('ReactNoop full flush did not settle');
      try {
        callback();
      } catch (error) {
        failedRoots.add(target);
        if (failure === null) failure = {error};
      }
    }
    if (failure !== null) throw failure.error;
    return clearYields() || [];
  }

  const api = {
    render: (element, callback) => submit(element, defaultRoot, true, callback),
    renderLegacySyncRoot: (element, callback) => submit(element, defaultRoot, false, callback),
    renderToRootWithID: (element, id, callback) => submit(element, id, true, callback),
    getOrCreateRootContainer: (id = defaultRoot, concurrent = false) => targetFor(id, concurrent).fixture,
    getChildren,
    getChildrenAsJSX(id) {
      const children = getChildren(id);
      const result = combine(children && children.map(toJSX));
      return Array.isArray(result) ? react.createElement(react.Fragment, {children: result}) : result;
    },
    unmountRootWithID(id) {
      const target = roots.get(id);
      if (!target) return;
      observe();
      if (target.root) target.root.unmount(() => roots.delete(id));
      else {
        dom[member(dom, 'm_unmountComponentAtNode__')](target.container);
        roots.delete(id);
      }
    },
    createPortal(children, fixture, key) {
      const target = roots.get(fixture.rootID);
      if (!target || target.fixture !== fixture || key != null) unsupported('ReactNoop portal container or key');
      observe();
      return dom[member(dom, 'm_createPortal__')](children, target.container);
    },
    batchedUpdates(action, ...args) {
      observe();
      return dom[member(dom, 'm_batchedUpdates__')](() => action(...args));
    },
    hasScheduledCallback: () => scheduled.length !== 0,
    flush,
    flushUnitsOfWork(units) {
      if (units !== Infinity) unsupported('ReactNoop finite units of work');
      return flush();
    },
    yield(value) {
      if (yielded === null) yielded = [];
      yielded.push(value);
    },
    clearYields,
    testRenderer() {
      // Jest's test-renderer matchers consume JSON from committed host children.
      function toJSON(node) {
        if (node.nodeType === 3) return node.nodeValue;
        const state = events[stateFor](node);
        const {children, ...props} = state[member(state, 'f_inputs__')];
        if (node.style.display === 'none' && (!props.style || props.style.display !== 'none')) return null;
        const rendered = Array.from(node.childNodes, toJSON).filter(child => child !== null);
        return {type:state[member(state, 'f_tag__')], props, children:rendered.length ? rendered : null};
      }
      const clear = () => clearYields() || [];
      let next = 0;
      return {
        unstable_yield: api.yield,
        unstable_clearYields: clear,
        unstable_setNowImplementation: () => unsupported('test renderer clock injection'),
        create(element, options) {
          const id = String(++next);
          const concurrent = Boolean(options && options.unstable_isConcurrent);
          const target = targetFor(id, concurrent);
          const update = tree => submit(tree, id, concurrent);
          update(element);
          return {update, unstable_flushAll:flush, unstable_clearYields:clear,
            unstable_flushNumberOfYields:() => unsupported('test renderer partial flush'),
            toJSON() {
              const children = Array.from(target.container.childNodes, toJSON).filter(child => child !== null);
              return children.length > 1 ? children : children[0] ?? null;
            },
          };
        },
      };
    },
  };
  for (const operation of ['advanceTime', 'expire', 'flushExpired', 'flushDeferredPri', 'flushNextYield',
    'flushThrough', 'flushAndYield', 'flushWithHostCounters', 'deferredUpdates', 'interactiveUpdates',
    'unbatchedUpdates', 'flushSync', 'dumpTree', 'findInstance']) {
    api[operation] = () => unsupported('ReactNoop.' + operation);
  }
  return api;
};
