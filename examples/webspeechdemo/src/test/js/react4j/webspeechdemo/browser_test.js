goog.module('react4j.webspeechdemo.BrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');

const APPLICATION = '/examples/webspeechdemo/webspeechdemo/';
const OBSERVE = `
window.applicationErrors = [];
window.applicationLogs = [];
window.addEventListener('error', event => window.applicationErrors.push(event.message));
const nativeLog = console.log.bind(console);
console.log = (...args) => { window.applicationLogs.push(args.join(' ')); nativeLog(...args); };
`;

// Host speech services and voice inventories vary. Keep native utterances and
// events, and control only the external engine state for lifecycle assertions.
const SPEECH_ENGINE = `
const engine = new EventTarget();
let voices = [];
let current;
window.utterances = [];
window.cancelCalls = 0;
engine.speaking = false;
engine.paused = false;
engine.getVoices = () => voices;
const emit = (type, options = {}) => {
  current.dispatchEvent(new SpeechSynthesisEvent(type, {utterance: current, ...options}));
};
engine.speak = utterance => {
  if (!(utterance instanceof SpeechSynthesisUtterance)) throw new Error('Expected a native utterance');
  current = utterance;
  window.utterances.push(utterance);
  engine.speaking = true;
  emit('start');
};
engine.cancel = () => {
  window.cancelCalls++;
  const wasSpeaking = engine.speaking;
  engine.speaking = false;
  engine.paused = false;
  if (wasSpeaking) emit('end');
};
engine.pause = () => { engine.paused = true; emit('pause'); };
engine.resume = () => { engine.paused = false; emit('resume'); };
window.finishSpeech = type => { engine.speaking = false; emit(type); };
window.wordBoundary = (charIndex, charLength) => emit('boundary', {name: 'word', charIndex, charLength});
window.setVoiceInventory = count => {
  voices = Array.from({length: count}, (_, index) => Object.create(SpeechSynthesisVoice.prototype, {
    voiceURI: {value: 'test-' + index}, name: {value: 'Test voice ' + index},
    lang: {value: 'en-AU'}, default: {value: index === 0}
  }));
  engine.dispatchEvent(new Event('voiceschanged'));
};
Object.defineProperty(window, 'speechSynthesis', {value: engine});
`;

/**
 * @param {function():boolean} condition
 * @return {!Promise<void>}
 */
async function waitFor(condition) {
  const deadline = Date.now() + 10000;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error('Timed out waiting for Web Speech Demo');
    await new Promise(resolve => setTimeout(resolve, 20));
  }
}

/**
 * @param {string=} setup
 * @return {!Promise<!HTMLIFrameElement>}
 */
async function loadApplication(setup = '') {
  const frame = /** @type {!HTMLIFrameElement} */ (document.createElement('iframe'));
  frame.width = '800';
  frame.height = '700';
  document.body.appendChild(frame);
  let ready = false;
  try {
    const response = await fetch(APPLICATION + 'index.html');
    asserts.assertTrue(response.ok);
    const html = await response.text();
    const page = /** @type {!HTMLDocument} */ (frame.contentDocument);
    /** @type {!Promise<void>} */
    const loaded = new Promise(resolve => { frame.onload = () => { resolve(undefined); }; });
    page.open();
    page.write(html.replace('<head>', '<head><base href="' + new URL(APPLICATION, location.href).href +
      '"><script>' + OBSERVE + setup + '</script>'));
    page.close();
    await loaded;
    ready = true;
    return frame;
  } finally {
    if (!ready) frame.remove();
  }
}

/**
 * @param {!HTMLIFrameElement} frame
 * @return {void}
 */
function closeApplication(frame) {
  const view = /** @type {!Window} */ (frame.contentWindow);
  const page = /** @type {!Document} */ (frame.contentDocument);
  const dom = /** @type {!Object<string, *>} */ (view['ReactDOM']);
  const unmount = /** @type {function(!Element):boolean} */ (dom['unmountComponentAtNode']);
  if (view.speechSynthesis) view.speechSynthesis.cancel();
  unmount(/** @type {!Element} */ (page.getElementById('app')));
  frame.remove();
}

/**
 * @param {!Document} page
 * @param {string} title
 * @return {!HTMLButtonElement}
 */
function button(page, title) {
  return /** @type {!HTMLButtonElement} */ (page.querySelector('button[title="' + title + '"]'));
}

/**
 * @param {!Document} page
 * @param {string} selector
 * @param {string} value
 * @return {void}
 */
