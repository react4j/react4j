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
  return Array.from(page.querySelectorAll('#app ul label'), label => label.textContent);
}

/**
 * @param {!Document} page
 * @param {string} title
 * @return {!Promise<void>}
 */
async function addTodo(page, title) {
  setText(input(page, 'input[placeholder]'), title);
  keyDown(input(page, 'input[placeholder]'), 13);
  await waitFor(() => input(page, 'input[placeholder]').value === '');
}

/**
 * @param {!Document} page
 * @param {string} filter
 * @return {!Promise<void>}
 */
async function filterTodos(page, filter) {
  /** @type {!HTMLAnchorElement} */ (page.querySelector('a[href="#' + filter + '"]')).click();
  const view = /** @type {!Window} */ (page.defaultView);
  await waitFor(() => view.getComputedStyle(page.querySelector('a[href="#' + filter + '"]')).borderTopColor === 'rgba(175, 47, 47, 0.2)');
}

/**
 * @param {!Document} page
 * @return {!Promise<!HTMLInputElement>}
 */
async function editFirst(page) {
  page.querySelector('#app ul label').dispatchEvent(new MouseEvent('dblclick', {bubbles: true}));
  await waitFor(() => editingInput(page) !== null);
  return /** @type {!HTMLInputElement} */ (editingInput(page));
}

/**
 * @param {!Document} page
 * @return {?HTMLInputElement}
 */
