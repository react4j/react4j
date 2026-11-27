// Jest 23 loads this file in each test realm of the upstream source checkout.
'use strict';
const fs = require('fs');
const counters = require('./reporter');
const adaptations = counters.adaptations;
const HostSymbol = global.Symbol;

installJavaRenderer();

function unsupported(operation) {
  throw new Error('Outside the React4j test adapter: ' + operation);
}

function installJavaRenderer() {
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
    const initialize = () => createRenderer(load(scope).call(scope, scope.goog), require.requireActual('react'));
    if (global.Symbol !== undefined) return initialize();
    // ReactElement's beforeEach removes Symbol even for unrelated element tests.
    // Give the Java host and the JSX records it consumes the same modern protocol.
    adaptations.add('J2CL and JSX use the modern Symbol element protocol');
    Object.defineProperty(scope, 'Symbol', {value: HostSymbol});
    global.Symbol = HostSymbol;
    try { return initialize(); } finally { global.Symbol = undefined; }
  }, {virtual: true});
  jest.doMock('react', () => fixture().react);
  jest.doMock('react-dom', () => {
    if (require('shared/ReactFeatureFlags').enableSchedulerTracing &&
        require('scheduler/tracing').__interactionsRef?.current == null) {
      adaptations.add('external scheduler tracing build compatibility');
      throw new Error('The profiling renderer requires a profiling scheduler/tracing module. Learn more at http://fb.me/react-profiling');
    }
    return fixture().adapter;
  });
  jest.doMock('react-dom/src/client/ReactDOM', () => fixture().adapter);
  jest.doMock('react-dom/test-utils', () => createTestUtils(fixture().adapter));
  jest.doMock('react-dom/src/client/ReactDOMComponentTree', () => ({
    ...require.requireActual('react-dom/src/client/ReactDOMComponentTree'),
    getInstanceFromNode: node => fixture().adapter.eventHost(node),
  }));
  jest.doMock('events/EventPluginUtils', () => ({
    ...require.requireActual('events/EventPluginUtils'),
    getFiberCurrentPropsFromNode: node => fixture().adapter.eventProps(node),
  }));
  jest.doMock('react-dom/src/events/ReactBrowserEventEmitter', () => ({
    ...require.requireActual('react-dom/src/events/ReactBrowserEventEmitter'),
    setEnabled: enabled => fixture().adapter.setEventsEnabled(enabled),
  }));
  if (process.env.REACT4J_RECONCILER_CASE) {
    jest.doMock('react-reconciler', () => fixture().createHostFixture);
    jest.doMock('react-noop-renderer', () => require('./noop')(fixture().noop));
    jest.doMock('react-test-renderer', () => {
      const cases = fixture().noop;
      if (process.env.REACT4J_RECONCILER_CASE === 'ReactTestRenderer-test.internal.js') {
        const flags = require('shared/forks/ReactFeatureFlags.test-renderer');
        return require('./noop')({...cases,
          replayFailedRender: () => flags.replayFailedUnitOfWorkWithInvokeGuardedCallback,
        }).testRenderer();
      }
      return require('./noop')(cases).testRenderer();
    });
    jest.doMock('react-noop-renderer/persistent', () => require('./noop')(fixture().noop, true));
  }
}

function createTestUtils(adapter) {
  const isDOMComponent = instance => Boolean(instance && instance.nodeType === 1 && instance.tagName);

  function scryRenderedDOMComponentsWithClass(instance, classNames) {
    const required = Array.isArray(classNames) ? classNames : classNames.split(/\s+/);
    return adapter.findRenderedHosts(instance, node => {
      if (!isDOMComponent(node)) return false;
      const classes = (node.getAttribute('class') || '').split(/\s+/);
      return required.every(name => classes.includes(name));
    });
  }

  return {
    renderIntoDocument: element => adapter.render(element, document.createElement('div')),
    isDOMComponent,
    scryRenderedDOMComponentsWithClass,
    findRenderedDOMComponentWithClass(instance, className) {
      const matches = scryRenderedDOMComponentsWithClass(instance, className);
      if (matches.length !== 1) {
        throw new Error('Did not find exactly one match (found: ' + matches.length + ') for class:' + className);
      }
      return matches[0];
    },
    SimulateNative: {click: node => dispatchNative(node, 'click')},
    Simulate: {
      click(node, eventData) {
        if (eventData != null) unsupported('synthetic click event data');
        return dispatchNative(node, 'click');
      },
      mouseEnter(node, eventData) {
        if (eventData != null) unsupported('synthetic mouseEnter event data');
        return dispatchNative(node, 'mouseover');
      },
      change(node) {
        if (node.tagName !== 'SELECT') unsupported('synthetic-only change outside a select');
        return dispatchNative(node, 'change');
      },
    },
  };
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
    const EventType = type === 'click' || type === 'mouseover' ? win.MouseEvent : win.Event;
    node.dispatchEvent(new EventType(type, {bubbles: true, cancelable: true}));
    if (failure) throw failure;
  } finally {
    win.removeEventListener('error', onError);
  }
}

