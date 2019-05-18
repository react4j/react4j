/**
 * @fileoverview Closure Compiler externs for Facebook React.js
 * @see http://reactjs.org
 * @externs
 */

/**
 * @const {!Object}
 * @suppress {const|duplicate}
 */
var React = {};

/**
 * @constructor
 * @param {Object} props Initial props of the component instance.
 */
React.Component = function(props) {};

/**
 * @type {Object}
 * @nosideeffects
 */
React.Component.prototype.props;

/**
 * @type {Object}
 * @nosideeffects
 */
React.Component.prototype.state;

/**
 * @param {function(): void=} callback
 * @return {void}
 */
React.Component.prototype.forceUpdate = function(callback) {};

/**
 * @param {Object} nextState
 * @param {(function(): void)=} callback
 * @return {void}
 */
React.Component.prototype.setState = function(nextState, callback) {};

/**
 * @protected
 * @return {void}
 */
React.Component.prototype.componentDidMount = function() {};

/**
 * @param {Object} nextProps
 * @param {Object} nextState
 * @return {boolean}
 * @protected
 */
React.Component.prototype.shouldComponentUpdate = function(nextProps, nextState) {};

/**
 * @param {Object} prevProps
 * @param {Object} prevState
 * @return {void}
 * @protected
 */
React.Component.prototype.componentDidUpdate = function(prevProps, prevState) {};

/**
 * @protected
 * @return {void}
 */
React.Component.prototype.componentWillUnmount = function() {};

/**
 * @protected
 * @return {void}
 */
React.Component.prototype.componentDidCatch = function() {};

/**
 * @protected
 */
React.Component.prototype.getSnapshotBeforeUpdate = function() {};

/**
 * @return {React.Component}
 * @protected
 */
React.Component.prototype.render = function() {};

/**
 * @type {!Object}
 * @const
 * @suppress {const|duplicate}
 */
React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED = {};

/**
 * @type {!Object}
 * @const
 * @suppress {const|duplicate}
 */
React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner = {};

/**
 * @type {!Object}
 * @suppress {duplicate}
 */
React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner.current;

/**
 * Interface to preserve React attributes for advanced compilation.
 * @interface
 */
React.ReactAttribute = function() {};

/**
 * @type {Object}
 */
React.ReactAttribute.dangerouslySetInnerHTML;

/**
 * @type {string}
 */
React.ReactAttribute.__html;

/**
 * @type {string}
 */
React.ReactAttribute.key;

/**
 * @type {symbol}
 */
React.ReactAttribute.$$typeof;

/**
 * @type {Object}
 */
React.ReactAttribute._owner;

/**
 * @type {string}
 */
React.ReactAttribute.ref;

// Attributes not defined in default Closure Compiler DOM externs.
// http://facebook.github.io/react/docs/tags-and-attributes.html#html-attributes
// It happens because React favors camelCasing over allinlowercase.
// How to update list:
//   1) Open http://facebook.github.io/react/docs/tags-and-attributes.html#html-attributes
//   2) Github Search in google/closure-compiler for attribute.

/**
 * @type {Object}
 */
React.ReactAttribute.styleMedia;

/**
 * @type {boolean}
 */
React.ReactAttribute.allowFullScreen;

/**
 * @type {boolean}
 */
React.ReactAttribute.autoComplete;

/**
 * @type {boolean}
 */
React.ReactAttribute.autoFocus;

/**
 * @type {boolean}
 */
React.ReactAttribute.autoPlay;

/**
 * @type {boolean}
 */
React.ReactAttribute.noValidate;

/**
 * @type {boolean}
 */
React.ReactAttribute.spellCheck;

// http://facebook.github.io/react/docs/events.html

/**
 * @type {Function}
 */
React.ReactAttribute.onCopy;

/**
 * @type {Function}
 */
React.ReactAttribute.onCut;

/**
 * @type {Function}
 */
React.ReactAttribute.onPaste;

/**
 * @type {Function}
 */
React.ReactAttribute.onCompositionEnd;

/**
 * @type {Function}
 */
React.ReactAttribute.onCompositionStart;

/**
 * @type {Function}
 */
React.ReactAttribute.onCompositionUpdate;

/**
 * @type {Function}
 */
React.ReactAttribute.onKeyDown;

/**
 * @type {Function}
 */
React.ReactAttribute.onKeyPress;

/**
 * @type {Function}
 */
React.ReactAttribute.onKeyUp;

/**
 * @type {Function}
 */
React.ReactAttribute.onFocus;

