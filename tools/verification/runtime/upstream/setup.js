// Jest 23 loads this file in each test realm of the upstream source checkout.
'use strict';
const fs = require('fs');
const path = require('path');
require(path.join(process.cwd(), 'scripts/jest/setupTests.js'));

let calls = 0;
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
const adaptations = new Set();
afterEach(() => { assertions = expect.getState().assertionCalls - uncheckedWarnings; });
function record(event) {
  fs.appendFileSync(process.env.REACT4J_CASE_LOG, JSON.stringify(event) + '\n');
}
jasmine.getEnv().addReporter({
  specStarted(result) {
    calls = 0;
    adaptations.clear();
    assertions = 0;
    uncheckedWarnings = 0;
    record({start:result.fullName, id:result.id});
  },
  specDone(result) {
    record({id:result.id, name:result.fullName, status:result.status, calls,
      assertions, adaptations:Array.from(adaptations),
      failure:result.failedExpectations.map(f => f.message).join('\n').slice(0, 2000)});
  },
});

if (process.env.REACT4J_CASE_RUNTIME === 'java') {
  installJavaRenderer();
}

function unsupported(operation) {
  throw new Error('Outside the React4j test adapter: ' + operation);
}

function reconcilerTimers() {
  // Jest 23's fake timers do not update performance.now(). Capture the
  // environment's timer instance without advancing or clearing its queue.
  const nativeRequire = require('module').createRequire(path.join(process.cwd(), 'package.json'));
  const prototype = nativeRequire('jest-util').FakeTimers.prototype;
  const useFakeTimers = prototype.useFakeTimers;
  let clock;
  prototype.useFakeTimers = function() {
    clock = this;
    return useFakeTimers.call(this);
  };
  try {
    jest.useFakeTimers();
  } finally {
    prototype.useFakeTimers = useFakeTimers;
  }
  if (!clock || typeof clock._now !== 'number') unsupported('Jest virtual clock');
  return clock;
}

function installJavaRenderer() {
  const timers = process.env.REACT4J_RECONCILER_CASE ? reconcilerTimers() : null;
  global.globalThis = global;
  global.CLOSURE_DEFINES = {'react4j.environment':process.env.NODE_ENV, 'react4j.check_invariants':'false'};
  const bundle = fs.readFileSync(process.env.REACT4J_BUNDLE, 'utf8');
  // Jest clears its module registry when the original fixture calls jest.resetModules().
  // Construct a fresh Closure module registry and Java static state on the same boundary.
  // Closure's legacy provides create global namespaces that its scripts then read by name.
  const load = new Function('scope',
    'with (scope) { return function(goog) {' + bundle + '\nreturn name => goog.module.get(name); }; }');
  const fixture = () => require('react4j-test-runtime');
  jest.doMock('react4j-test-runtime', () => {
    const scope = Object.create(global);
    scope.globalThis = scope;
    // ReactDebugFiberPerf replaces Performance with a marks-only test double.
    if (global.performance && typeof global.performance.now !== 'function') {
      scope.performance = Object.create(global.performance);
      const clock = require('perf_hooks').performance;
      scope.performance.now = () => {
        adaptations.add('host clock alongside User Timing fixture');
        return clock.now();
      };
    }
    scope.goog = {};
    return createRenderer(load(scope).call(scope, scope.goog), require.requireActual('react'), timers);
  }, {virtual: true});
  jest.doMock('react', () => fixture().react);
  jest.doMock('react-dom', () => fixture().adapter);
  jest.doMock('react-dom/src/client/ReactDOM', () => fixture().adapter);
  jest.doMock('react-dom/test-utils', () => ({
    renderIntoDocument: element => fixture().adapter.render(element, document.createElement('div')),
    SimulateNative: {click: node => dispatchNative(node, 'click')},
    Simulate: {change(node) {
      if (node.tagName !== 'SELECT') unsupported('synthetic-only change outside a select');
      return dispatchNative(node, 'change');
    }},
  }));
  if (process.env.REACT4J_RECONCILER_CASE) {
    if (process.env.REACT4J_RECONCILER_CASE === 'ReactTracing-test.internal.js') {
      jest.doMock('scheduler/tracing', () => unsupported('upstream Scheduler tracing bundle compatibility'));
    }
    jest.doMock('react-reconciler', () => fixture().createHostFixture);
    jest.doMock('react-noop-renderer', () => require('./noop')(fixture().noop));
    jest.doMock('react-test-renderer', () => require('./noop')(fixture().noop).testRenderer());
    jest.doMock('react-noop-renderer/persistent', () => new Proxy({}, {
      get(target, key) {
        if (key === '__esModule') return false;
        return () => unsupported('ReactNoop persistent host operation ' + String(key));
      },
    }));
  }
}

