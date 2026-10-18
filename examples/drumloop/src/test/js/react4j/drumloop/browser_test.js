goog.module('react4j.drumloop.BrowserTest');
const CacheTest = goog.require('react4j.drumloop.cache.ReactCacheTest');
const TestCase = goog.require('goog.testing.TestCase');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');

const APPLICATION = '/examples/drumloop/drumloop/';

/** @param {function():boolean} condition @return {!Promise<void>} */
async function waitFor(condition) {
  const deadline = Date.now() + 15000;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error('Timed out waiting for Drumloop');
    await new Promise(resolve => setTimeout(resolve, 20));
  }
}

// Observe real Web Audio calls without replacing decoding, playback, or promises.
const OBSERVE = `<script>
window.audioStarts = [];
window.audioResumes = 0;
window.applicationErrors = [];
window.addEventListener('error', event => window.applicationErrors.push(event.message));
const NativeAudioContext = window.AudioContext;
window.AudioContext = class extends NativeAudioContext {
  constructor() { super(); window.audioContext = this; }
  resume() { window.audioResumes++; return super.resume(); }
};
const nativeStart = AudioBufferSourceNode.prototype.start;
AudioBufferSourceNode.prototype.start = function(...args) {
  window.audioStarts.push({time: performance.now(), loop: this.loop, duration: this.buffer.duration});
  return nativeStart.apply(this, args);
};
</script>`;

