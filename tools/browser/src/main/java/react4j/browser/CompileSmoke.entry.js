goog.module('react4j.browser.CompileSmokeEntry');
const Smoke = goog.require('react4j.browser.CompileSmoke');
window['react4jCompileSmoke'] = Smoke.snapshot();
window['react4jCompileElement'] = Smoke.element();
