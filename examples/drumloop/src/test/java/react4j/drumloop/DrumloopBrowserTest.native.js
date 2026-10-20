/** @return {string} */
DrumloopBrowserTest.observeScript = function() { return `
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
`; };
/** @param {!HTMLIFrameElement} frame @return {!Object} */
// The injected page is compiled separately; read its observation keys verbatim.
DrumloopBrowserTest.observe = function(frame) {
  const view = frame.contentWindow;
  return {
    get applicationErrors() { return view['applicationErrors']; },
    get audioStarts() { return view['audioStarts']; },
    get audioResumes() { return view['audioResumes']; },
    get audioContext() { return view['audioContext']; },
  };
};
/** @param {!HTMLIFrameElement} frame @return {number} */
DrumloopBrowserTest.now = function(frame) { return frame.contentWindow.performance.now(); };
