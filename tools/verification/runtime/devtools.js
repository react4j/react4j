(async () => {
  async function until(check, message) {
    const deadline = performance.now() + 10000;
    do {
      const result = check();
      if (result) return result;
      await new Promise(resolve => setTimeout(resolve, 25));
    } while (performance.now() < deadline);
    throw Error(message);
  }
  const hook = await until(() => window.__REACT_DEVTOOLS_GLOBAL_HOOK__?.reactDevtoolsAgent &&
    window.__REACT_DEVTOOLS_GLOBAL_HOOK__, 'Extension bridge unavailable');
  const [rendererID, renderer] = await until(() => [...hook.rendererInterfaces][0], 'Renderer unavailable');
  function inspect(host, name) {
    const hostID = renderer.getElementIDForHostInstance(host);
    const owner = renderer.getOwnersList(hostID)?.find(item => item.displayName === name);
    if (!owner || renderer.getDisplayNameForElementID(owner.id) !== name) {
      throw Error('Extension cannot find the ' + name + ' component');
    }
    const data = renderer.inspectElement(1, owner.id, null, true);
    if (data.type !== 'full-data' || !data.value.props?.data ||
        !renderer.findHostInstancesForElementID(owner.id)?.some(node => node === host || node.contains(host))) {
      throw Error('Extension cannot inspect ' + name + ' or locate its DOM');
    }
    return {id: owner.id, props: data.value.props.data};
  }
  const component = 'TodoEntry';
  const input = document.querySelector('input[placeholder]');
  const {id} = inspect(input, component);
  const children = new Set(document.body.children);
  const post = (event, payload) => window.postMessage({source:'react-devtools-content-script',payload:{event,payload}}, '*');
  post('highlightHostInstance', {id, rendererID, displayName:component, hideAfterTimeout:false,
    openBuiltinElementsPanel:false, scrollIntoView:false});
  const overlay = await until(() => [...document.body.children].find(child => !children.has(child) &&
    child.textContent.includes(component) && /\d+px × \d+px/.test(child.textContent)), 'Highlight did not identify TodoEntry');
  post('clearHostInstanceHighlight');
  await until(() => !overlay.isConnected || !overlay.textContent, 'Highlight was not cleared');
  renderer.startProfiling(false);
  window.finishDevToolsProbe = () => {
    renderer.stopProfiling();
    const commits = renderer.getProfilingData().dataForRoots.flatMap(root => root.commitData);
    if (!commits.some(commit => commit.fiberActualDurations?.some(([fiber,duration]) => fiber === id && duration > 0))) {
      throw Error('Extension profile has no measured TodoEntry update');
    }
    const {props} = inspect(document.querySelector('#app li input[type=checkbox]'), 'TodoItem');
    if (!Object.hasOwn(props, 'todo')) throw Error('Extension lost the TodoItem input: ' + Object.keys(props));
    return {component, inspectedInput:'TodoItem.todo', highlight:component, commits:commits.length};
  };
})()
