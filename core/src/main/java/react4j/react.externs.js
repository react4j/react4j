/**
 * @fileoverview Properties shared between React4j JavaScript records and J2CL overlays.
 * @externs
 */

/** @const */
var React4jExterns = {};

/**
 * Properties shared by the host input maps and their typed overlays.
 * @interface
 */
React4jExterns.HostInput = function() {};

/**
 * @type {Object}
 */
React4jExterns.HostInput.dangerouslySetInnerHTML;

/**
 * @type {string}
 */
React4jExterns.HostInput.__html;

/**
 * @type {string}
 */
React4jExterns.HostInput.key;

/**
 * @type {symbol}
 */
React4jExterns.HostInput.$$typeof;

/**
 * @type {Object}
 */
React4jExterns.HostInput._owner;

/** @type {*} */
React4jExterns.HostInput.ref;

/** @type {!Object} */
React4jExterns.HostInput.props;

/** @type {string} */
React4jExterns.HostInput.componentStack;

// Attributes not defined in default Closure Compiler DOM externs.
// http://facebook.github.io/react/docs/tags-and-attributes.html#html-attributes
// It happens because React favors camelCasing over allinlowercase.
// How to update list:
//   1) Open http://facebook.github.io/react/docs/tags-and-attributes.html#html-attributes
//   2) Github Search in google/closure-compiler for attribute.

/**
 * @type {Object}
 */
React4jExterns.HostInput.styleMedia;

/**
 * @type {boolean}
 */
React4jExterns.HostInput.allowFullScreen;

/**
 * @type {boolean}
 */
React4jExterns.HostInput.autoComplete;

/**
 * @type {boolean}
 */
React4jExterns.HostInput.autoFocus;

/**
 * @type {boolean}
 */
React4jExterns.HostInput.autoPlay;

/**
 * @type {boolean}
 */
React4jExterns.HostInput.noValidate;

/**
 * @type {boolean}
 */
React4jExterns.HostInput.spellCheck;

// http://facebook.github.io/react/docs/events.html

/**
 * @type {Function}
 */
React4jExterns.HostInput.onCopy;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onCut;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onPaste;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onCompositionEnd;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onCompositionStart;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onCompositionUpdate;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onKeyDown;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onKeyPress;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onKeyUp;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onFocus;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onBlur;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onChange;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onInput;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onInvalid;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onSubmit;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onClick;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onContextMenu;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDoubleClick;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDrag;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDragEnd;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDragEnter;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDragExit;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDragLeave;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDragOver;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDragStart;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onDrop;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseDown;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseEnter;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseLeave;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseMove;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseOut;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseOver;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onMouseUp;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onSelect;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onTouchCancel;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onTouchEnd;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onTouchMove;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onTouchStart;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onScroll;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onWheel;

/**
 * @type {Function}
 */
React4jExterns.HostInput.onAbort;
React4jExterns.HostInput.onCanPlay;
React4jExterns.HostInput.onCanPlayThrough;
React4jExterns.HostInput.onDurationChange;
React4jExterns.HostInput.onEmptied;
React4jExterns.HostInput.onEncrypted;
React4jExterns.HostInput.onEnded;
React4jExterns.HostInput.onError;
React4jExterns.HostInput.onLoadedData;
React4jExterns.HostInput.onLoadedMetadata;
React4jExterns.HostInput.onLoadStart;
React4jExterns.HostInput.onPause;
React4jExterns.HostInput.onPlay;
React4jExterns.HostInput.onPlaying;
React4jExterns.HostInput.onProgress;
React4jExterns.HostInput.onRateChange;
React4jExterns.HostInput.onSeeked;
React4jExterns.HostInput.onSeeking;
React4jExterns.HostInput.onStalled;
React4jExterns.HostInput.onSuspend;
React4jExterns.HostInput.onTimeUpdate;
React4jExterns.HostInput.onVolumeChange;
React4jExterns.HostInput.onWaiting;

React4jExterns.HostInput.onAnimationStart;
React4jExterns.HostInput.onAnimationEnd;
React4jExterns.HostInput.onAnimationIteration;

React4jExterns.HostInput.onTransitionEnd;

React4jExterns.HostInput.onToggle;

/** @record @struct */
React4jExterns.SyntheticEvent = function() {};
 /** @type {boolean} */
React4jExterns.SyntheticEvent.prototype.bubbles;
 /** @type {?} */
React4jExterns.SyntheticEvent.prototype.currentTarget;
 /** @type {boolean} */
React4jExterns.SyntheticEvent.prototype.cancelable;
 /** @type {boolean} */
React4jExterns.SyntheticEvent.prototype.defaultPrevented;
 /** @type {number} */
React4jExterns.SyntheticEvent.prototype.eventPhase;
 /** @type {boolean} */
React4jExterns.SyntheticEvent.prototype.isTrusted;
 /** @type {!Event} */
React4jExterns.SyntheticEvent.prototype.nativeEvent;
 /** @type {!EventTarget} */
React4jExterns.SyntheticEvent.prototype.target;
 /** @type {number} */
React4jExterns.SyntheticEvent.prototype.timeStamp;
 /** @type {string} */
React4jExterns.SyntheticEvent.prototype.type;
/**
 * @return {void}
 */
React4jExterns.SyntheticEvent.prototype.preventDefault = function() {};

/**
 * @return {boolean}
 */
React4jExterns.SyntheticEvent.prototype.isDefaultPrevented = function() {};

/**
 * @return {void}
 */
React4jExterns.SyntheticEvent.prototype.stopPropagation = function() {};

/**
 * @return {boolean}
 */
React4jExterns.SyntheticEvent.prototype.isPropagationStopped = function() {};

/**
 * @return {void}
 */
React4jExterns.SyntheticEvent.prototype.persist = function() {};