// ReactTestUtils exposes dispatch errors synchronously. DOM dispatch reports them to Window.
function dispatchNative(node, type) {
  adaptations.add('native ' + type + ' fixture');
  const win = node.ownerDocument.defaultView;
  let failure;
  function onError(event) {
    failure = event.error;
    event.preventDefault();
  }
  win.addEventListener('error', onError);
  try {
    const EventType = type === 'click' ? win.MouseEvent : win.Event;
    node.dispatchEvent(new EventType(type, {bubbles: true, cancelable: true}));
    if (failure) throw failure;
  } finally {
    win.removeEventListener('error', onError);
  }
}

function createRenderer(moduleFor, sourceReact, timers) {
  const View = moduleFor('react4j.internal.NativeView');
  // Only BUNDLE is used here. Reject changed or ambiguous J2CL signatures.
  function member(object, prefix) {
    const keys = Object.getOwnPropertyNames(object).filter(key => key.startsWith(prefix));
    if (keys.length !== 1) throw Error('Expected one J2CL method matching ' + prefix);
    return keys[0];
  }

  // The historical Babel class transform calls its superclass as a function.
  // Construct the Java instance with the original fixture subclass's prototype.
  function Component(props) {
    return Reflect.construct(View, [props], this.constructor);
  }
  Object.setPrototypeOf(Component.prototype, View.prototype);
  Component.prototype.isReactComponent = {};
  Component.prototype.setState = function(patch, callback) {
    if (typeof patch === 'function' || callback != null) unsupported('functional setState or update callback');
    return View.prototype.setState.call(this, patch);
  };
  Component.prototype.forceUpdate = function(callback) {
    if (callback != null) unsupported('forceUpdate callback');
    return View.prototype.forceUpdate.call(this);
  };

  const constructors = new WeakMap();
  let owner;
  const namedRefs = new WeakMap();
  const contexts = new WeakMap();
  const current = sourceReact.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner;
  const dispatcher = {
    readContext(context, observedBits) {
      if (observedBits != null) unsupported('context change bitmasks');
      const java = contexts.get(context);
      if (!java) unsupported('private context read outside the Java fixture');
      adaptations.add('private context read from active Java render scope');
      const api = moduleFor('react4j.React');
      const fiber = api[member(api, 'm_currentOwner__')]();
      const scope = moduleFor('react4j.dom.Reconciler.Scope');
      return scope[member(scope, 'm_lookup__')](fiber[member(fiber, 'f__scope__')], java);
    },
  };
  function renderFixture(receiver, render, args) {
    const parent = owner;
    const previous = current.currentDispatcher;
    owner = receiver;
    current.currentDispatcher = dispatcher;
    try { return render.apply(receiver, args); } finally {
      current.currentDispatcher = previous;
      owner = parent;
    }
  }
  const hooks = ['componentDidMount', 'componentDidUpdate', 'componentWillUnmount',
    'getSnapshotBeforeUpdate', 'shouldComponentUpdate', 'componentDidCatch'];
  const lazyType = typeof Symbol === 'function' && Symbol.for ? Symbol.for('react.lazy') : 0xead4;
  function factory(type) {
    if (type?.$$typeof === lazyType) {
      throw new Error('Unsupported React4j runtime feature: React.lazy');
    }
    if (typeof type !== 'function') return type;
    if (constructors.has(type)) return constructors.get(type);
    function create(inputs) {
      if (type.prototype instanceof Component) {
        const instance = new type(inputs);
        const render = instance.render;
        instance.render = function() { return renderFixture(this, render, []); };
        for (const hook of hooks) {
          if (typeof instance[hook] !== 'function') continue;
          const name = 'On' + hook.charAt(0).toUpperCase() + hook.slice(1);
          const iface = moduleFor('react4j.internal.' + name);
          instance['$implements__react4j_internal_' + name] = true;
          instance[member(iface.prototype, 'm_' + hook + '__')] = instance[hook];
        }
        return instance;
      }
      if (type.prototype && type.prototype.render) unsupported('legacy class factory');
      const view = new View(inputs);
      view.render = function() { return renderFixture(undefined, type, [this.props]); };
      return view;
    }
    create.displayName = type.displayName || type.name;
    create.defaultProps = type.defaultProps;
    constructors.set(type, create);
    return create;
  }
  function refFor(instance, name) {
    adaptations.add('string ref fixture');
    let refs = namedRefs.get(instance);
    if (!refs) {
      refs = new Map();
      namedRefs.set(instance, refs);
      instance.refs = {};
    }
    if (!refs.has(name)) refs.set(name, value => {
      if (value == null) delete instance.refs[name];
      else instance.refs[name] = value;
    });
    return refs.get(name);
  }
  const react = Object.assign({}, sourceReact, {
    Component,
    createElement(type, props, ...children) {
      if (props && props.ref != null && (typeof props.ref === 'string' || typeof props.ref === 'number')) {
        if (!owner) unsupported('ownerless string ref');
        props = Object.assign({}, props, {ref:refFor(owner, props.ref)});
      }
      return sourceReact.createElement(factory(type), props, ...children);
    },
    createContext(initial, changedBits) {
      if (changedBits) unsupported('context change bitmasks');
      const api = moduleFor('react4j.React');
      const context = api[member(api, 'm_createContext__')](initial);
      const provider = context[member(Object.getPrototypeOf(context), 'm_provide__')](initial, []);
      const builder = context[member(Object.getPrototypeOf(context), 'm_consumer__')]();
      const consumer = builder[member(Object.getPrototypeOf(builder), 'm_render__')](() => null);
      const fixture = {Provider:provider.type, Consumer:consumer.type};
      contexts.set(fixture, context);
      return fixture;
    },
  });

  const noop = timers && {
    MemoryRenderer: moduleFor('react4j.testing.MemoryRenderer'),
    react,
    unsupported,
    clock: () => timers._now,
    strictReplay() {
      if (process.env.NODE_ENV === 'production') return false;
      const flags = require('shared/ReactFeatureFlags');
      if (flags.debugRenderPhaseSideEffectsForStrictMode !== false) return true;
      adaptations.add('upstream flag disables strict render replay');
      return false;
    },
    observe() { calls++; },
  };
  function createHostFixture(config) {
    const HostFixture = moduleFor('react4j.testing.HostFixture');
    const host = new HostFixture(config);
    return {
      createContainer: container => host.createContainer(container),
      updateContainer(element, container, parent, callback) {
        if (parent != null) unsupported('custom host subtree mounting');
        calls++;
        return host.updateContainer(element, container, callback ?? null);
      },
    };
  }
  let adapter;
  return {get adapter() { return adapter ??= createDOMAdapter(moduleFor, member); }, react, noop, createHostFixture};
}

