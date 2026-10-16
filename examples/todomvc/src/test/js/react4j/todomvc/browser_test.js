goog.module('react4j.todomvc.BrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');

/**
 * @param {function():boolean} condition
 * @return {!Promise<void>}
 */
async function waitFor(condition) {
  const deadline = Date.now() + 10000;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error('Timed out waiting for TodoMVC');
    await new Promise(resolve => setTimeout(resolve, 20));
  }
}

/**
 * @param {string} variant
 * @return {!Promise<!HTMLIFrameElement>}
 */
async function loadApplication(variant) {
  const frame = /** @type {!HTMLIFrameElement} */ (document.createElement('iframe'));
  frame.width = '800';
  frame.height = '900';
  const loaded = new Promise(resolve => { frame.onload = () => { resolve(undefined); }; });
  // Navigate to the real page so hash routing and history use the packaged URL.
  frame.src = '/examples/todomvc/' + variant + '/' + variant + '/index.html';
  document.body.appendChild(frame);
  await loaded;
  return frame;
}

/**
 * @param {!Document} page
 * @param {string} selector
 * @return {!HTMLInputElement}
 */
function input(page, selector) {
  return /** @type {!HTMLInputElement} */ (page.querySelector(selector));
}

/**
 * @param {!HTMLInputElement} element
 * @param {string} value
 * @return {void}
 */
function setText(element, value) {
  Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set.call(element, value);
  element.dispatchEvent(new Event('input', {bubbles: true}));
}

/**
 * @param {!HTMLInputElement} element
 * @param {number} code
 * @return {void}
 */
function keyDown(element, code) {
  element.dispatchEvent(new KeyboardEvent('keydown', {keyCode: code, which: code, bubbles: true}));
}

/**
 * @param {!Document} page
 * @return {!Array<string>}
 */
function titles(page) {
  return Array.from(page.querySelectorAll('.todo-list label'), label => label.textContent);
}

/**
 * @param {!Document} page
 * @param {string} title
 * @return {!Promise<void>}
 */
async function addTodo(page, title) {
  setText(input(page, '.new-todo'), title);
  keyDown(input(page, '.new-todo'), 13);
  await waitFor(() => input(page, '.new-todo').value === '');
}

/**
 * @param {!Document} page
 * @param {string} filter
 * @return {!Promise<void>}
 */
async function filterTodos(page, filter) {
  /** @type {!HTMLAnchorElement} */ (page.querySelector('a[href="#' + filter + '"]')).click();
  await waitFor(() => page.querySelector('.filters .selected').textContent === (filter || 'all').replace(/^./, c => c.toUpperCase()));
}

/**
 * @param {!Document} page
 * @return {!Promise<!HTMLInputElement>}
 */
async function editFirst(page) {
  page.querySelector('.todo-list label').dispatchEvent(new MouseEvent('dblclick', {bubbles: true}));
  await waitFor(() => page.querySelector('.todo-list li.editing') !== null);
  return input(page, '.editing .edit');
}

/**
 * @param {string} variant
 * @return {!Promise<void>}
 */