function createRenderer(moduleFor, sourceReact) {
  const View = moduleFor('react4j.internal.NativeView');
  // Only BUNDLE is used here. Reject changed or ambiguous J2CL signatures.
  function member(object, prefix) {
    const keys = Object.getOwnPropertyNames(object).filter(key => key.startsWith(prefix));
    if (keys.length !== 1) throw Error('Expected one J2CL method matching ' + prefix);
    return keys[0];
  }

  // The historical Babel class transform calls its superclass as a function.
  // Older TypeScript constructors keep using their original receiver after calling super.
  function Component(props, context) {
    const view = Object.assign(this, new View(props));
    view.context = context;
    view.state = undefined;
    return view;
  }
  Object.setPrototypeOf(Component.prototype, View.prototype);
  for (const name of ['replaceState', 'isMounted']) {
    const descriptor = Object.getOwnPropertyDescriptor(sourceReact.Component.prototype, name);
    if (descriptor) Object.defineProperty(Component.prototype, name, descriptor);
  }
  Component.prototype.isReactComponent = {};
  let isMounted = () => false;
  function updateCallback(component, callback) {
    if (callback == null) return null;
    if (typeof callback !== 'function') {
      throw new Error('Invalid argument passed as callback. Expected a function. Instead received: ' + String(callback));
    }
    return () => callback.call(component);
  }
  const warnedConstructorState = new WeakSet();
  const warnedConstructorForce = new WeakSet();
  let constructingFixture = false;
  function warnConstructorUpdate(instance, method, warned) {
    if (process.env.NODE_ENV !== 'production' && constructingFixture && !warned.has(instance.constructor)) {
      warned.add(instance.constructor);
      const name = instance.constructor.displayName || instance.constructor.name || 'Component';
      console.error("Warning: Can't call " + method + " on a component that is not yet mounted. " +
        'This is a no-op, but it might indicate a bug in your application. ' +
        'Instead, assign to `this.state` directly or define a `state = {};` ' +
        'class property with the desired state in the ' + name + ' component.');
    }
  }
  Component.prototype.setState = function(patch, callback) {
    warnConstructorUpdate(this, 'setState', warnedConstructorState);
    const done = updateCallback(this, callback);
    const update = typeof patch === 'function' ? (state, props) => patch.call(this, state, props) : patch;
    View.prototype.setStateWithCallback.call(this, update, done);
  };
  Component.prototype.forceUpdate = function(callback) {
    warnConstructorUpdate(this, 'forceUpdate', warnedConstructorForce);
    View.prototype.forceUpdateWithCallback.call(this, updateCallback(this, callback));
  };
  Component.prototype.updater = {
    isMounted: component => isMounted(component),
    enqueueReplaceState(instance, patch, callback) {
      const update = typeof patch === 'function' ? (state, props) => patch.call(instance, state, props) : patch;
      View.prototype.replaceStateWithCallback.call(instance, update, updateCallback(instance, callback));
    },
  };

  const constructors = new WeakMap();
  const warnedUnextended = new WeakSet();
  const internals = sourceReact.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
  const current = internals.ReactCurrentOwner;
  const JavaReact = moduleFor('react4j.React');
  const owner = moduleFor('react4j.internal.CurrentOwner');
  const currentOwner = owner[member(owner, 'm_get__')].bind(owner);
  const Fiber = moduleFor('react4j.reconciler.Reconciler.Fiber');
  const reconciler = moduleFor('react4j.reconciler.Reconciler');
  const readContext = member(reconciler, 'm_readCurrentContext__');
  function strictReplay() {
    if (process.env.NODE_ENV === 'production') return false;
    const flags = require('shared/ReactFeatureFlags');
    if (flags.debugRenderPhaseSideEffectsForStrictMode !== false) return true;
    adaptations.add('upstream flag disables strict render replay');
    return false;
  }
  const DomHost = moduleFor('react4j.dom.DomReconcilerHost');
  if (DomHost) {
    DomHost.prototype[member(DomHost.prototype, 'm_strictReplay__')] = strictReplay;
    DomHost.prototype[member(DomHost.prototype, 'm_replayFailedRender__')] = () =>
      require('shared/ReactFeatureFlags').replayFailedUnitOfWorkWithInvokeGuardedCallback !== false;
  }

  const Failure = moduleFor('react4j.reconciler.Reconciler.RenderFailure');
  if (internals.ReactDebugCurrentFrame) {
    const stack = member(Failure, 'm_stackFrom__');
    internals.ReactDebugCurrentFrame.getCurrentStack = () => {
      const fiber = currentOwner();
      return fiber ? Failure[stack](fiber) : '';
    };
  }
  const dispatcher = {
    readContext(context, observedBits) {
      adaptations.add('private context read from active Java render scope');
      return reconciler[readContext](context, observedBits);
    },
  };
  function PureComponent(props, context) {
    return Component.call(this, props, context);
  }
  PureComponent.prototype = Object.create(Component.prototype);
  PureComponent.prototype.constructor = PureComponent;
  PureComponent.prototype.shouldComponentUpdate = function(props, state) {
    const runtime = moduleFor('react4j.reconciler.Reconciler');
    return !runtime.shallowEqual(this.props, props) || !runtime.shallowEqual(this.state, state);
  };
  function renderFixture(receiver, render, args, constructing = false) {
    const previous = current.currentDispatcher;
    const parent = current.current;
    const fiber = currentOwner();
    const previousConstructing = constructingFixture;
    constructingFixture = constructing;
    current.currentDispatcher = dispatcher;
    if (!constructing && fiber && (receiver || internals.ReactDebugCurrentFrame)) {
      current.current = {type: fiber[member(fiber, 'f_type__')], tag: receiver ? 1 : 0, stateNode: receiver};
    } else {
      current.current = null;
    }
    try { return render.apply(receiver, args); } finally {
      current.current = parent;
      current.currentDispatcher = previous;
      constructingFixture = previousConstructing;
    }
  }
  const hooks = ['componentDidMount', 'componentDidUpdate', 'componentWillUnmount',
    'getSnapshotBeforeUpdate', 'shouldComponentUpdate', 'componentDidCatch'];
  function installLifecycleHooks(instance) {
    for (const hook of hooks) {
      if (typeof instance[hook] !== 'function') continue;
      const name = 'On' + hook.charAt(0).toUpperCase() + hook.slice(1);
      const iface = moduleFor('react4j.internal.' + name);
      instance['$implements__react4j_internal_' + name] = true;
      instance[member(iface.prototype, 'm_' + hook + '__')] = instance[hook];
    }
  }
  const lazyType = HostSymbol.for('react.lazy');
  const memoType = HostSymbol.for('react.memo');
  const forwardRefType = HostSymbol.for('react.forward_ref');
  function factory(type) {
    if (constructors.has(type)) return constructors.get(type);
    if (type?.$$typeof === lazyType) {
      const wrapper = Object.assign({}, type, {_resolveType: factory});
      constructors.set(type, wrapper);
      constructors.set(wrapper, wrapper);
      return wrapper;
    }
    if (type?.$$typeof === memoType) {
      const wrapper = Object.assign({}, type, {type: factory(type.type), originalType: type});
      constructors.set(type, wrapper);
      constructors.set(wrapper, wrapper);
      return wrapper;
    }
    if (type?.$$typeof === forwardRefType) {
      const render = (props, ref) => renderFixture(undefined, type.render, [props, ref]);
      Object.defineProperty(render, 'name', {value: type.render.name});
      render.displayName = type.render.displayName;
      const wrapper = Object.assign({}, type, {render, originalType: type});
      delete wrapper.propTypes;
      constructors.set(type, wrapper);
      constructors.set(wrapper, wrapper);
      return wrapper;
    }
    if (typeof type !== 'function') return type;
    const legacyClass = type.prototype && Object.hasOwn(type.prototype, '__reactAutoBindPairs');
    if (legacyClass && !(type.prototype instanceof Component)) {
      // create-react-class copies Component methods onto a shared, detached mixin prototype.
      let base = type.prototype;
      while (Object.getPrototypeOf(base) !== Object.prototype) base = Object.getPrototypeOf(base);
      Object.setPrototypeOf(base, Component.prototype);
    }
    function create(inputs, context) {
      if (type.prototype?.isReactComponent) {
        let instance = renderFixture(undefined, () => {
          if (!legacyClass) return new type(inputs, context);
          const view = Reflect.construct(View, [inputs], type);
          type.call(view, inputs, context, Component.prototype.updater);
          return view;
        }, [], true);
        if (typeof instance[member(View.prototype, 'm_updateInputs__')] !== 'function') {
          instance = Object.assign(new View(inputs), instance);
        }
        const render = instance.render === View.prototype.render ? undefined : instance.render;
        instance.render = function() {
          if (process.env.NODE_ENV !== 'production' && !render) {
            const name = type.displayName || type.name || 'Component';
            const details = type.prototype?.render && type.prototype.render !== View.prototype.render
              ? 'did you accidentally return an object from the constructor?'
              : 'you may have forgotten to define `render`.';
            console.error('Warning: ' + name + '(...): No `render` method found on the returned component instance: ' + details);
          }
          return renderFixture(this, render, []);
        };
        if (render?._isMockFunction) {
          instance.render._isMockFunction = true;
        }
        installLifecycleHooks(instance);
        return instance;
      }
      const view = new View(inputs);
      view.context = context;
      view.render = function() {
        if (process.env.NODE_ENV !== 'production' && typeof type.prototype?.render === 'function'
            && !warnedUnextended.has(type)) {
          warnedUnextended.add(type);
          const name = type.displayName || type.name || 'Component';
          console.error('Warning: The <' + name + ' /> component appears to have a render method, ' +
            "but doesn't extend React.Component. This is likely to cause errors. " +
            'Change ' + name + ' to extend React.Component instead.');
        }
        const result = renderFixture(undefined, type, [this.props, this.context]);
        if (result && typeof result.render === 'function') {
          Object.assign(this, result);
          installLifecycleHooks(this);
          const fiber = currentOwner();
          if (fiber) fiber[member(Fiber.prototype, 'm_initializeModuleComponent__')](this);
          this.render = () => renderFixture(this, result.render, []);
          return this.render();
        }
        return result;
      };
      return view;
    }
    create.componentClass = type;
    create.functionComponent = !type.prototype?.isReactComponent;
    create.rawRenderResult = true;
    create.displayName = type.displayName || type.name;
    Object.defineProperty(create, 'name', {value: type.name});
    Object.defineProperty(create, 'defaultProps', {get: () => type.defaultProps});
    constructors.set(type, create);
    constructors.set(create, create);
    return create;
  }
  // Upstream JSX exposes the original class on its records. Resolve React4j view factories at the Java boundary.
  const elementAccess = moduleFor('react4j.ReactElement.$Overlay');
  const typeAccessor = member(elementAccess, 'm_type__');
  const readType = elementAccess[typeAccessor];
  elementAccess[typeAccessor] = element => factory(readType.call(elementAccess, element));

  function withElementType(element, type) {
    if (element == null || element.type === type) return element;
    const descriptors = Object.getOwnPropertyDescriptors(element);
    descriptors.type.value = type;
    const record = Object.create(Object.getPrototypeOf(element), descriptors);
    return Object.isFrozen(element) ? Object.freeze(record) : record;
  }
  function validateJSXProps(element, type) {
    if (process.env.NODE_ENV === 'production') return element;
    const describe = require('shared/describeComponentFrame').default;
    const name = candidate => candidate?.displayName || candidate?.name || null;
    const stack = displayName => () =>
      describe(displayName, element._source, name(element._owner?.type)) +
      internals.ReactDebugCurrentFrame.getStackAddendum();
    if (type?.$$typeof === forwardRefType && type.propTypes) {
      const renderName = name(type.render);
      const displayName = type.displayName || (renderName ? 'ForwardRef(' + renderName + ')' : 'ForwardRef');
      reconciler[member(reconciler, 'm_checkForwardRefProps__')](element, type, element.props, stack(displayName));
    } else if (typeof type === 'function' &&
        (type.propTypes || type.PropTypes !== undefined || typeof type.getDefaultProps === 'function')) {
      const PropTypes = moduleFor('react4j.reconciler.PropTypeValidator');
      PropTypes[member(PropTypes, 'm_checkElement__')](element, type, stack(name(type)));
    }
    return element;
  }

  const react = Object.assign({}, sourceReact, {
    Component, PureComponent,
    createElement(type, props, ...children) {
      if (type?.$$typeof === HostSymbol.for('react.context') && typeof children[0] === 'function') {
        const render = children[0];
        children[0] = value => renderFixture(undefined, render, [value]);
      }
      const element = sourceReact.createElement(factory(type), props, ...children);
      return validateJSXProps(withElementType(element, type), type);
    },
    cloneElement(element, ...args) {
      const type = element?.type;
      const clone = sourceReact.cloneElement(withElementType(element, factory(type)), ...args);
      return validateJSXProps(withElementType(clone, type), type);
    },
    createContext(initial, changedBits) {
      const context = JavaReact[member(JavaReact, 'm_createContext__')](initial);
      context._calculateChangedBits = changedBits;
      return context;
    },
  });

  const noop = process.env.REACT4J_RECONCILER_CASE && {
    MemoryRenderer: moduleFor('react4j.testing.MemoryRenderer'),
    react,
    unsupported,
    userTimingEnabled: () => require('shared/ReactFeatureFlags').enableUserTimingAPI === true,
    get replayOutsideStrictMode() {
      return require('shared/ReactFeatureFlags').debugRenderPhaseSideEffects === true;
    },
    registerReflection: lookup => { isMounted = lookup; },
    strictReplay,
    replayFailedRender() {
      return require('shared/ReactFeatureFlags').replayFailedUnitOfWorkWithInvokeGuardedCallback !== false;
    },
    profilerEnabled() {
      return Boolean(require('shared/ReactFeatureFlags').enableProfilerTimer);
    },
    profilerInteractions() {
      return require('shared/ReactFeatureFlags').enableSchedulerTracing
        ? require('scheduler/tracing').unstable_getCurrent() : null;
    },
    observe() { counters.calls++; },
  };
  function createHostFixture(config) {
    const HostFixture = moduleFor('react4j.testing.HostFixture');
    const host = new HostFixture(config);
    return {
      createContainer: container => host.createContainer(container),
      updateContainer(element, container, parent, callback) {
        if (parent != null) unsupported('custom host subtree mounting');
        counters.calls++;
        return host.updateContainer(element, container, callback ?? null);
      },
    };
  }
  let adapter;
  return {get adapter() { return adapter ??= createDOMAdapter(moduleFor, member, current, currentOwner, lookup => { isMounted = lookup; }); }, react, noop, createHostFixture};
}

