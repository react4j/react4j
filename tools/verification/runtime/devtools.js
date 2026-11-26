(async (production, readableNames) => {
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
  const metadata = hook.renderers.get(rendererID);
  if (metadata.bundleType !== (production ? 0 : 1)) throw Error('Unexpected DevTools renderer bundle type');
  if (production && metadata.rendererPackageName !== 'react4j-dom') throw Error('Expected the optimized Java renderer');
  function inspect(host, name) {
    const hostID = renderer.getElementIDForHostInstance(host);
    const owners = renderer.getOwnersList(hostID);
    const owner = readableNames ? owners?.find(item => item.displayName === name) : owners?.findLast(item => item.type === 1);
    if (!owner || (readableNames && renderer.getDisplayNameForElementID(owner.id) !== name)) {
      throw Error('Extension cannot find the ' + name + ' component');
    }
    const data = renderer.inspectElement(1, owner.id, null, true);
    if (data.type !== 'full-data' || !data.value.props?.data ||
        !renderer.findHostInstancesForElementID(owner.id)?.some(node => node === host || node.contains(host))) {
      throw Error('Extension cannot inspect ' + name + ' or locate its DOM');
    }
    return {id: owner.id, name:renderer.getDisplayNameForElementID(owner.id), props: data.value.props.data};
  }
  const input = document.querySelector('input[placeholder]');
  const {id, name:component} = inspect(input, 'TodoEntry');
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
    let timedCommits = 0;
    let todoEntryTimedCommits = 0;
    let todoEntryPeakMs = 0;
    for (const {fiberActualDurations} of commits) {
      if (fiberActualDurations?.some(([, duration]) => duration > 0)) timedCommits++;
      const entry = fiberActualDurations?.find(([fiber, duration]) => fiber === id && duration > 0);
      if (entry) {
        todoEntryTimedCommits++;
        todoEntryPeakMs = Math.max(todoEntryPeakMs, entry[1]);
      }
    }
    if (!todoEntryTimedCommits) {
      throw Error('Extension profile has no measured TodoEntry update');
    }
    const {props} = inspect(document.querySelector('#app li input[type=checkbox]'), 'TodoItem');
    const keys = Object.keys(props);
    if (readableNames ? !Object.hasOwn(props, 'todo') : !keys.length) throw Error('Extension lost the TodoItem input');
    return {
      observation:{component, inspectedInput:readableNames ? 'TodoItem.todo' : keys.join(','), highlight:component, commits:commits.length},
      timing:{timedCommits, todoEntryTimedCommits, todoEntryPeakMs},
    };
  };
})
