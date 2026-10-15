goog.module('react4j.drumloop.BrowserTest');
const CacheTest = goog.require('react4j.drumloop.ReactCacheTest');
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
      await waitFor(() => page.querySelectorAll('.step_button').length === 112 &&
        page.querySelectorAll('.button').length === 4);
      const errors = /** @type {!Array<string>} */ (view['applicationErrors']);
      const audioStarts = /** @type {!Array<!Object<string, *>>} */ (view['audioStarts']);
      const audioContext = /** @type {!AudioContext} */ (view['audioContext']);
      asserts.assertEquals('Trap Lord 9000', page.querySelector('.logo').textContent);
      asserts.assertEquals(7, page.querySelectorAll('.track').length);
      asserts.assertEquals(0, errors.length);
      await page.fonts.ready;
      asserts.assertTrue(page.fonts.check('28px Righteous'));
      const sounds = view.performance.getEntriesByType('resource').filter(entry => entry.name.endsWith('.wav'));
      asserts.assertEquals(11, sounds.length);
      asserts.assertTrue(sounds.every(entry => entry.name.startsWith(base + 'sounds/')));

      const step = page.querySelector('.step_button');
      step.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => step.classList.contains('step_button_on'));
      step.dispatchEvent(new MouseEvent('click', {bubbles: true, shiftKey: true}));
      await waitFor(() => step.classList.contains('step_button_doubled'));
      step.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => !step.classList.contains('step_button_on'));
      step.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => step.classList.contains('step_button_on'));

      const bpm = /** @type {!HTMLInputElement} */ (page.querySelector('.bpmInput'));
      Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set.call(bpm, '120');
      bpm.dispatchEvent(new Event('input', {bubbles: true}));
      await waitFor(() => bpm.value === '120');
      await audioContext.suspend();
      const play = page.querySelector('.startButton');
      const started = view.performance.now();
      play.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => play.textContent === 'Stop' && audioStarts.length > 0);
      asserts.assertTrue('Play resumes suspended audio', /** @type {number} */ (view['audioResumes']) > 0);
      const indicator = /** @type {!HTMLElement} */ (page.querySelector('.indicator'));
      asserts.assertEquals('0px', indicator.style.left);
      await new Promise(resolve => setTimeout(resolve, 150));
      asserts.assertEquals('The beat must not advance at a zero-millisecond interval', '0px', indicator.style.left);
      await waitFor(() => indicator.style.left !== '0px');
      const elapsed = view.performance.now() - started;
      asserts.assertTrue('120 BPM advances after about 500 ms: ' + elapsed, elapsed >= 400 && elapsed < 850);
      // One beat at 120 BPM is 500 ms (the original 16-step timing convention).
      asserts.assertEquals('37.5px', indicator.style.left);
      play.dispatchEvent(new MouseEvent('click', {bubbles: true}));
      await waitFor(() => play.textContent === 'Play' && !page.querySelector('.indicator'));
      const starts = audioStarts.length;
      await new Promise(resolve => setTimeout(resolve, 600));
      asserts.assertEquals(starts, audioStarts.length);

      const fx = page.querySelector('.button');
      fx.dispatchEvent(new MouseEvent('mousedown', {bubbles: true, shiftKey: true}));
      await waitFor(() => fx.classList.contains('button_held'));
      asserts.assertTrue(audioStarts[audioStarts.length - 1]['loop']);
      fx.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));
      asserts.assertTrue(fx.classList.contains('button_held'));
      fx.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));
      await waitFor(() => !fx.classList.contains('button_held'));
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
