goog.module('react4j.RuntimeTest');
const Example = goog.require('react4j.RuntimeCoverageExample');

/**
 * @param {boolean} condition
 * @param {string} message
 */
function check(condition, message) {
  if (!condition) throw new Error(message);
}

/** @return {!Promise<void>} */
async function run() {
  const react = window['React'];
  const dom = window['ReactDOM'];
  Example.configuration();
  Example.registry();
  check(JSON.stringify(Example.nodes()) === '[1,2,3,"4",5.5,6.5,"text",["nested"]]', 'Node overloads');
  const introspected = Example.introspection();
  check(JSON.stringify(introspected) === '[null,"AUTODETECT",42,true,4200000000,2.5,5,3,"x","<Stream>","plain"]', 'Introspection: ' + JSON.stringify(introspected));
  Example.stateUpdates();
  const root = document.createElement('div');
  document.body.appendChild(root);
  let view;
  /** Real React component used for Java bridge roundtrips. */
  class Component extends react['Component'] {
    /** @param {!Object} props */
    constructor(props) {
      super(props);
      view = this;
      this.state = {count: 1};
    }
    /** @return {!Object} */
    render() { return react.createElement('span', null, this.props.label + ':' + this.state.count); }
  }
  try {
    const element = Example.element(Component);
    check('copy-key' === element.key && 'copy' === element.props.label, 'Element inputs/key');
    dom.render(element, root);
    check('copy:1' === root.textContent, 'View element rendering');
    check(Example.inputs(view) === view.props && Example.requireInputs(view) === view.props, 'Native inputs');
    check(Example.state(view) === view.state, 'Native state');
    Example.updateState(view, {count: 2});
    check('copy:2' === root.textContent, 'Native state update');
    const empty = new react['Component'](null);
    check(null === Example.inputs(empty), 'Nullable native inputs');
    check(Example.rejectsNullInputs(empty), 'Required native inputs reject null');
    dom.render(Example.contextTree('provided'), root);
    check('provided' === root.textContent, 'Context provider/consumer');
    dom.render(Example.contextTree(null), root);
    check('empty' === root.textContent, 'Null context update');
    Example.pauseScheduler();
    await new Promise(resolve => setTimeout(resolve, 0));
    check(!Example.schedulerPaused(), 'Scheduler release after render turn');
    Example.pauseScheduler();
    await new Promise(resolve => setTimeout(resolve, 0));
    check(!Example.schedulerPaused(), 'Scheduler can pause again');
  } finally {
    dom.unmountComponentAtNode(root);
    root.remove();
  }
}
exports = run;