function createDOMAdapter(moduleFor, member, current, currentOwner, registerReflection) {
  const dom = moduleFor('react4j.dom.ReactDOM');
  // Find the committed host exposed through refs in Java fixtures. Each Jest case owns its roots.
  const domRenderer = moduleFor('react4j.dom.Renderer');
  const isMountedView = member(moduleFor('react4j.reconciler.Reconciler.Fiber').prototype, 'm_isMountedView__');
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
  function findView(instance) {
    for (const container of containers) {
      const root = committedRoot(container);
      if (!root) continue;
      const view = walk(rootFiber(root),
        fiber => fiberView(fiber) === instance ? fiber : undefined);
      if (view) return view;
    }
  }
  const renderWarnings = new Map();
  function warnOnRenderAccess(method) {
    const owner = current.current?.stateNode;
    if (process.env.NODE_ENV === 'production' || !owner) return;
    let warned = renderWarnings.get(method);
    if (!warned) renderWarnings.set(method, warned = new WeakSet());
    if (warned.has(owner)) return;
    warned.add(owner);
    const name = owner.constructor.displayName || owner.constructor.name || 'Component';
    console.error('Warning: ' + name + ' is accessing ' + method + ' inside its render() function. ' +
      'render() should be a pure function of props and state.');
  }
  registerReflection(instance => {
    warnOnRenderAccess('isMounted');
    const rendering = current.current?.stateNode === instance ? currentOwner() : null;
    const fiber = rendering || findView(instance) || mountedViews.get(instance);
    return !!fiber && fiber[isMountedView](instance);
  });
  const render = member(dom, 'm_render__react4j_Renderable__java_lang_Object__react4j_dom_ReactDOM_RenderCallbackFn');
  const unmount = member(dom, 'm_unmountComponentAtNode__');
  const batch = member(dom, 'm_batchedUpdates__');
  const portal = member(dom, 'm_createPortal__');
  const events = moduleFor('react4j.dom.DomEvents');
  const eventState = node => events[member(events, 'm_state__')](node);
  let eventsEnabled = true;
  let warnedPortalAlias = false;
  return {
    eventHost(node) {
      const state = eventState(node);
      if (!state) return null;
      adaptations.add('private emitter host lookup from committed Java event state');
      return {stateNode: state[member(state, 'f_element__')], type: state[member(state, 'f_tag__')]};
    },
    eventProps(node) {
      const state = eventState(node);
      return state ? state[member(state, 'f_inputs__')] : null;
    },
    setEventsEnabled(enabled) {
      if (enabled !== eventsEnabled) {
        events[member(events, enabled ? 'm_resume__' : 'm_pause__')]();
        eventsEnabled = enabled;
      }
    },
    render(element, container, callback) {
      counters.calls++;
      containers.add(container);
      const instance = dom[render](element, container, callback || null);
      const root = committedRoot(container);
      if (root) walk(rootFiber(root), fiber => {
        const view = fiberView(fiber);
        if (view) mountedViews.set(view, fiber);
      });
      return instance;
    },
    unmountComponentAtNode(container) { counters.calls++; return dom[unmount](container); },
    verifyForeignRootOwnership(...operations) {
      moduleFor('react4j.dom.ForeignRootAssertions').verify(...operations);
    },
    verifyForeignRootRefs(...operations) {
      moduleFor('react4j.dom.ForeignRootAssertions').verifyRefs(...operations);
    },
    unstable_batchedUpdates(action, ...args) {
      counters.calls++;
      let result;
      dom[batch](() => { result = action(...args); });
      return result;
    },
    flushSync(action, ...args) {
      counters.calls++;
      let result;
      dom[member(dom, 'm_flushSync__')](() => { result = action(...args); });
      return result;
    },
    unstable_flushControlled(action, ...args) {
      counters.calls++;
      dom[member(dom, 'm_flushControlled__')](() => action(...args));
    },
    createPortal(children, container, key) {
      if (key != null) unsupported('keyed portal');
      counters.calls++;
      return dom[portal](children, container);
    },
    unstable_createPortal(children, container, key) {
      adaptations.add('deprecated portal alias uses createPortal');
      if (process.env.NODE_ENV !== 'production' && !warnedPortalAlias) {
        warnedPortalAlias = true;
        console.warn('Warning: The ReactDOM.unstable_createPortal() alias has been deprecated, ' +
          'and will be removed in React 17+. Update your code to use ' +
          'ReactDOM.createPortal() instead. It has the exact same API, ' +
          'but without the "unstable_" prefix.');
      }
      return this.createPortal(children, container, key);
    },
    unstable_createRoot(container, options) {
      if (options && options.hydrate) unsupported('hydration');
      counters.calls++;
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
      warnOnRenderAccess('findDOMNode');
      if (instance == null || instance.nodeType) return instance;
      adaptations.add('findDOMNode fixture');
      const view = findView(instance);
      if (view) return walk(view, fiberHost) || null;
      // During unmount the parent can already have removed the child from its list.
      // Read the actual fiber until dispose clears its view and host children.
      const previous = mountedViews.get(instance);
      if (previous && fiberView(previous) === instance) {
        return walk(previous, fiberHost) || null;
      }
      unsupported('findDOMNode outside a committed tree');
    },
    findRenderedHosts(instance, match) {
      if (instance == null) return [];
      const view = findView(instance);
      if (!view) unsupported('rendered host query outside a committed view');
      adaptations.add('committed host class query');
      const matches = [];
      walk(view, fiber => {
        const host = fiberHost(fiber);
        if (host && match(host)) matches.push(host);
      });
      return matches;
    },
  };
}