function editingInput(page) {
  const view = /** @type {!Window} */ (page.defaultView);
  for (const element of page.querySelectorAll('#app li > input')) {
    if (view.getComputedStyle(element).display !== 'none') {
      return /** @type {!HTMLInputElement} */ (element);
    }
  }
  return null;
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
    asserts.assertEquals('What needs to be done?', input(page, 'input[placeholder]').placeholder);
    asserts.assertNull(page.querySelector('#app article footer'));
    const panel = page.querySelector('#app article');
    const info = page.querySelector('#app > div > footer');
    asserts.assertEquals('550px', view.getComputedStyle(panel.parentElement).maxWidth);
    asserts.assertEquals('rgb(245, 245, 245)', view.getComputedStyle(page.body).backgroundColor);
    asserts.assertEquals('rgb(255, 255, 255)', view.getComputedStyle(panel).backgroundColor);
    asserts.assertEquals('14px', view.getComputedStyle(panel).fontSize);
    asserts.assertEquals('10px', view.getComputedStyle(info).fontSize);
    asserts.assertEquals('10px', view.getComputedStyle(info.querySelector('p')).lineHeight);
    asserts.assertEquals('400', view.getComputedStyle(info.querySelector('a')).fontWeight);
    asserts.assertEquals(550, panel.getBoundingClientRect().width);
    asserts.assertEquals(130, panel.getBoundingClientRect().top);
    asserts.assertRoughlyEquals(65, info.getBoundingClientRect().top - panel.getBoundingClientRect().bottom, 0.1);
    const outside = page.createElement('button');
    page.body.appendChild(outside);
    asserts.assertNotEquals('Todo control resets must remain scoped', '0px', view.getComputedStyle(outside).borderTopWidth);
    outside.remove();
    await addTodo(page, 'Read the source');
    await addTodo(page, '  Ship the example  ');
    asserts.assertArrayEquals(['Read the source', 'Ship the example'], titles(page));
    asserts.assertEquals('2 items left', page.querySelector('#app article footer > span').textContent);
    asserts.assertEquals('none', view.getComputedStyle(page.querySelector('#app ul label')).textDecorationLine);
    asserts.assertEquals('Delete controls stay hidden until row hover', 'none',
        view.getComputedStyle(page.querySelector('#app li button')).display);
    setText(input(page, 'input[placeholder]'), '   ');
    keyDown(input(page, 'input[placeholder]'), 13);
    asserts.assertEquals(2, titles(page).length);

    input(page, '#app li input[type=checkbox]').click();
    await waitFor(() => page.querySelectorAll('#app li input[type=checkbox]:checked').length === 1);
    asserts.assertEquals('1 item left', page.querySelector('#app article footer > span').textContent);
    const toggle = input(page, '#app li input[type=checkbox]');
    const toggleUrl = view.getComputedStyle(toggle, '::after').content;
    asserts.assertTrue(toggleUrl.includes('/assets/'));
    asserts.assertFalse(toggleUrl.includes('asset:'));
    await waitFor(() => view.performance.getEntriesByType('resource').filter(entry => /\/assets\/[^/]+\.svg$/.test(entry.name)).length === 2);
    const completedLabel = /** @type {!HTMLInputElement} */ (page.querySelector('#app li input[type=checkbox]:checked')).nextElementSibling;
    asserts.assertEquals('line-through', view.getComputedStyle(completedLabel).textDecorationLine);
    await waitFor(() => view.getComputedStyle(completedLabel).color === 'rgb(217, 217, 217)');
    asserts.assertNotNull(page.querySelector('#app article footer button'));
    const clear = view.getComputedStyle(page.querySelector('#app article footer button'));
    asserts.assertEquals('0px', clear.borderTopWidth);
    asserts.assertEquals('rgba(0, 0, 0, 0)', clear.backgroundColor);
    asserts.assertEquals('14px', clear.fontSize);
    asserts.assertEquals('none', clear.outlineStyle);
    asserts.assertEquals('none', view.getComputedStyle(input(page, '#app li input[type=checkbox]')).outlineStyle);
    asserts.assertEquals('none', view.getComputedStyle(input(page, '#app section > input[type=checkbox]')).outlineStyle);
    await filterTodos(page, 'active');
    asserts.assertArrayEquals(['Ship the example'], titles(page));
    await filterTodos(page, 'completed');
    asserts.assertArrayEquals(['Read the source'], titles(page));
    await filterTodos(page, '');
    asserts.assertEquals(2, titles(page).length);

    input(page, '#app section > input[type=checkbox]').click();
    await waitFor(() => page.querySelectorAll('#app li input[type=checkbox]:checked').length === 2);
    asserts.assertEquals('0 items left', page.querySelector('#app article footer > span').textContent);
    input(page, '#app section > input[type=checkbox]').click();
    await waitFor(() => page.querySelectorAll('#app li input[type=checkbox]:checked').length === 0);
    asserts.assertEquals('2 items left', page.querySelector('#app article footer > span').textContent);
    asserts.assertEquals('none', view.getComputedStyle(page.querySelector('#app ul label')).textDecorationLine);
    frame.width = '200';
    await waitFor(() => 230 === panel.getBoundingClientRect().width);
    frame.width = '400';
    await waitFor(() => 400 === panel.getBoundingClientRect().width);
    await waitFor(() => view.getComputedStyle(page.querySelector('#app article footer')).height === '50px');
    frame.width = '800';
    await waitFor(() => view.getComputedStyle(page.querySelector('#app article footer')).height === '20px');

    let editor = await editFirst(page);
    asserts.assertEquals('Read the source', editor.value);
    asserts.assertEquals('block', view.getComputedStyle(editor).display);
    asserts.assertEquals('none', view.getComputedStyle(editor.previousElementSibling).display);
    setText(editor, 'Discard this change');
    keyDown(editor, 27);
    await waitFor(() => editingInput(page) === null);
    asserts.assertEquals('Read the source', titles(page)[0]);
    editor = await editFirst(page);
    setText(editor, 'Review the source');
    keyDown(editor, 13);
    await waitFor(() => editingInput(page) === null);
    asserts.assertEquals('Review the source', titles(page)[0]);
    editor = await editFirst(page);
    setText(editor, 'Saved on blur');
    asserts.assertEquals('Edit field should receive focus', editor, page.activeElement);
    // Headless iframe focus changes do not emit native blur events.
    editor.dispatchEvent(new FocusEvent('blur', {relatedTarget: input(page, 'input[placeholder]')}));
    await waitFor(() => editingInput(page) === null);
    asserts.assertEquals('Saved on blur', titles(page)[0]);
    editor = await editFirst(page);
    setText(editor, '');
    keyDown(editor, 13);
    await waitFor(() => titles(page).length === 1);
    asserts.assertArrayEquals(['Ship the example'], titles(page));
    /** @type {!HTMLButtonElement} */ (page.querySelector('#app li button')).click();
    await waitFor(() => page.querySelector('#app article footer') === null);
    asserts.assertEquals(0, titles(page).length);

    await addTodo(page, 'First');
    await addTodo(page, 'Second');
    input(page, '#app section > input[type=checkbox]').click();
    await waitFor(() => page.querySelectorAll('#app li input[type=checkbox]:checked').length === 2);
    /** @type {!HTMLButtonElement} */ (page.querySelector('#app article footer button')).click();
    await waitFor(() => page.querySelector('#app article footer') === null);
    asserts.assertEquals(0, titles(page).length);
    await addTodo(page, 'In memory only');
    const reloaded = new Promise(resolve => { frame.onload = () => { resolve(undefined); }; });
    view.location.reload();
    await reloaded;
    const freshPage = /** @type {!Document} */ (frame.contentDocument);
    asserts.assertEquals(0, titles(freshPage).length);
    asserts.assertNull(freshPage.querySelector('#app article footer'));
    asserts.assertEquals('', input(freshPage, 'input[placeholder]').value);
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
