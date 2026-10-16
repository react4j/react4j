/**
 * @fileoverview Web Bluetooth declarations used by the heart rate monitor.
 * @externs
 */

/** @constructor */
function Bluetooth() {}

/** @type {Bluetooth|undefined} */
Navigator.prototype.bluetooth;

/** @record */
function BluetoothLEScanFilterInit() {}

/** @type {!Array<string>} */
BluetoothLEScanFilterInit.prototype.services;

/** @record */
function RequestDeviceOptions() {}

/** @type {boolean} */
RequestDeviceOptions.prototype.acceptAllDevices;

/** @type {!Array<!BluetoothLEScanFilterInit>} */
RequestDeviceOptions.prototype.filters;

/**
 * @param {!Object} options
 * @return {!Promise<!BluetoothDevice>}
 */
Bluetooth.prototype.requestDevice = function(options) {};

/** @constructor @implements {EventTarget} */
function BluetoothDevice() {}

/** @type {?BluetoothRemoteGATTServer} */
BluetoothDevice.prototype.gatt;

/** @constructor */
function BluetoothRemoteGATTServer() {}

/** @type {boolean} */
BluetoothRemoteGATTServer.prototype.connected;

/** @return {!Promise<!BluetoothRemoteGATTServer>} */
BluetoothRemoteGATTServer.prototype.connect = function() {};

/** @return {void} */
BluetoothRemoteGATTServer.prototype.disconnect = function() {};

/**
 * @param {string} service
 * @return {!Promise<!BluetoothRemoteGATTService>}
 */
BluetoothRemoteGATTServer.prototype.getPrimaryService = function(service) {};

/** @constructor */
function BluetoothRemoteGATTService() {}

/**
 * @param {string} characteristic
 * @return {!Promise<!BluetoothRemoteGATTCharacteristic>}
 */
BluetoothRemoteGATTService.prototype.getCharacteristic = function(characteristic) {};

/** @constructor @implements {EventTarget} */
function BluetoothRemoteGATTCharacteristic() {}

/** @type {?DataView} */
BluetoothRemoteGATTCharacteristic.prototype.value;

/** @return {!Promise<!BluetoothRemoteGATTCharacteristic>} */
BluetoothRemoteGATTCharacteristic.prototype.startNotifications = function() {};