/**
 * @type {Function}
 */
React.ReactAttribute.onBlur;

/**
 * @type {Function}
 */
React.ReactAttribute.onChange;

/**
 * @type {Function}
 */
React.ReactAttribute.onInput;

/**
 * @type {Function}
 */
React.ReactAttribute.onInvalid;

/**
 * @type {Function}
 */
React.ReactAttribute.onSubmit;

/**
 * @type {Function}
 */
React.ReactAttribute.onClick;

/**
 * @type {Function}
 */
React.ReactAttribute.onContextMenu;

/**
 * @type {Function}
 */
React.ReactAttribute.onDoubleClick;

/**
 * @type {Function}
 */
React.ReactAttribute.onDrag;

/**
 * @type {Function}
 */
React.ReactAttribute.onDragEnd;

/**
 * @type {Function}
 */
React.ReactAttribute.onDragEnter;

/**
 * @type {Function}
 */
React.ReactAttribute.onDragExit;

/**
 * @type {Function}
 */
React.ReactAttribute.onDragLeave;

/**
 * @type {Function}
 */
React.ReactAttribute.onDragOver;

/**
 * @type {Function}
 */
React.ReactAttribute.onDragStart;

/**
 * @type {Function}
 */
React.ReactAttribute.onDrop;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseDown;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseEnter;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseLeave;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseMove;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseOut;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseOver;

/**
 * @type {Function}
 */
React.ReactAttribute.onMouseUp;

/**
 * @type {Function}
 */
React.ReactAttribute.onSelect;

/**
 * @type {Function}
 */
React.ReactAttribute.onTouchCancel;

/**
 * @type {Function}
 */
React.ReactAttribute.onTouchEnd;

/**
 * @type {Function}
 */
React.ReactAttribute.onTouchMove;

/**
 * @type {Function}
 */
React.ReactAttribute.onTouchStart;

/**
 * @type {Function}
 */
React.ReactAttribute.onScroll;

/**
 * @type {Function}
 */
React.ReactAttribute.onWheel;

/**
 * @type {Function}
 */
React.ReactAttribute.onAbort;
React.ReactAttribute.onCanPlay;
React.ReactAttribute.onCanPlayThrough;
React.ReactAttribute.onDurationChange;
React.ReactAttribute.onEmptied;
React.ReactAttribute.onEncrypted;
React.ReactAttribute.onEnded;
React.ReactAttribute.onError;
React.ReactAttribute.onLoadedData;
React.ReactAttribute.onLoadedMetadata;
React.ReactAttribute.onLoadStart;
React.ReactAttribute.onPause;
React.ReactAttribute.onPlay;
React.ReactAttribute.onPlaying;
React.ReactAttribute.onProgress;
React.ReactAttribute.onRateChange;
React.ReactAttribute.onSeeked;
React.ReactAttribute.onSeeking;
React.ReactAttribute.onStalled;
React.ReactAttribute.onSuspend;
React.ReactAttribute.onTimeUpdate;
React.ReactAttribute.onVolumeChange;
React.ReactAttribute.onWaiting;

React.ReactAttribute.onAnimationStart;
React.ReactAttribute.onAnimationEnd;
React.ReactAttribute.onAnimationIteration;

React.ReactAttribute.onTransitionEnd;

React.ReactAttribute.onToggle;

/** @record @struct */
React.SyntheticEvent = function() {};
 /** @type {boolean} */
React.SyntheticEvent.prototype.bubbles;
 /** @type {?} */
React.SyntheticEvent.prototype.currentTarget;
 /** @type {boolean} */
React.SyntheticEvent.prototype.cancelable;
 /** @type {boolean} */
React.SyntheticEvent.prototype.defaultPrevented;
 /** @type {number} */
React.SyntheticEvent.prototype.eventPhase;
 /** @type {boolean} */
React.SyntheticEvent.prototype.isTrusted;
 /** @type {!Event} */
React.SyntheticEvent.prototype.nativeEvent;
 /** @type {!EventTarget} */
React.SyntheticEvent.prototype.target;
 /** @type {!Date} */
React.SyntheticEvent.prototype.timeStamp;
 /** @type {string} */
React.SyntheticEvent.prototype.type;
/**
 * @return {void}
 */
React.SyntheticEvent.prototype.preventDefault = function() {};

/**
 * @return {boolean}
 */
React.SyntheticEvent.prototype.isDefaultPrevented = function() {};

/**
 * @return {void}
 */
React.SyntheticEvent.prototype.stopPropagation = function() {};

/**
 * @return {boolean}
 */
