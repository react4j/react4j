goog.module('react4j.dom.RuntimeTest');
const Example = goog.require('react4j.dom.RuntimeCoverageExample');

/**
 * @param {boolean} condition
 * @param {string} message
 */
function check(condition, message) {
  if (!condition) throw new Error(message);
}

/**
 * @param {function(): boolean} condition
 * @return {!Promise<void>}
 */
async function until(condition) {
  const deadline = Date.now() + 5000;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error('React root did not commit');
    await new Promise(resolve => setTimeout(resolve, 25));
  }
}

/** @return {!Promise<void>} */
async function run() {
  Example.classNames();
  Example.batching();
  const container = document.createElement('div');
  const target = document.createElement('div');
  document.body.appendChild(container);
  document.body.appendChild(target);
  try {
    let rendered = 0;
    Example.render(container, true, () => rendered++);
    check(1 === rendered, 'Render callback');
    const button = container.firstElementChild;
    check('true' === button.getAttribute('data-shared'), 'Data attribute');
    check('rgb(0, 68, 204)' === getComputedStyle(button).backgroundColor, 'CSS custom property');
    Example.render(container, false, null);
    check(button === container.firstElementChild, 'DOM reuse');
    check(!button.hasAttribute('data-shared'), 'Null data removes the attribute');
    check('' === button.style.getPropertyValue('--accent') && '' === button.style.backgroundColor, 'Null styles remove CSS properties');
    Example.portal(container, target);
    check('container' === container.textContent && 'portal' === target.textContent, 'Portal render');
    check(Example.unmount(container), 'Mounted tree unmount');
    check(!Example.unmount(container), 'Repeated unmount');
    check('' === target.textContent, 'Portal cleanup');
    const root = Example.root(container);
    Example.renderRoot(root, 'first');
    await until(() => 'first' === container.textContent);
    await new Promise(resolve => Example.renderRootCallback(root, 'second', resolve));
    check('second' === container.textContent, 'Root render callback');
    Example.unmountRoot(root);
    await until(() => '' === container.textContent);
    const other = Example.root(target);
    await new Promise(resolve => Example.renderRootCallback(other, 'other', resolve));
    await new Promise(resolve => Example.unmountRootCallback(other, resolve));
    check('' === target.textContent, 'Root unmount callback');
  } finally {
    container.remove();
    target.remove();
  }
}
exports = run;
