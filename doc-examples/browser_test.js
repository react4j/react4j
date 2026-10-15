goog.module('react4j.examples.BrowserTest');
const Example = goog.require('react4j.examples.hello_world.BrowserExample');
const HelloWorld = goog.require('react4j.examples.hello_world.HelloWorld');
const Mode = goog.require('react4j.browser.Mode');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');
/** @type {!Element} */
let root;
testSuite({
  setUp() {
    root = document.createElement('div');
    root.id = 'app';
    document.body.appendChild(root);
  },
  tearDown() {
    Example.unmount(root);
    root.remove();
  },
  testHelloWorld() {
    HelloWorld.bootstrap();
    asserts.assertEquals('Hello World', root.textContent);
    asserts.assertEquals('H1', root.firstElementChild.tagName);
  },
  testGeneratedViewInputsAndContextUpdates() {
    Example.render(root, 1, 'first', 'light');
    asserts.assertEquals('1:first:light', root.textContent);
    const element = root.firstElementChild;
    Example.render(root, 2, null, 'dark');
    asserts.assertEquals('2:null:dark', root.textContent);
    asserts.assertEquals(element, root.firstElementChild);
    Example.render(root, 3, 'last', null);
    asserts.assertEquals('3:last:null', root.textContent);
    asserts.assertTrue(Example.unmount(root));
    asserts.assertEquals('', root.textContent);
    asserts.assertFalse(Example.unmount(root));
  },
  testConfiguration() {
    asserts.assertEquals('development' === Mode.environment ?
      'true:true:false:true:true' : 'false:false:true:false:false', Example.flags());
  },
  testPortalAndBatchedRendering() {
    const target = document.createElement('div');
    document.body.appendChild(target);
    try {
      Example.renderPortal(root, target);
      asserts.assertEquals('container', root.textContent);
      asserts.assertEquals('portal', target.textContent);
      asserts.assertTrue(Example.unmount(root));
      asserts.assertEquals('', target.textContent);
    } finally {
      target.remove();
    }
  },
});
