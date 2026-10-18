goog.module('react4j.css.example.BrowserTest');
const Example = goog.require('react4j.css.example.BrowserExample');
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
    style.textContent = await (await fetch('/css/examples/save-button/src/test/js/react4j/css/example/styles.css')).text();
    document.head.appendChild(style);
    Example.render(container, "#04c", "true");
  },
  tearDown() {
    container.remove();
    style.remove();
  },
  testGeneratedBindingsAreSingletons() {
    asserts.assertTrue(Example.bindingsAreSingletons());
  },
  testInterfaceImplementationsKeepIndependentClasses() {
    const button = /** @type {!HTMLElement} */ (container.querySelector('#success'));
    const status = /** @type {!HTMLElement} */ (container.querySelector('#status'));
    asserts.assertNotEquals(button.classList.item(0), status.classList.item(0));
    asserts.assertNotEquals(button.classList.item(1), status.classList.item(1));
    asserts.assertEquals('rgb(0, 136, 0)', computed('success').backgroundColor);
    asserts.assertEquals('rgb(0, 136, 0)', computed('status').borderTopColor);
    asserts.assertEquals('3px', computed('status').borderTopWidth);
  },
  async testSharedKeyframesAreReusedAndUnreferencedDefinitionsAreExcluded() {
    const button = /** @type {!HTMLElement} */ (container.querySelector('#busy-button'));
    const status = /** @type {!HTMLElement} */ (container.querySelector('#busy-status'));
    const name = computed('busy-button').animationName;
    asserts.assertNotEquals('none', name);
    asserts.assertEquals(name, computed('busy-status').animationName);
    asserts.assertEquals('0.8s', computed('busy-button').animationDuration);
    asserts.assertEquals('0.8s', computed('busy-status').animationDuration);
    asserts.assertEquals('infinite', computed('busy-status').animationIterationCount);
    asserts.assertEquals(1, button.getAnimations().length);
    asserts.assertEquals(1, status.getAnimations().length);
    const animation = button.getAnimations()[0];
    animation.pause();
    await animation.ready;
    animation.currentTime = 400;
    const opacity = Number(computed('busy-button').opacity);
    asserts.assertTrue(opacity > 0.4 && opacity < 1);
    const sheet = /** @type {!CSSStyleSheet} */ (style.sheet);
    /** @type {!Array<string>} */
    const names = [];
    for (const rule of ruleArray(/** @type {!CSSRuleList} */ (sheet.cssRules))) {
      if (!rule.cssText.startsWith('@layer components {')) continue;
      const layer = /** @type {!CSSGroupingRule} */ (rule);
      for (const child of ruleArray(layer.cssRules)) {
        if (child.cssText.startsWith('@keyframes')) names.push(/** @type {!CSSKeyframesRule} */ (child).name);
      }
    }
    const expected = [name, computed('direct-animation').animationName,
      computed('local-animation').animationName, computed('accessor-animation').animationName];
    asserts.assertEquals(4, new Set(expected).size);
    asserts.assertArrayEquals(expected.sort(), names.sort());
  },
  testCheckedIdsFragmentsAndJavaOnlyResourceBindings() {
    const control = /** @type {!HTMLElement} */ (container.querySelector('[data-symbol=control]'));
    const filter = /** @type {!SVGFilterElement} */ (container.querySelector('filter'));
    const global = /** @type {!HTMLElement} */ (container.querySelector('[data-symbol=global]'));
    asserts.assertTrue(control.id !== 'control');
    asserts.assertEquals('5px', getComputedStyle(control).borderBottomWidth);
    asserts.assertTrue(getComputedStyle(control).filter.includes('#' + filter.id));
    asserts.assertEquals(filter, document.getElementById(filter.id));
    asserts.assertEquals('9px', getComputedStyle(global).letterSpacing);
    /** @type {!Array<string>} */
    const animatedIds = ['direct-animation', 'local-animation', 'accessor-animation'];
    for (let i = 0; i < animatedIds.length; i++) {
      const id = /** @type {string} */ (animatedIds[i]);
      const node = /** @type {!HTMLElement} */ (container.querySelector('#' + id));
      asserts.assertEquals(1, node.getAnimations().length);
      asserts.assertEquals(node.style.animationName, getComputedStyle(node).animationName);
    }
  },
  async testExplicitGlobalSelectionAndDependencyFirstCascade() {
    // Defaults is also a later explicit root; its first visit precedes Theme.
    asserts.assertEquals('3px', computed('base').borderTopWidth);
    asserts.assertEquals('rgb(153, 0, 153)', getComputedStyle(/** @type {!Element} */ (container.querySelector('[data-symbol=control]'))).backgroundColor);
    asserts.assertEquals('', computed('base').getPropertyValue('--jcss-react4j-css-example-BrowserExample_0024_UnusedGlobals--excluded'));
    style.textContent = await (await fetch('/css/examples/save-button/src/test/js/react4j/css/example/alternate.css')).text();
    asserts.assertEquals('4px', computed('base').borderTopWidth);
    asserts.assertEquals('rgb(0, 153, 153)', getComputedStyle(/** @type {!Element} */ (container.querySelector('[data-symbol=control]'))).backgroundColor);
    asserts.assertEquals('', computed('base').getPropertyValue('--jcss-react4j-css-example-BrowserExample_0024_UnusedGlobals--excluded'));
    asserts.assertEquals('rgb(0, 136, 0)', computed('success').backgroundColor);
  },
  async testComponentImportsRetainGlobalsWithoutExplicitRoots() {
    style.textContent = await (await fetch('/css/examples/save-button/src/test/js/react4j/css/example/component.css')).text();
    asserts.assertEquals('2px', computed('base').borderTopWidth);
    asserts.assertEquals('rgb(0, 136, 0)', getComputedStyle(/** @type {!Element} */ (container.querySelector('[data-symbol=control]'))).backgroundColor);
    asserts.assertEquals('', computed('base').getPropertyValue('--jcss-react4j-css-example-BrowserExample_0024_UnusedGlobals--excluded'));
    asserts.assertEquals('rgb(255, 255, 255)', computed('base').color);
    asserts.assertEquals('rgb(0, 136, 0)', computed('success').backgroundColor);
  },
  testCheckedPeerSelectorFollowsFocus() {
    const button = /** @type {!HTMLButtonElement} */ (container.querySelector('#peer-button'));
    asserts.assertEquals('rgb(255, 255, 255)', computed('peer-label').color);
    button.focus();
    asserts.assertTrue(button.matches(':focus-visible'));
    asserts.assertEquals('rgb(0, 136, 0)', computed('peer-label').color);
    asserts.assertEquals('rgb(255, 255, 255)', computed('busy-status').color);
    button.blur();
    asserts.assertEquals('rgb(255, 255, 255)', computed('peer-label').color);
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
  testTypedPropertiesUpdateAndRemoveValues() {
    const custom = /** @type {!HTMLElement} */ (container.querySelector('#custom'));
    const base = /** @type {!HTMLElement} */ (container.querySelector('#base'));
    asserts.assertEquals('#04c', custom.style.getPropertyValue(Example.successProperty()));
    asserts.assertEquals('true', base.getAttribute('data-shared'));

    Example.render(container, '#a0c', 'false');
    asserts.assertEquals(custom, container.querySelector('#custom'));
    asserts.assertEquals(base, container.querySelector('#base'));
    asserts.assertEquals('#a0c', custom.style.getPropertyValue(Example.successProperty()));
    asserts.assertEquals('rgb(170, 0, 204)', computed('custom').backgroundColor);
    asserts.assertEquals('false', base.getAttribute('data-shared'));

    Example.render(container, null, null);
    asserts.assertEquals(custom, container.querySelector('#custom'));
    asserts.assertEquals(base, container.querySelector('#base'));
    asserts.assertEquals('', custom.style.getPropertyValue(Example.successProperty()));
    asserts.assertEquals('rgb(0, 136, 0)', computed('custom').backgroundColor);
    asserts.assertFalse(base.hasAttribute('data-shared'));
    asserts.assertFalse(/** @type {!Element} */ (container.querySelector('#nested')).hasAttribute('data-shared'));

    Example.render(container, '', '');
    asserts.assertEquals('', custom.style.getPropertyValue(Example.successProperty()));
    asserts.assertTrue(base.hasAttribute('data-shared'));
    asserts.assertEquals('', base.getAttribute('data-shared'));
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
    /** @type {!Array<string>} */
    const animations = [];
    const rules = ruleArray(componentRules);
    for (let i = 0; i < rules.length; i++) {
      if (rules[i].cssText.startsWith('@keyframes')) {
        animations.push(rules[i].cssText);
        continue;
      }
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
    for (const animation of animations) layer.insertRule(animation, componentRules.length);
    for (let i = ordered.length - 1; i >= 0; i--) {
      const group = ordered[i];
      for (let j = 0; j < group.length; j++) layer.insertRule(group[j], componentRules.length);
    }
    asserts.assertArrayEquals(before, snapshot());
    asserts.assertEquals('2px', computed('base').outlineWidth);
  },
});
