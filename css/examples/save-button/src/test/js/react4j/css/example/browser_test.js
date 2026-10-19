goog.module('react4j.css.example.BrowserTest');
const Example = goog.require('react4j.css.example.BrowserExample');
const Mode = goog.require('react4j.browser.Mode');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');
/** @define {string} */
const variant = goog.define('react4j.css.example.variant', '');

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
  async testPackagedAssetsAtAnApplicationMountAndNestedRoute() {
    const root = variant ? '/css/examples/save-button/src/test/js/react4j/css/example/' +
      variant.slice(0, -1) + '_assets/' : '/css/examples/save-button/asset_example/';
    const inventory = /** @type {!Object<string, *>} */ (await (await fetch(root.slice(0, -1) + '.manifest.json')).json());
    const entries = /** @type {!Array<!Object<string, *>>} */ (inventory['assets']);
    /**
     * @param {string} suffix
     * @return {string}
     */
    function assetUrl(suffix) {
      const entry = entries.find(item => String(item['logicalPath']).endsWith(suffix));
      if (!entry) throw new Error('Missing retained asset: ' + suffix);
      return String(entry['url']);
    }
    /** @type {!Array<string>} */
    const expected = ['card/icon.svg', 'card/react4j.png', 'card/prepared.svg', 'card/background.svg', 'Righteous-Regular.ttf'];
    for (const suffix of expected) {
      asserts.assertTrue(/^assets\/[A-Za-z0-9_-]{43}\.(svg|png|ttf)$/.test(assetUrl(suffix)));
    }
    const optimized = 'production' === Mode.environment;
    asserts.assertEquals(!optimized, entries.some(item => String(item['logicalPath']).endsWith('card/unused.svg')));
    const precache = /** @type {!Array<string>} */ (await (await fetch(root + 'precache.json')).json());
    const onDemand = /** @type {!Array<string>} */ (await (await fetch(root + 'on-demand.json')).json());
    const selected = ['app.js', assetUrl('card/icon.svg')];
    if (!optimized) selected.push(assetUrl('card/unused.svg'));
    asserts.assertArrayEquals(selected.sort(), precache);
    for (const suffix of expected.slice(1)) asserts.assertTrue(onDemand.includes(assetUrl(suffix)));
    asserts.assertFalse(onDemand.includes(assetUrl('card/icon.svg')));
    asserts.assertTrue((await (await fetch(root + assetUrl('card/prepared.svg'))).text()).includes('fill="#04c"'));
    /** @type {!Array<string>} */
    const routes = ['index.html', 'nested/page.html'];
    for (const route of routes) {
      const frame = /** @type {!HTMLIFrameElement} */ (document.createElement('iframe'));
      /** @type {!Promise<void>} */
      const loaded = new Promise(resolve => { frame.onload = () => { resolve(undefined); }; });
      frame.src = root + route;
      document.body.appendChild(frame);
      try {
        await loaded;
        const page = /** @type {!HTMLDocument} */ (frame.contentDocument);
        const view = /** @type {!Window} */ (frame.contentWindow);
        const deadline = Date.now() + 10000;
        while (!page.querySelector('#card')) {
          if (Date.now() > deadline) throw new Error('Packaged asset application did not render: ' + route);
          await new Promise(resolve => setTimeout(resolve, 20));
        }
        const base = new URL(root, location.href).href;
        /** @type {!Array<!Array<string>>} */
        const images = [['icon', 'card/icon.svg'], ['photo', 'card/react4j.png'], ['prepared', 'card/prepared.svg']];
        for (const pair of images) {
          const image = /** @type {!HTMLImageElement} */ (page.getElementById(pair[0]));
          await image.decode();
          asserts.assertTrue(image.naturalWidth > 0);
          asserts.assertEquals(base + assetUrl(pair[1]), image.src);
        }
        const card = /** @type {!HTMLElement} */ (page.getElementById('card'));
        const css = view.getComputedStyle(card);
        asserts.assertTrue(css.backgroundImage.includes(base + assetUrl('card/background.svg')));
        const family = css.fontFamily.split(',')[0];
        const fonts = await page.fonts.load('16px ' + family);
        asserts.assertEquals(1, fonts.length);
        asserts.assertEquals('loaded', fonts[0].status);
        await page.fonts.ready;
        /** @type {!Array<string>} */
        const requested = view.performance.getEntriesByType('resource').map(entry => entry.name);
        for (const suffix of expected) asserts.assertTrue(requested.includes(base + assetUrl(suffix)));
        asserts.assertFalse(requested.some(name => name.endsWith('precache.json') || name.endsWith('on-demand.json')));
        if (!optimized) asserts.assertFalse(requested.includes(base + assetUrl('card/unused.svg')));
      } finally {
        frame.remove();
      }
    }
  },
  async setUp() {
    container = /** @type {!HTMLElement} */ (document.createElement('div'));
    document.body.appendChild(container);
    style = /** @type {!HTMLStyleElement} */ (document.createElement('style'));
    style.textContent = await (await fetch('/css/examples/save-button/src/test/js/react4j/css/example/' + variant + 'styles.css')).text();
    document.head.appendChild(style);
    Example.render(container, "#04c", "true");
  },
  tearDown() {
    container.remove();
    style.remove();
  },
  testClassNamingPolicy() {
    const root = /** @type {!Element} */ (container.querySelector('#success')).classList.item(0);
    const control = /** @type {!HTMLElement} */ (container.querySelector('[data-symbol=control]'));
    const property = Example.successProperty();
    const animation = computed('direct-animation').animationName;
    asserts.assertNotNull(root);
    if (Mode.environment === 'production' && variant !== 'uncompressed-') {
      asserts.assertFalse(root.startsWith('jcss-'));
      asserts.assertFalse(control.id.startsWith('jcss-'));
      asserts.assertFalse(property.startsWith('--jcss-'));
      asserts.assertFalse(animation.startsWith('jcss-'));
      asserts.assertEquals(variant === 'prefixed-', control.id.startsWith('app-'));
      asserts.assertEquals(variant === 'prefixed-', property.startsWith('--app-'));
      asserts.assertEquals(variant === 'prefixed-', animation.startsWith('app-'));
      asserts.assertEquals(variant === 'prefixed-', root.startsWith('app-'));
    } else {
      asserts.assertTrue(root.startsWith('jcss-'));
      asserts.assertTrue(control.id.startsWith('jcss-'));
      asserts.assertTrue(property.startsWith('--jcss-'));
      asserts.assertTrue(animation.startsWith('jcss-'));
    }
  },
  testGeneratedBindingsAreSingletons() {
    asserts.assertTrue(Example.bindingsAreSingletons());
  },
  testInliningKeepsModifierAndRootWhileDeadValuesLoseTheirCss() {
    asserts.assertEquals('rgb(12, 34, 56)', computed('modifier-only').color);
    asserts.assertEquals('7px', computed('modifier-only').borderTopWidth);
    const optimized = Mode.environment === 'production';
    asserts.assertEquals(!optimized, style.textContent.includes('outline-width:17px'));
    asserts.assertEquals(!optimized, style.textContent.includes('98,76,54'));
    asserts.assertEquals(!optimized, style.textContent.includes('--keyframes-unused'));
    asserts.assertEquals(!optimized, style.textContent.includes('47px'));
    asserts.assertFalse(style.textContent.includes('$react4j$css$'));
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
    if (Mode.environment !== 'production') {
      expected.push(/** @type {!HTMLElement} */ (container.querySelector('[data-symbol=equal-names]')).id);
    }
    asserts.assertEquals(Mode.environment === 'production' ? 0 : 1,
      names.filter(name => name.endsWith('--keyframes-unused')).length);
    asserts.assertArrayEquals(expected.sort(), names.filter(name => !name.endsWith('--keyframes-unused')).sort());
  },
  testEqualReadablePropertyAliasesKeepOnlyTheirOwnProvider() {
    const node = /** @type {!HTMLElement} */ (container.querySelector('[data-symbol=property-aliases]'));
    const actual = getComputedStyle(node);
    const optimized = Mode.environment === 'production';
    asserts.assertEquals(optimized ? '13px' : '37px', actual.paddingLeft);
    asserts.assertEquals(optimized ? '0px' : '37px', actual.borderRightWidth);
    asserts.assertEquals(!optimized, style.textContent.includes('border-right:37px'));
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
    asserts.assertEquals('direct-global', global.id);
    asserts.assertEquals('9px', global.style.getPropertyValue('--spacing'));
    /** @type {!Array<string>} */
    const animatedIds = ['direct-animation', 'local-animation', 'accessor-animation'];
    for (let i = 0; i < animatedIds.length; i++) {
      const id = /** @type {string} */ (animatedIds[i]);
      const node = /** @type {!HTMLElement} */ (container.querySelector('#' + id));
      asserts.assertEquals(1, node.getAnimations().length);
      asserts.assertEquals(node.style.animationName, getComputedStyle(node).animationName);
    }
  },
  testEqualGetterTextPreservesBindingKindAndLiveness() {
    const node = /** @type {!HTMLElement} */ (container.querySelector('[data-symbol=equal-names]'));
    asserts.assertEquals('jcss-react4j-css-example-BrowserExample_0024_EqualNamePulse--keyframes', node.id);
    asserts.assertEquals('11px', getComputedStyle(node).borderTopWidth);
    asserts.assertEquals(Mode.environment !== 'production', style.textContent.includes('opacity:0.12345'));
  },
  testEqualImportedIdsKeepOnlyTheLiveProviderChain() {
    const node = /** @type {!HTMLElement} */ (container.querySelector('[data-symbol=shared-import]'));
    const css = getComputedStyle(node);
    asserts.assertEquals('shared-import', node.id);
    asserts.assertEquals('13px', css.paddingLeft);
    asserts.assertEquals('19px', css.marginLeft);
    asserts.assertEquals('23px', css.borderLeftWidth);
    asserts.assertEquals('29px', css.marginRight);
    asserts.assertEquals('700', css.fontWeight);
    asserts.assertEquals(Mode.environment === 'production' ? '0px' : '37px', css.paddingRight);
    asserts.assertEquals(Mode.environment !== 'production', style.textContent.includes('padding-right:37px'));
  },
  async testExplicitGlobalSelectionAndDependencyFirstCascade() {
    // Defaults is also a later explicit root; its first visit precedes Theme.
    asserts.assertEquals('3px', computed('base').borderTopWidth);
    asserts.assertEquals('rgb(153, 0, 153)', getComputedStyle(/** @type {!Element} */ (container.querySelector('[data-symbol=control]'))).backgroundColor);
    asserts.assertEquals('', computed('base').getPropertyValue('--jcss-react4j-css-example-BrowserExample_0024_UnusedGlobals--excluded'));
    style.textContent = await (await fetch('/css/examples/save-button/src/test/js/react4j/css/example/' + variant + 'alternate.css')).text();
    asserts.assertEquals('4px', computed('base').borderTopWidth);
    asserts.assertEquals('rgb(0, 153, 153)', getComputedStyle(/** @type {!Element} */ (container.querySelector('[data-symbol=control]'))).backgroundColor);
    asserts.assertEquals('', computed('base').getPropertyValue('--jcss-react4j-css-example-BrowserExample_0024_UnusedGlobals--excluded'));
    asserts.assertEquals('rgb(0, 136, 0)', computed('success').backgroundColor);
  },
  async testComponentImportsRetainGlobalsWithoutExplicitRoots() {
    style.textContent = await (await fetch('/css/examples/save-button/src/test/js/react4j/css/example/' + variant + 'component.css')).text();
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
    asserts.assertEquals(Mode.environment === 'production' ? 4 : 5, groups.size);
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
