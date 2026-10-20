/** @return {string} */
SpeechBrowserTest.observeScript = function() { return `
window.applicationErrors = [];
window.applicationLogs = [];
window.addEventListener('error', event => window.applicationErrors.push(event.message));
const nativeLog = console.log.bind(console);
console.log = (...args) => { window.applicationLogs.push(args.join(' ')); nativeLog(...args); };
`; };
/** @return {string} */
SpeechBrowserTest.speechEngineScript = function() { return `
const engine = new EventTarget();
let voices = [];
let current;
window.utterances = [];
window.utteranceTypes = [];
window.cancelCalls = 0;
engine.speaking = false;
engine.paused = false;
engine.getVoices = () => voices;
const emit = (type, options = {}) => {
  current.dispatchEvent(new SpeechSynthesisEvent(type, {utterance: current, ...options}));
};
engine.speak = utterance => {
  window.utteranceTypes.push(utterance instanceof SpeechSynthesisUtterance);
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
`; };
/** @return {string} */
SpeechBrowserTest.nativeSpeechScript = function() { return `
const nativeSpeak = speechSynthesis.speak.bind(speechSynthesis);
speechSynthesis.speak = utterance => {
  window.nativeUtteranceValid = utterance instanceof SpeechSynthesisUtterance;
  utterance.volume = 0;
  nativeSpeak(utterance);
};
`; };
/** @param {!HTMLIFrameElement} frame @return {!Object} */
// The injected page is compiled separately; read its observation keys verbatim.
SpeechBrowserTest.observe = function(frame) {
  const view = frame.contentWindow;
  return {
    get applicationErrors() { return view['applicationErrors']; },
    get applicationLogs() { return view['applicationLogs']; },
    get nativeUtteranceValid() { return view['nativeUtteranceValid']; },
    get utterances() { return view['utterances']; },
    get utteranceTypes() { return view['utteranceTypes']; },
    get cancelCalls() { return view['cancelCalls']; },
    get speechSynthesis() { return view['speechSynthesis']; },
  };
};
/** @param {!HTMLIFrameElement} frame @param {number} count */
SpeechBrowserTest.inventory = function(frame, count) { frame.contentWindow['setVoiceInventory'](count); };
/** @param {!HTMLIFrameElement} frame @param {string} type */
SpeechBrowserTest.finish = function(frame, type) { frame.contentWindow['finishSpeech'](type); };
/** @param {!HTMLIFrameElement} frame @param {number} index @param {number} length */
SpeechBrowserTest.boundary = function(frame, index, length) { frame.contentWindow['wordBoundary'](index, length); };
/** @param {!HTMLIFrameElement} frame @return {number} */
SpeechBrowserTest.voiceCount = function(frame) { return frame.contentWindow.speechSynthesis.getVoices().length; };
/** @param {!HTMLIFrameElement} frame @return {boolean} */
SpeechBrowserTest.paused = function(frame) { return frame.contentWindow.speechSynthesis.paused; };
/** @param {!HTMLIFrameElement} frame */
SpeechBrowserTest.unmount = function(frame) {
  const view = frame.contentWindow;
  if (view['ReactDOM']) view['ReactDOM']['unmountComponentAtNode'](frame.contentDocument.getElementById('app'));
};