function createDOMAdapter(moduleFor, member) {
  const dom = moduleFor('react4j.dom.ReactDOM');
  // Find the committed host exposed through refs in Java fixtures. Each Jest case owns its roots.
  const domRenderer = moduleFor('react4j.dom.Renderer');
  const committedRoot = container => domRenderer[member(domRenderer, 'f_ROOTS__')].get(container);
  const rootFiber = root => root[member(root, 'f_fiber__')];
  const fiberView = fiber => fiber[member(fiber, 'f_view__')];
  const fiberHost = fiber => fiber[member(fiber, 'f_hostInstance__')] || undefined;
  const containers = new Set();
  const mountedViews = new WeakMap();
  function walk(fiber, select) {
    const value = select(fiber);
    if (value !== undefined) return value;
    for (const child of fiber[member(fiber, 'f_children__')]) {
      const found = walk(child, select);
      if (found !== undefined) return found;
    }
  }
  const render = member(dom, 'm_render__react4j_Renderable__java_lang_Object__react4j_dom_ReactDOM_RenderCallbackFn');
  const unmount = member(dom, 'm_unmountComponentAtNode__');
  const batch = member(dom, 'm_batchedUpdates__');
  const portal = member(dom, 'm_createPortal__');
  return {
    render(element, container, callback) {
      calls++;
      containers.add(container);
      const instance = dom[render](element, container, callback || null);
      const root = committedRoot(container);
      if (root) walk(rootFiber(root), fiber => {
        const view = fiberView(fiber);
        if (view) mountedViews.set(view, fiber);
      });
      return instance;
    },
    unmountComponentAtNode(container) { calls++; return dom[unmount](container); },
    unstable_batchedUpdates(action, ...args) { calls++; return dom[batch](() => action(...args)); },
    createPortal(children, container, key) {
      if (key != null) unsupported('keyed portal');
      calls++;
      return dom[portal](children, container);
    },
    unstable_createRoot(container, options) {
      if (options && options.hydrate) unsupported('hydration');
      calls++;
      const root = dom[member(dom, 'm_createRoot__')](container);
      // React4j exposes its completion callback as an argument. Only forward real commits.
      function work(submit) {
        let complete = false;
        const callbacks = [];
        submit(() => {
          complete = true;
          callbacks.forEach(fn => fn());
        });
        return {then(fn) { if (complete) fn(); else callbacks.push(fn); }};
      }
      return {render:node => work(done => root.render(node, done)),
        unmount:() => work(done => root.unmount(done))};
    },
    // Upstream tests use findDOMNode to inspect fixtures. React4j exposes their DOM through refs.
    findDOMNode(instance) {
      if (instance == null || instance.nodeType) return instance;
      adaptations.add('findDOMNode fixture');
      for (const container of containers) {
        const root = committedRoot(container);
        if (!root) continue;
        const view = walk(rootFiber(root),
          fiber => fiberView(fiber) === instance ? fiber : undefined);
        if (view) return walk(view, fiberHost) || null;
      }
      // During unmount the parent can already have removed the child from its list.
      // Read the actual fiber until dispose clears its view and host children.
      const previous = mountedViews.get(instance);
      if (previous && fiberView(previous) === instance) {
        return walk(previous, fiberHost) || null;
      }
      unsupported('findDOMNode outside a committed tree');
    },
  };
}
