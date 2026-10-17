goog.module('react4j.hrm.BrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const asserts = goog.require('goog.testing.asserts');
const testSuite = goog.require('goog.testing.testSuite');

const APPLICATION = '/examples/heart-rate-monitor/heart-rate-monitor/';
const OBSERVE = `
window.applicationErrors = [];
window.applicationLogs = [];
window.addEventListener('error', event => window.applicationErrors.push(event.message));
window.addEventListener('unhandledrejection', event => window.applicationErrors.push(String(event.reason)));
console.log = (...args) => window.applicationLogs.push(args.join(' '));
`;

// Control the external Bluetooth device, retaining native events, promises and DataView.
const BLUETOOTH = `
// Linux Chromium does not expose the Web Bluetooth constructors by default.
window.BluetoothDevice = class extends EventTarget {};
window.BluetoothRemoteGATTCharacteristic = class extends EventTarget {};
window.BluetoothRemoteGATTServer = class {};
window.BluetoothRemoteGATTService = class {};
const device = new EventTarget();
Object.setPrototypeOf(device, BluetoothDevice.prototype);
const characteristic = new EventTarget();
Object.setPrototypeOf(characteristic, BluetoothRemoteGATTCharacteristic.prototype);
Object.defineProperty(characteristic, 'value', {value: null, writable: true});
let rejectPairing = false;
window.requests = [];
window.notificationStarts = 0;
const server = {
  connected: false,
  connect() { this.connected = true; return Promise.resolve(this); },
  disconnect() { this.connected = false; device.dispatchEvent(new Event('gattserverdisconnected')); },
  getPrimaryService(name) {
    if (name !== 'heart_rate') throw new Error('Wrong service: ' + name);
    const service = Object.create(BluetoothRemoteGATTService.prototype);
    service.getCharacteristic = name => {
      if (name !== 'heart_rate_measurement') throw new Error('Wrong characteristic: ' + name);
      return Promise.resolve(characteristic);
    };
    return Promise.resolve(service);
  }
};
Object.setPrototypeOf(server, BluetoothRemoteGATTServer.prototype);
Object.defineProperty(device, 'gatt', {value: server});
characteristic.startNotifications = () => {
  window.notificationStarts++;
  return Promise.resolve(characteristic);
};
window.measure = rate => {
  const bytes = new Uint8Array([0, rate]);
  characteristic.value = new DataView(bytes.buffer);
  characteristic.dispatchEvent(new Event('characteristicvaluechanged'));
};
window.disconnectSensor = () => server.disconnect();
window.cancelPairing = () => { rejectPairing = true; };
Object.defineProperty(navigator, 'bluetooth', {value: {
  requestDevice(options) {
    window.requests.push(options);
    if (rejectPairing) { rejectPairing = false; return Promise.reject(new DOMException('Cancelled', 'NotFoundError')); }
    return Promise.resolve(device);
  }
}});
`;

/**
 * @param {function():boolean} condition
 * @return {!Promise<void>}
 */
async function waitFor(condition) {
  const deadline = Date.now() + 10000;
  while (!condition()) {
    if (Date.now() > deadline) throw new Error('Timed out waiting for Heart Rate Monitor');
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
 * @param {!Document} page
 * @param {number} index
 * @return {!HTMLButtonElement}
 */
function button(page, index) {
  return /** @type {!HTMLButtonElement} */ (page.querySelectorAll('button')[index]);
}

testSuite({
  setUpPage() {
    TestCase.getActiveTestCase().promiseTimeout = 20000;
  },

  async testPackagedDashboardAndBluetoothLifecycle() {
    const frame = await loadApplication(BLUETOOTH);
    const view = /** @type {!Window} */ (frame.contentWindow);
    const page = /** @type {!Document} */ (frame.contentDocument);
    const measure = /** @type {function(number):void} */ (view['measure']);
    const disconnect = /** @type {function():void} */ (view['disconnectSensor']);
    try {
      asserts.assertEquals('Heart Rate Monitor', page.querySelector('h1').textContent);
      const heart = /** @type {!HTMLImageElement} */ (page.querySelector('img'));
      await waitFor(() => heart.complete);
      asserts.assertTrue(heart.naturalWidth > 0);
      asserts.assertEquals('160px', view.getComputedStyle(heart).width);
      asserts.assertEquals('flex', view.getComputedStyle(heart.parentElement).display);
      asserts.assertEquals('column', view.getComputedStyle(heart.parentElement).flexDirection);
      asserts.assertFalse(button(page, 0).disabled);
      asserts.assertTrue(button(page, 1).disabled);
      asserts.assertFalse(heart.classList.contains('beating'));
      button(page, 0).click();
      await waitFor(() => Number(view['notificationStarts']) === 1);
      const requests = /** @type {!Array<!Object<string, *>>} */ (view['requests']);
      asserts.assertEquals(1, requests.length);
      asserts.assertFalse(requests[0]['acceptAllDevices']);
      asserts.assertEquals('[{"services":["heart_rate"]}]', JSON.stringify(requests[0]['filters']));
      asserts.assertTrue(button(page, 0).disabled);
      asserts.assertFalse(button(page, 1).disabled);
      measure(72);
      await waitFor(() => heart.classList.contains('beating'));
      asserts.assertContains('72', page.getElementById('app').textContent);
      asserts.assertTrue(heart.classList.contains('beating'));
      asserts.assertEquals('beating', view.getComputedStyle(heart).animationName);
      button(page, 1).click();
      await waitFor(() => !button(page, 0).disabled);
      asserts.assertFalse(button(page, 0).disabled);
      asserts.assertTrue(button(page, 1).disabled);
      asserts.assertFalse(heart.classList.contains('beating'));
      asserts.assertNotContains('72', page.getElementById('app').textContent);
      button(page, 0).click();
      await waitFor(() => Number(view['notificationStarts']) === 2);
      measure(81);
      await waitFor(() => page.getElementById('app').textContent.includes('81'));
      asserts.assertContains('81', page.getElementById('app').textContent);
      disconnect();
      await waitFor(() => !button(page, 0).disabled);
      asserts.assertFalse(button(page, 0).disabled);
      asserts.assertTrue(button(page, 1).disabled);
      asserts.assertNotContains('81', page.getElementById('app').textContent);
      asserts.assertEquals(0, /** @type {!Array<string>} */ (view['applicationErrors']).length);
    } finally {
      frame.remove();
    }
  },

  async testCancelledPairingCanRetry() {
    const frame = await loadApplication(BLUETOOTH);
    const view = /** @type {!Window} */ (frame.contentWindow);
    const page = /** @type {!Document} */ (frame.contentDocument);
    const cancel = /** @type {function():void} */ (view['cancelPairing']);
    try {
      cancel();
      button(page, 0).click();
      await new Promise(resolve => setTimeout(resolve, 50));
      asserts.assertEquals(1, /** @type {!Array<*>} */ (view['requests']).length);
      asserts.assertFalse(button(page, 0).disabled);
      asserts.assertTrue(button(page, 1).disabled);
      button(page, 0).click();
      await waitFor(() => Number(view['notificationStarts']) === 1);
      asserts.assertTrue(button(page, 0).disabled);
      asserts.assertFalse(button(page, 1).disabled);
      asserts.assertEquals(0, /** @type {!Array<string>} */ (view['applicationErrors']).length);
    } finally {
      frame.remove();
    }
  },

  async testUnavailableBluetoothPreservesUnsupportedBehavior() {
    const frame = await loadApplication("Object.defineProperty(navigator, 'bluetooth', {value: undefined});");
    const view = /** @type {!Window} */ (frame.contentWindow);
    const page = /** @type {!Document} */ (frame.contentDocument);
    try {
      asserts.assertEquals('', page.getElementById('app').textContent);
      asserts.assertContains('Bluetooth API not present', /** @type {!Array<string>} */ (view['applicationLogs']));
      asserts.assertEquals(0, /** @type {!Array<string>} */ (view['applicationErrors']).length);
    } finally {
      frame.remove();
    }
  }
});