function setValue(page, selector, value) {
  const element = /** @type {!HTMLInputElement} */ (page.querySelector(selector));
  const prototype = element.tagName === 'TEXTAREA' ? HTMLTextAreaElement.prototype :
    element.tagName === 'SELECT' ? HTMLSelectElement.prototype : HTMLInputElement.prototype;
  Object.getOwnPropertyDescriptor(prototype, 'value').set.call(element, value);
  element.dispatchEvent(new Event(element.tagName === 'SELECT' ? 'change' : 'input', {bubbles: true}));
}

testSuite({
  setUpPage() {
    TestCase.getActiveTestCase().promiseTimeout = 20000;
  },

  async testNativeBrowserApiAndPackagedPage() {
    const frame = await loadApplication(`
const nativeSpeak = speechSynthesis.speak.bind(speechSynthesis);
speechSynthesis.speak = utterance => {
  window.nativeUtteranceValid = utterance instanceof SpeechSynthesisUtterance;
  utterance.volume = 0;
  nativeSpeak(utterance);
};
`);
    const view = /** @type {!Window} */ (frame.contentWindow);
    const page = /** @type {!Document} */ (frame.contentDocument);
    try {
      asserts.assertEquals('Web Speech Synthesis Demo', page.querySelector('h1').textContent);
      asserts.assertEquals('rgb(51, 51, 51)', view.getComputedStyle(page.body).backgroundColor);
      asserts.assertEquals('rgb(255, 255, 255)', view.getComputedStyle(page.getElementById('app').firstElementChild).color);
      asserts.assertEquals('480px', view.getComputedStyle(page.querySelector('form')).maxWidth);
      asserts.assertEquals('rgb(255, 255, 255)', view.getComputedStyle(page.getElementById('textarea')).backgroundColor);
      for (const control of page.querySelectorAll('button, select')) {
        const css = view.getComputedStyle(control);
        asserts.assertEquals('rgb(255, 255, 255)', css.backgroundColor);
        asserts.assertEquals('0px', css.borderTopWidth);
        asserts.assertEquals('2px', css.borderRadius);
      }
      asserts.assertEquals('24px', view.getComputedStyle(page.querySelector('select')).height);
      asserts.assertEquals('24px', view.getComputedStyle(button(page, 'Reset pitch')).height);
      asserts.assertEquals('52px', view.getComputedStyle(button(page, 'Speak')).height);
      const outside = page.createElement('button');
      page.body.appendChild(outside);
      asserts.assertNotEquals('Application control styles must remain scoped', '0px', view.getComputedStyle(outside).borderTopWidth);
      outside.remove();
      frame.width = '400';
      await waitFor(() => 384 === page.querySelector('form').getBoundingClientRect().width);
      frame.width = '800';
      await waitFor(() => 480 === page.querySelector('form').getBoundingClientRect().width);
      asserts.assertFalse(button(page, 'Speak').disabled);
      asserts.assertTrue(button(page, 'Pause').disabled);
      asserts.assertTrue(button(page, 'Stop').disabled);
      const pitch = /** @type {!HTMLInputElement} */ (page.querySelector('#pitch'));
      asserts.assertEquals('0.5', pitch.value);
      await waitFor(() => page.querySelectorAll('option').length === view.speechSynthesis.getVoices().length);
      setValue(page, 'textarea', 'React4j speech check.');
      button(page, 'Speak').click();
      asserts.assertTrue(/** @type {boolean} */ (view['nativeUtteranceValid']));
      const errors = /** @type {!Array<string>} */ (view['applicationErrors']);
      asserts.assertEquals(0, errors.length);
    } finally {
      closeApplication(frame);
    }
  },

  async testVoiceChangesAndSpeechLifecycle() {
    const frame = await loadApplication(SPEECH_ENGINE);
    const view = /** @type {!Window} */ (frame.contentWindow);
    const page = /** @type {!Document} */ (frame.contentDocument);
    const inventory = /** @type {function(number):void} */ (view['setVoiceInventory']);
    const finish = /** @type {function(string):void} */ (view['finishSpeech']);
    const boundary = /** @type {function(number, number):void} */ (view['wordBoundary']);
    const utterances = /** @type {!Array<!SpeechSynthesisUtterance>} */ (view['utterances']);
    try {
      asserts.assertEquals(0, page.querySelectorAll('option').length);
      inventory(2);
      await waitFor(() => page.querySelectorAll('option').length === 2);
      const select = /** @type {!HTMLSelectElement} */ (page.querySelector('select'));
      asserts.assertEquals('test-0', select.value);
      setValue(page, 'select', 'test-1');
      await waitFor(() => select.value === 'test-1');
      button(page, 'Reset voice').click();
      await waitFor(() => select.value === 'test-0');
      // Remove synthetic voices before assigning a voice to a native utterance.
      inventory(0);
      await waitFor(() => select.options.length === 0);

      setValue(page, 'textarea', 'Hello world');
      /** @type {!Array<!Array<string>>} */
      const settings = [['pitch', '0.8', '0.5'], ['rate', '-2', '0'], ['volume', '0.4', '1']];
      for (const setting of settings) {
        const id = setting[0];
        const value = setting[1];
        const defaultValue = setting[2];
        setValue(page, '#' + id, value);
        button(page, 'Reset ' + id).click();
        const input = /** @type {!HTMLInputElement} */ (page.querySelector('#' + id));
        await waitFor(() => Number(input.value) === Number(defaultValue));
        setValue(page, '#' + id, value);
      }
      button(page, 'Speak').click();
      await waitFor(() => button(page, 'Speak').disabled);
      asserts.assertEquals(1, utterances.length);
      asserts.assertEquals('Hello world', utterances[0].text);
      asserts.assertRoughlyEquals(0.8, utterances[0].pitch, 0.001);
      asserts.assertRoughlyEquals(1 / 3, utterances[0].rate, 0.001);
      asserts.assertRoughlyEquals(0.4, utterances[0].volume, 0.001);
      asserts.assertNull(utterances[0].voice);
      asserts.assertTrue(select.disabled);
      asserts.assertTrue((/** @type {!HTMLInputElement} */ (page.querySelector('#pitch'))).disabled);
      asserts.assertFalse(button(page, 'Pause').disabled);
      asserts.assertEquals('Hello world', page.querySelector('#textbeingspoken').textContent);
      await waitFor(() => view.getComputedStyle(page.getElementById('textarea')).backgroundColor === 'rgb(51, 51, 51)');
      boundary(6, 5);
      const marker = /** @type {!HTMLElement} */ (page.querySelector('#textarea > div:last-child'));
      await waitFor(() => view.getComputedStyle(marker).display === 'block');
      asserts.assertTrue(parseFloat(view.getComputedStyle(marker).width) > 0);
      asserts.assertTrue(parseFloat(view.getComputedStyle(marker).height) > 0);
      asserts.assertEquals(4, marker.style.length);
      for (let index = 0; index < marker.style.length; index++) {
        asserts.assertTrue(marker.style.item(index).startsWith('--'));
      }
      asserts.assertEquals('fixed', view.getComputedStyle(marker).position);
      asserts.assertEquals('rgb(255, 165, 0)', view.getComputedStyle(marker).borderTopColor);
      button(page, 'Pause').click();
      await waitFor(() => !!page.querySelector('button[title="Resume"]'));
      button(page, 'Resume').click();
      await waitFor(() => !!page.querySelector('button[title="Pause"]'));
      button(page, 'Stop').click();
      await waitFor(() => !button(page, 'Speak').disabled);
      asserts.assertTrue(button(page, 'Stop').disabled);
      asserts.assertFalse(select.disabled);
      asserts.assertEquals('Hello world', (/** @type {!HTMLTextAreaElement} */ (page.querySelector('textarea'))).value);

      button(page, 'Speak').click();
      await waitFor(() => button(page, 'Speak').disabled);
      finish('end');
      await waitFor(() => !button(page, 'Speak').disabled);
      button(page, 'Speak').click();
      await waitFor(() => button(page, 'Speak').disabled);
      button(page, 'Pause').click();
      await waitFor(() => !!page.querySelector('button[title="Resume"]'));
      const cancels = /** @type {number} */ (view['cancelCalls']);
      finish('error');
      await waitFor(() => !button(page, 'Speak').disabled);
      asserts.assertEquals(cancels + 1, /** @type {number} */ (view['cancelCalls']));
      asserts.assertFalse(view.speechSynthesis.paused);
      const errors = /** @type {!Array<string>} */ (view['applicationErrors']);
      asserts.assertEquals(0, errors.length);
    } finally {
      closeApplication(frame);
    }
  },

  async testUnsupportedSpeechApi() {
    const frame = await loadApplication('delete window.speechSynthesis;');
    const view = /** @type {!Window} */ (frame.contentWindow);
    const page = /** @type {!Document} */ (frame.contentDocument);
    try {
      asserts.assertEquals('', page.getElementById('app').textContent);
      const messages = /** @type {!Array<string>} */ (view['applicationLogs']);
      asserts.assertTrue(messages.includes('Speech API not present'));
      asserts.assertEquals(0, (/** @type {!Array<string>} */ (view['applicationErrors'])).length);
    } finally {
      closeApplication(frame);
    }
  },
});