async function exerciseApplication(variant) {
  const frame = await loadApplication(variant);
  const view = /** @type {!Window} */ (frame.contentWindow);
  const page = /** @type {!Document} */ (frame.contentDocument);
  /** @type {!Array<string>} */
  const errors = [];
  view.addEventListener('error', event => errors.push(/** @type {!ErrorEvent} */ (event).message));
  const nativeError = view.console.error.bind(view.console);
  view.console.error = (...args) => { errors.push(args.map(String).join(' ')); nativeError(...args); };
  try {
    asserts.assertEquals('todos', page.querySelector('h1').textContent);
    asserts.assertEquals('What needs to be done?', input(page, '.new-todo').placeholder);
    asserts.assertNull(page.querySelector('.footer'));
    asserts.assertEquals('550px', view.getComputedStyle(page.body).maxWidth);
    await addTodo(page, 'Read the source');
    await addTodo(page, '  Ship the example  ');
    asserts.assertArrayEquals(['Read the source', 'Ship the example'], titles(page));
    asserts.assertEquals('2 items left', page.querySelector('.todo-count').textContent);
    setText(input(page, '.new-todo'), '   ');
    keyDown(input(page, '.new-todo'), 13);
    asserts.assertEquals(2, titles(page).length);

    input(page, '.todo-list .toggle').click();
    await waitFor(() => page.querySelectorAll('.todo-list li.checked').length === 1);
    asserts.assertEquals('1 item left', page.querySelector('.todo-count').textContent);
    asserts.assertNotNull(page.querySelector('.clear-completed'));
    await filterTodos(page, 'active');
    asserts.assertArrayEquals(['Ship the example'], titles(page));
    await filterTodos(page, 'completed');
    asserts.assertArrayEquals(['Read the source'], titles(page));
    await filterTodos(page, '');
    asserts.assertEquals(2, titles(page).length);

    input(page, '.toggle-all').click();
    await waitFor(() => page.querySelectorAll('.todo-list li.checked').length === 2);
    asserts.assertEquals('0 items left', page.querySelector('.todo-count').textContent);
    input(page, '.toggle-all').click();
    await waitFor(() => page.querySelectorAll('.todo-list li.checked').length === 0);
    asserts.assertEquals('2 items left', page.querySelector('.todo-count').textContent);

    let editor = await editFirst(page);
    asserts.assertEquals('Read the source', editor.value);
    setText(editor, 'Discard this change');
    keyDown(editor, 27);
    await waitFor(() => page.querySelector('.editing') === null);
    asserts.assertEquals('Read the source', titles(page)[0]);
    editor = await editFirst(page);
    setText(editor, 'Review the source');
    keyDown(editor, 13);
    await waitFor(() => page.querySelector('.editing') === null);
    asserts.assertEquals('Review the source', titles(page)[0]);
    editor = await editFirst(page);
    setText(editor, 'Saved on blur');
    asserts.assertEquals('Edit field should receive focus', editor, page.activeElement);
    // Headless iframe focus changes do not emit native blur events.
    editor.dispatchEvent(new FocusEvent('blur', {relatedTarget: input(page, '.new-todo')}));
    await waitFor(() => page.querySelector('.editing') === null);
    asserts.assertEquals('Saved on blur', titles(page)[0]);
    editor = await editFirst(page);
    setText(editor, '');
    keyDown(editor, 13);
    await waitFor(() => titles(page).length === 1);
    asserts.assertArrayEquals(['Ship the example'], titles(page));
    /** @type {!HTMLButtonElement} */ (page.querySelector('.destroy')).click();
    await waitFor(() => page.querySelector('.footer') === null);
    asserts.assertEquals(0, titles(page).length);

    await addTodo(page, 'First');
    await addTodo(page, 'Second');
    input(page, '.toggle-all').click();
    await waitFor(() => page.querySelectorAll('.todo-list li.checked').length === 2);
    /** @type {!HTMLButtonElement} */ (page.querySelector('.clear-completed')).click();
    await waitFor(() => page.querySelector('.footer') === null);
    asserts.assertEquals(0, titles(page).length);
    await addTodo(page, 'In memory only');
    const reloaded = new Promise(resolve => { frame.onload = () => { resolve(undefined); }; });
    view.location.reload();
    await reloaded;
    const freshPage = /** @type {!Document} */ (frame.contentDocument);
    asserts.assertEquals(0, titles(freshPage).length);
    asserts.assertNull(freshPage.querySelector('.footer'));
    asserts.assertEquals('', input(freshPage, '.new-todo').value);
    asserts.assertEquals(JSON.stringify(errors), 0, errors.length);
  } finally {
    frame.remove();
  }
}

testSuite({
  setUpPage() {
    TestCase.getActiveTestCase().promiseTimeout = 60000;
  },
  testRawApplication() { return exerciseApplication('raw'); },
  testArezApplication() { return exerciseApplication('arez'); },
  testStingApplication() { return exerciseApplication('sting'); }
});
