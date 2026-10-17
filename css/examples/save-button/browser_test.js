goog.module('cssproof.BrowserTest');
const Example = goog.require('org.realityforge.css.example.BrowserExample');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');

/** @type {!HTMLElement} */
let container;
/** @type {!HTMLStyleElement} */
let style;

/**
 * @param {string} id
 * @return {!CSSStyleDeclaration}
 */
function computed(id) {
  return /** @type {!CSSStyleDeclaration} */ (getComputedStyle(/** @type {!Element} */ (container.querySelector('#' + id))));
}

/**
 * @param {!CSSRuleList} list
 * @return {!Array<!CSSRule>}
 */
function ruleArray(list) {
  /** @type {!Array<!CSSRule>} */
  const rules = [];
  for (let i = 0; i < list.length; i++) rules.push(/** @type {!CSSRule} */ (list.item(i)));
  return rules;
}

/** @return {!Array<string>} */
function snapshot() {
  /** @type {!Array<string>} */
  const ids = ['base', 'success', 'cancel', 'custom', 'disabled', 'conflict', 'nested', 'status'];
  /** @type {!Array<string>} */
  const result = [];
  for (let i = 0; i < ids.length; i++) {
    const css = computed(ids[i]);
    result.push([css.color, css.backgroundColor, css.border, css.opacity].join('|'));
  }
  return result;
}

testSuite({
  async setUp() {
    container = /** @type {!HTMLElement} */ (document.createElement('div'));
    document.body.appendChild(container);
    style = /** @type {!HTMLStyleElement} */ (document.createElement('style'));
    style.textContent = await (await fetch('/css/examples/save-button/styles.css')).text();
    document.head.appendChild(style);
    Example.render(container);
  },
  tearDown() {
    container.remove();
    style.remove();
  },
  testGeneratedBindingsAndCascade() {
    asserts.assertEquals('rgb(255, 255, 255)', computed('base').color);
    asserts.assertEquals('rgb(0, 136, 0)', computed('success').backgroundColor);
    asserts.assertEquals('rgb(170, 0, 0)', computed('cancel').backgroundColor);
    asserts.assertEquals('dotted', computed('cancel').borderTopStyle);
    asserts.assertEquals('rgb(0, 68, 204)', computed('custom').backgroundColor);
    asserts.assertEquals('0.5', computed('disabled').opacity);
    // Authored modifier order wins, independently of the order of class tokens.
    asserts.assertEquals('rgb(170, 0, 0)', computed('conflict').backgroundColor);
    const conflict = /** @type {!HTMLElement} */ (container.querySelector('#conflict'));
    conflict.className = conflict.className.split(' ').reverse().join(' ');
    asserts.assertEquals('rgb(170, 0, 0)', computed('conflict').backgroundColor);
    // The parent's cancel modifier does not style a nested component instance.
    asserts.assertEquals('rgb(51, 51, 51)', computed('nested').backgroundColor);
    asserts.assertEquals('rgb(0, 136, 0)', computed('status').borderTopColor);
  },
  testFocusAndIndependentFragmentReordering() {
    const button = /** @type {!HTMLButtonElement} */ (container.querySelector('#base'));
    button.focus();
    asserts.assertTrue(document.hasFocus());
    asserts.assertTrue(button.matches(':focus-visible'));
    asserts.assertEquals('2px', computed('base').outlineWidth);
    asserts.assertEquals('rgb(0, 102, 255)', computed('base').outlineColor);
    const before = snapshot();
    const sheet = /** @type {!CSSStyleSheet} */ (style.sheet);
    /** @type {?CSSGroupingRule} */
    let found = null;
    const sheetRules = ruleArray(/** @type {!CSSRuleList} */ (sheet.cssRules));
    for (let i = 0; i < sheetRules.length; i++) {
      if (sheetRules[i].cssText.startsWith('@layer components {')) {
        found = /** @type {!CSSGroupingRule} */ (sheetRules[i]);
        break;
      }
    }
    asserts.assertNotNull(found);
    const layer = /** @type {!CSSGroupingRule} */ (found);
    const componentRules = /** @type {!CSSRuleList} */ (layer.cssRules);
    /** @type {!Map<string, !Array<string>>} */
    const groups = new Map();
    /** @type {!Array<!Array<string>>} */
    const ordered = [];
    const rules = ruleArray(componentRules);
    for (let i = 0; i < rules.length; i++) {
      const match = rules[i].cssText.match(/\.([A-Za-z0-9_-]+)/);
      if (match === null) throw new Error('Expected a scoped component selector');
      const captured = /** @type {string} */ (match[1]);
      const owner = captured.split('--')[0];
      let group = groups.get(owner);
      if (!group) { group = []; groups.set(owner, group); ordered.push(group); }
      group.push(rules[i].cssText);
    }
    asserts.assertEquals(2, groups.size);
    while (componentRules.length) layer.deleteRule(0);
    for (let i = ordered.length - 1; i >= 0; i--) {
      const group = ordered[i];
      for (let j = 0; j < group.length; j++) layer.insertRule(group[j], componentRules.length);
    }
    asserts.assertArrayEquals(before, snapshot());
    asserts.assertEquals('2px', computed('base').outlineWidth);
  },
});
