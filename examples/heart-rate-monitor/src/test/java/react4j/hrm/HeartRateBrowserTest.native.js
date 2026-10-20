/** @return {string} */
HeartRateBrowserTest.observeScript = function() { return `
window.applicationErrors = [];
window.applicationLogs = [];
window.addEventListener('error', event => window.applicationErrors.push(event.message));
window.addEventListener('unhandledrejection', event => window.applicationErrors.push(String(event.reason)));
console.log = (...args) => window.applicationLogs.push(args.join(' '));
`; };
/** @return {string} */
HeartRateBrowserTest.bluetoothScript = function() { return `
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
window.services = [];
window.characteristics = [];
window.notificationStarts = 0;
const server = {
  connected: false,
  connect() { this.connected = true; return Promise.resolve(this); },
  disconnect() { this.connected = false; device.dispatchEvent(new Event('gattserverdisconnected')); },
  getPrimaryService(name) {
    window.services.push(name);
    const service = Object.create(BluetoothRemoteGATTService.prototype);
    service.getCharacteristic = name => {
      window.characteristics.push(name);
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
`; };
/** @param {!HTMLIFrameElement} frame @return {!Object} */
// The injected page is compiled separately; read its observation keys verbatim.
HeartRateBrowserTest.observe = function(frame) {
  const view = frame.contentWindow;
  return {
    get applicationErrors() { return view['applicationErrors']; },
    get applicationLogs() { return view['applicationLogs']; },
    get requests() { return view['requests'].map(request => ({acceptAllDevices: request['acceptAllDevices'], filters: request['filters']})); },
    get notificationStarts() { return view['notificationStarts']; },
    get services() { return view['services']; },
    get characteristics() { return view['characteristics']; },
  };
};
/** @param {!HTMLIFrameElement} frame @param {number} rate */
HeartRateBrowserTest.measure = function(frame, rate) { frame.contentWindow['measure'](rate); };
/** @param {!HTMLIFrameElement} frame */
HeartRateBrowserTest.disconnect = function(frame) { frame.contentWindow['disconnectSensor'](); };
/** @param {!HTMLIFrameElement} frame */
HeartRateBrowserTest.cancel = function(frame) { frame.contentWindow['cancelPairing'](); };