testSuite({
  setUpPage() {
    TestCase.getActiveTestCase().promiseTimeout = 20000;
  },
  testCacheContracts() {
    return CacheTest.run();
  },

  async testPackagedApplication() {
    const frame = /** @type {!HTMLIFrameElement} */ (document.createElement('iframe'));
    frame.width = '1000';
    frame.height = '700';
    document.body.appendChild(frame);
    const page = /** @type {!HTMLDocument} */ (frame.contentDocument);
    const view = /** @type {!Window} */ (frame.contentWindow);
    try {
      const response = await fetch(APPLICATION + 'index.html');
      asserts.assertTrue(response.ok);
      const html = await response.text();
      const base = new URL(APPLICATION, location.href).href;
      page.open();
      page.write(html.replace('<head>', '<head><base href="' + base + '">' + OBSERVE));
      page.close();
      await waitFor(() => page.querySelectorAll('button[data-step]').length === 112 &&
        page.querySelectorAll('button[data-sound]').length === 4);
      const map = /** @type {!Object<string, *>} */ (await (await fetch(APPLICATION + 'css/index.css.map')).json());
      asserts.assertEquals(3, map['version']);
      const sections = /** @type {!Array<!Object<string, *>>} */ (map['sections']);
      /** @type {!Set<string>} */
      const javaSources = new Set();
      sections.forEach(section => {
        const component = /** @type {!Object<string, *>} */ (section['map']);
        const sources = /** @type {!Array<string>} */ (component['sources']);
        sources.filter(name => name.endsWith('.java')).forEach(name => javaSources.add(name));
      });
      /** @type {!Array<string>} */
      const owners = [
        'BpmInput', 'DrumMachineView', 'FxButton', 'IndicatorView', 'PlayButton', 'StepButton', 'TrackView'
      ];
      asserts.assertArrayEquals(owners.map(name => 'react4j/drumloop/views/' + name + '.java').sort(), Array.from(javaSources).sort());
      asserts.assertEquals('flex', view.getComputedStyle(page.querySelector('h1').parentElement).display);
      asserts.assertEquals('rgb(51, 51, 51)', view.getComputedStyle(page.body).backgroundColor);
      const errors = /** @type {!Array<string>} */ (view['applicationErrors']);
      const audioStarts = /** @type {!Array<!Object<string, *>>} */ (view['audioStarts']);
      const audioContext = /** @type {!AudioContext} */ (view['audioContext']);
      asserts.assertEquals('Trap Lord 9000', page.querySelector('h1').textContent);
      asserts.assertEquals(7, page.querySelectorAll('h2').length);
      const container = page.getElementById('app').firstElementChild;
      asserts.assertEquals(800, container.getBoundingClientRect().width);
      asserts.assertEquals(155, page.querySelector('h2').parentElement.getBoundingClientRect().width);
      const effects = page.querySelector('button[data-sound]').parentElement;
      asserts.assertEquals(796, effects.getBoundingClientRect().width);
      const outside = page.createElement('div');
      outside.style.cssText = 'width: 10px; padding: 2px; border: 1px solid;';
      page.body.appendChild(outside);
      asserts.assertEquals('Component sizing must not reset unrelated elements', 16, outside.getBoundingClientRect().width);
      outside.remove();
      frame.width = '400';
      await waitFor(() => 400 === container.getBoundingClientRect().width);
      asserts.assertEquals(396, effects.getBoundingClientRect().width);
      asserts.assertEquals(155, page.querySelector('h2').parentElement.getBoundingClientRect().width);
      frame.width = '1000';
      await waitFor(() => 800 === container.getBoundingClientRect().width);
      asserts.assertEquals(0, errors.length);
      await page.fonts.ready;
      asserts.assertTrue(page.fonts.check('28px Righteous'));
      const sounds = view.performance.getEntriesByType('resource').filter(entry => entry.name.endsWith('.wav'));
      asserts.assertEquals(11, sounds.length);
      asserts.assertTrue(sounds.every(entry => entry.name.startsWith(base + 'sounds/')));

      const step = page.querySelector('button[data-step]');
      step.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => view.getComputedStyle(step).opacity === '1');
      step.dispatchEvent(new MouseEvent('click', {bubbles: true, shiftKey: true}));
      await waitFor(() => step.getAnimations().length === 1);
      asserts.assertEquals('0.5s', view.getComputedStyle(step).animationDuration);
      asserts.assertEquals('linear', view.getComputedStyle(step).animationTimingFunction);
      asserts.assertEquals('infinite', view.getComputedStyle(step).animationIterationCount);
      step.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => view.getComputedStyle(step).opacity === '0.35');
      asserts.assertEquals(0, step.getAnimations().length);
      step.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => view.getComputedStyle(step).opacity === '1');

      const bpm = /** @type {!HTMLInputElement} */ (page.querySelector('input[type=number]'));
      Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set.call(bpm, '120');
      bpm.dispatchEvent(new Event('input', {bubbles: true}));
      await waitFor(() => bpm.value === '120');
      await audioContext.suspend();
      const play = page.querySelector('input[type=number] + button');
      const started = view.performance.now();
      play.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => play.textContent === 'Stop' && audioStarts.length > 0);
      asserts.assertTrue('Play resumes suspended audio', /** @type {number} */ (view['audioResumes']) > 0);
      const indicator = /** @type {!HTMLElement} */ (page.querySelector('[style*="--jcss-react4j-drumloop-views-IndicatorView_0024_IndicatorStyle--indicator_002d_left"]'));
      asserts.assertEquals('0px', view.getComputedStyle(indicator).left);
      asserts.assertEquals('0px', indicator.style.getPropertyValue('--jcss-react4j-drumloop-views-IndicatorView_0024_IndicatorStyle--indicator_002d_left'));
      asserts.assertEquals(600, indicator.parentElement.getBoundingClientRect().width);
      asserts.assertEquals(38, indicator.getBoundingClientRect().width);
      await new Promise(resolve => setTimeout(resolve, 150));
      asserts.assertEquals('The beat must not advance at a zero-millisecond interval', '0px', view.getComputedStyle(indicator).left);
      await waitFor(() => view.getComputedStyle(indicator).left !== '0px');
      const elapsed = view.performance.now() - started;
      asserts.assertTrue('120 BPM advances after about 500 ms: ' + elapsed, elapsed >= 400 && elapsed < 850);
      // One beat at 120 BPM is 500 ms (the original 16-step timing convention).
      asserts.assertEquals('37.5px', view.getComputedStyle(indicator).left);
      play.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => play.textContent === 'Play' && !page.querySelector('[style*="--jcss-react4j-drumloop-views-IndicatorView_0024_IndicatorStyle--indicator_002d_left"]'));
      const starts = audioStarts.length;
      await new Promise(resolve => setTimeout(resolve, 600));
      asserts.assertEquals(starts, audioStarts.length);

      const fx = page.querySelector('button[data-sound]');
      fx.dispatchEvent(new MouseEvent('mousedown', {bubbles: true, shiftKey: true}));
      await waitFor(() => view.getComputedStyle(fx).backgroundColor === 'rgb(253, 114, 114)');
      asserts.assertTrue(audioStarts[audioStarts.length - 1]['loop']);
      fx.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));
      asserts.assertTrue(view.getComputedStyle(fx).backgroundColor === 'rgb(253, 114, 114)');
      fx.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));
      await waitFor(() => view.getComputedStyle(fx).backgroundColor === 'rgba(0, 0, 0, 0)');
      const before = audioStarts.length;
      fx.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));
      await waitFor(() => audioStarts.length === before + 1);
      asserts.assertFalse(audioStarts[before]['loop']);
      fx.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));
      asserts.assertEquals(0, errors.length);
    } finally {
      const context = /** @type {?AudioContext} */ (view['audioContext']);
      if (context) await context.close();
      frame.remove();
    }
  },
});