React.SyntheticEvent.prototype.isPropagationStopped = function() {};

/**
 * @return {void}
 */
React.SyntheticEvent.prototype.persist = function() {};

/**
 * React event system creates plugins and event properties dynamically.
 * These externs are needed when consuming React as a JavaScript module
 * in light of new ClojureScript compiler additions (as of version 1.9.456).
 * See the following link for an example.
 * https://github.com/facebook/react/blob/c7129c/src/renderers/dom/shared/eventPlugins/SimpleEventPlugin.js#L43
 */
var ResponderEventPlugin;
var SimpleEventPlugin;
var TapEventPlugin;
var EnterLeaveEventPlugin;
var ChangeEventPlugin;
var SelectEventPlugin;
var BeforeInputEventPlugin;

var bubbled;
var captured;
var topAbort;
var topAnimationEnd;
var topAnimationIteration;
var topAnimationStart;
var topBlur;
var topCancel;
var topCanPlay;
var topCanPlayThrough;
var topClick;
var topClose;
var topContextMenu;
var topCopy;
var topCut;
var topDoubleClick;
var topDrag;
var topDragEnd;
var topDragEnter;
var topDragExit;
var topDragLeave;
var topDragOver;
var topDragStart;
var topDrop;
var topDurationChange;
var topEmptied;
var topEncrypted;
var topEnded;
var topError;
var topFocus;
var topInput;
var topInvalid;
var topKeyDown;
var topKeyPress;
var topKeyUp;
var topLoad;
var topLoadedData;
var topLoadedMetadata;
var topLoadStart;
var topMouseDown;
var topMouseMove;
var topMouseOut;
var topMouseOver;
var topMouseUp;
var topPaste;
var topPause;
var topPlay;
var topPlaying;
var topProgress;
var topRateChange;
var topReset;
var topScroll;
var topSeeked;
var topSeeking;
var topStalled;
var topSubmit;
var topSuspend;
var topTimeUpdate;
var topTouchCancel;
var topTouchEnd;
var topTouchMove;
var topTouchStart;
var topTransitionEnd;
var topVolumeChange;
var topWaiting;
var topWheel;

React.createContext = function() {};

React.Context = function() {};
React.Context.prototype.Provider = function() {};
React.Context.prototype.Consumer = function() {};

/**
 * @const {symbol}
 */
React.Fragment;

/**
 * @const {symbol}
 */
React.StrictMode;
/**
 * @const {symbol}
 */
React.Suspense;
/**
 * @const {symbol}
 */
React.unstable_Profiler;
/**
 * @const {symbol}
 */
React.Element;
/**
 * @const {symbol}
 */
React.Provider;

// https://github.com/facebook/react/blob/master/packages/shared/isTextInputElement.js#L13-L29
// Closure will rename these properties during optimization
// But these are used dynamically to check against element props so they must not be renamed.
var isTextInputElement = {};
isTextInputElement.supportedInputTypes = {
  color: true,
  date: true,
  datetime: true,
  'datetime-local': true,
  email: true,
  month: true,
  number: true,
  password: true,
  range: true,
  search: true,
  tel: true,
  text: true,
  time: true,
  url: true,
  week: true
};

/**
 * The ReactDOM global object.
 *
 * @type {!Object}
 * @const
 * @suppress {const|duplicate}
 */
var ReactDOM = {};

/**
 * @param {React.Component} container
 * @param {Element} mountPoint
 * @param {(function(): void)=} opt_callback
 * @return {React.Component}
 */
ReactDOM.render = function(container, mountPoint, opt_callback) {};

/**
 * @param {Element} container
 * @return {boolean}
 */
ReactDOM.unmountComponentAtNode = function(container) {};

/**
 * Call the provided function in a context within which calls to `setState`
 * and friends are batched such that components aren't updated unnecessarily.
 *
 * @param {(function(): void)} callback Function which calls `setState`, `forceUpdate`, etc.
 */
ReactDOM.unstable_batchedUpdates = function(callback) {};

ReactDOM.createPortal = function() {};

/**
 * @interface
 */
React.ReactRoot = function() {};

/**
 * @param {React.Component} child
 * @param {(function(): void)=} opt_callback
 */
React.ReactRoot.prototype.render = function(child, opt_callback) {};

/**
 * @param {(function(): void)=} opt_callback
 */
React.ReactRoot.prototype.unmount = function(opt_callback) {};

/**
 * @param {Element} container
 * @return {React.ReactRoot}
 */
ReactDOM.unstable_createRoot = function(container) {};
