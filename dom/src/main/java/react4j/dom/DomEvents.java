package react4j.dom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.dom.events.ReactEventHandler;
import react4j.dom.events.SyntheticEvent;

final class DomEvents {
    private static final boolean DEVELOPMENT = !"production".equals(System.getProperty("react4j.environment"));
    private static final Map<String, JsPropertyMap<@Nullable Object>> RELEASED_PROPERTIES = new HashMap<>();
    private static final String[] EVENTS = {
        "Abort",
        "AnimationEnd",
        "AnimationIteration",
        "AnimationStart",
        "AuxClick",
        "Blur",
        "CanPlay",
        "CanPlayThrough",
        "Cancel",
        "Change",
        "Click",
        "Close",
        "CompositionEnd",
        "CompositionStart",
        "CompositionUpdate",
        "ContextMenu",
        "Copy",
        "Cut",
        "DoubleClick",
        "Drag",
        "DragEnd",
        "DragEnter",
        "DragExit",
        "DragLeave",
        "DragOver",
        "DragStart",
        "Drop",
        "DurationChange",
        "Emptied",
        "Encrypted",
        "Ended",
        "Error",
        "Focus",
        "GotPointerCapture",
        "Input",
        "Invalid",
        "KeyDown",
        "KeyPress",
        "KeyUp",
        "Load",
        "LoadedData",
        "LoadedMetadata",
        "LoadStart",
        "LostPointerCapture",
        "MouseDown",
        "MouseMove",
        "MouseOut",
        "MouseOver",
        "MouseUp",
        "Paste",
        "Pause",
        "Play",
        "Playing",
        "PointerCancel",
        "PointerDown",
        "PointerMove",
        "PointerOut",
        "PointerOver",
        "PointerUp",
        "Progress",
        "RateChange",
        "Reset",
        "Scroll",
        "Seeked",
        "Seeking",
        "Stalled",
        "Submit",
        "Suspend",
        "TextInput",
        "TimeUpdate",
        "Toggle",
        "TouchCancel",
        "TouchEnd",
        "TouchMove",
        "TouchStart",
        "TransitionEnd",
        "VolumeChange",
        "Waiting",
        "Wheel"
    };
    private static final Set<String> NON_BUBBLING = Set.of(
            "abort",
            "canplay",
            "canplaythrough",
            "cancel",
            "close",
            "blur",
            "durationchange",
            "emptied",
            "encrypted",
            "ended",
            "error",
            "focus",
            "invalid",
            "load",
            "loadeddata",
            "loadedmetadata",
            "loadstart",
            "pause",
            "play",
            "playing",
            "progress",
            "ratechange",
            "scroll",
            "seeked",
            "seeking",
            "stalled",
            "suspend",
            "timeupdate",
            "toggle",
            "volumechange",
            "waiting");
    private static final List<String> DOCUMENT_SELECTION_EVENTS =
            List.of("selectionchange", "mousedown", "mouseup", "contextmenu", "dragend");
    private static final Set<String> MOUSE_ON_DISABLED =
            Set.of("Click", "DoubleClick", "MouseDown", "MouseMove", "MouseUp", "MouseEnter");
    private static final Set<String> DISABLABLE = Set.of("button", "input", "select", "textarea");
    private static final Set<String> TEXT_INPUT_TYPES = Set.of(
            "color",
            "date",
            "datetime",
            "datetime-local",
            "email",
            "month",
            "number",
            "password",
            "range",
            "search",
            "tel",
            "text",
            "time",
            "url",
            "week");
    private static final Map<String, String> KEY_ALIASES = Map.ofEntries(
            Map.entry("Esc", "Escape"),
            Map.entry("Spacebar", " "),
            Map.entry("Left", "ArrowLeft"),
            Map.entry("Up", "ArrowUp"),
            Map.entry("Right", "ArrowRight"),
            Map.entry("Down", "ArrowDown"),
            Map.entry("Del", "Delete"),
            Map.entry("Win", "OS"),
            Map.entry("Menu", "ContextMenu"),
            Map.entry("Apps", "ContextMenu"),
            Map.entry("Scroll", "ScrollLock"),
            Map.entry("MozPrintableKey", "Unidentified"));
    private static final Map<Integer, String> LEGACY_KEYS = Map.ofEntries(
            Map.entry(8, "Backspace"),
            Map.entry(9, "Tab"),
            Map.entry(12, "Clear"),
            Map.entry(13, "Enter"),
            Map.entry(16, "Shift"),
            Map.entry(17, "Control"),
            Map.entry(18, "Alt"),
            Map.entry(19, "Pause"),
            Map.entry(20, "CapsLock"),
            Map.entry(27, "Escape"),
            Map.entry(32, " "),
            Map.entry(33, "PageUp"),
            Map.entry(34, "PageDown"),
            Map.entry(35, "End"),
            Map.entry(36, "Home"),
            Map.entry(37, "ArrowLeft"),
            Map.entry(38, "ArrowUp"),
            Map.entry(39, "ArrowRight"),
            Map.entry(40, "ArrowDown"),
            Map.entry(45, "Insert"),
            Map.entry(46, "Delete"),
            Map.entry(144, "NumLock"),
            Map.entry(145, "ScrollLock"),
            Map.entry(224, "Meta"));
    private static final String[] EVENT_PROPERTIES = {
        "altKey",
        "animationName",
        "bubbles",
        "button",
        "buttons",
        "cancelable",
        "changedTouches",
        "charCode",
        "clientX",
        "clientY",
        "clipboardData",
        "code",
        "ctrlKey",
        "data",
        "dataTransfer",
        "deltaMode",
        "deltaX",
        "deltaY",
        "deltaZ",
        "detail",
        "elapsedTime",
        "eventPhase",
        "height",
        "inputType",
        "isComposing",
        "isPrimary",
        "isTrusted",
        "key",
        "keyCode",
        "locale",
        "location",
        "metaKey",
        "movementX",
        "movementY",
        "pageX",
        "pageY",
        "pointerId",
        "pointerType",
        "pressure",
        "propertyName",
        "pseudoElement",
        "relatedTarget",
        "repeat",
        "screenX",
        "screenY",
        "shiftKey",
        "targetTouches",
        "tangentialPressure",
        "tiltX",
        "tiltY",
        "timeStamp",
        "touches",
        "twist",
        "view",
        "which",
        "width"
    };

    private static final DomHost.WeakMap<DomHost.Node, Boundary> BOUNDARIES = new DomHost.WeakMap<>();
    private static final DomHost.WeakMap<DomHost.Node, State> STATES = new DomHost.WeakMap<>();

    private static int paused;
    private static boolean spaceKeypress;

    private DomEvents() {}

    static void pause() {
        paused++;
    }

    static void resume() {
        paused--;
    }

    static void update(
            final DomHost.Element element,
            final DomHost.Node container,
            final DomHost.@Nullable Element logicalParent,
            final JsPropertyMap<@Nullable Object> inputs) {
        var state = STATES.get(element);
        if (null == state) {
            var boundary = BOUNDARIES.get(container);
            if (null == boundary) {
                boundary = new Boundary(container);
                BOUNDARIES.set(container, boundary);
            }
            boundary.hosts++;
            state = new State(boundary, logicalParent, inputs);
            STATES.set(element, state);
        } else {
            state.logicalParent = logicalParent;
            state.inputs = inputs;
        }
    }

    @Nullable
    static State state(final DomHost.Element element) {
        return STATES.get(element);
    }

    static void clear(final DomHost.Element element) {
        final var state = STATES.get(element);
        if (null == state) {
            return;
        }
        STATES.delete(element);
        if (null != state.trackedProperty) {
            DomHost.properties(element).delete(state.trackedProperty);
        }
        final var boundary = state.boundary;
        if (0 == --boundary.hosts) {
            boundary.listeners.forEach(
                    (name, listener) -> boundary.node.removeEventListener(name, listener, NON_BUBBLING.contains(name)));
            final var document = boundary.node.document();
            for (final var event : DOCUMENT_SELECTION_EVENTS) {
                document.removeEventListener(event, boundary.selectionListener, false);
            }
            BOUNDARIES.delete(boundary.node);
        }
    }

    private static String nativeName(final String name) {
        return switch (name) {
            case "DoubleClick" -> "dblclick";
            case "TextInput" -> "textInput";
            default -> name.toLowerCase(Locale.ROOT);
        };
    }

    private static void dispatch(final Boundary boundary, final String name, final NativeEvent nativeEvent) {
        if (0 != paused) {
            return;
        }
        final var target = nativeEvent.target;
        if (null == target) {
            return;
        }
        DomHost.Node eventTarget = target;
        if (3 == target.nodeType && null != target.parentNode) {
            eventTarget = target.parentNode;
        }
        for (DomHost.Node node = eventTarget; null != node && node != boundary.node; node = node.parentNode) {
            final var state = STATES.get(node);
            if (null == state) {
                continue;
            }
            if (state.boundary != boundary) {
                return;
            }
            final var element = Js.<DomHost.Element>uncheckedCast(node);
            final boolean change = isChange(name, element, state);
            if ("Blur".equals(name)) {
                DomHost.commitNumberOnBlur(element, state);
            }
            final var selected = boundary.selectTarget(name, element);
            final DomHost.Node actualTarget = eventTarget;
            try {
                ReactDOM.batchedUpdates(() -> {
                    final var batch = new DispatchBatch();
                    if (!"Change".equals(name)
                            && !"TextInput".equals(name)
                            && !("KeyPress".equals(name) && 0 == charCode(DomHost.properties(nativeEvent)))
                            && !("Click".equals(name) && 2 == eventNumber(DomHost.properties(nativeEvent), "button"))) {
                        collectHandlers(
                                batch,
                                element,
                                actualTarget,
                                name,
                                nativeEvent,
                                name.startsWith("Composition") ? customInputData(nativeEvent) : null);
                    }
                    switch (name) {
                        case "MouseOver", "MouseOut", "PointerOver", "PointerOut" ->
                            collectEnterLeave(batch, element, name, nativeEvent);
                        default -> {}
                    }
                    if (change) {
                        collectHandlers(batch, element, actualTarget, "Change", nativeEvent, null);
                    }
                    if (null != selected) {
                        collectHandlers(batch, selected, selected, "Select", nativeEvent, null);
                    }
                    final var beforeInput = beforeInputData(boundary, name, nativeEvent);
                    if (Js.isTruthy(beforeInput)) {
                        collectHandlers(batch, element, actualTarget, "BeforeInput", nativeEvent, beforeInput);
                    }
                    batch.run();
                });
            } finally {
                if (change) {
                    DomHost.restoreAfterEvent(element);
                }
            }
            return;
        }
    }

    private static boolean isChange(final String event, final DomHost.Element element, final State state) {
        final var tag = element.localName;
        final var type = DomHost.properties(element).get("type");
        if ("select".equals(tag) || "input".equals(tag) && "file".equals(type)) {
            return "Change".equals(event);
        }
        final boolean checkable = DomHost.isCheckable(element);
        final boolean text = "textarea".equals(tag)
                || "input".equals(tag) && null != type && TEXT_INPUT_TYPES.contains(DomHost.stringify(type));
        if (checkable ? !"Click".equals(event) : !text || !"Input".equals(event) && !"Change".equals(event)) {
            return false;
        }
        if (null == state.trackedProperty) {
            return true;
        }
        final var value = DomHost.stringify(DomHost.controlValue(element));
        if (value.equals(state.lastValue)) {
            return false;
        }
        state.lastValue = value;
        return true;
    }

    private static @Nullable Object customInputData(final NativeEvent nativeEvent) {
        final var detail = DomHost.properties(nativeEvent).get("detail");
        return null != detail && "object".equals(Js.typeof(detail))
                ? DomHost.properties(detail).get("data")
                : null;
    }

    private static @Nullable Object beforeInputData(
            final Boundary boundary, final String name, final NativeEvent nativeEvent) {
        final var source = DomHost.properties(nativeEvent);
        if (boundary.hasTextInput) {
            return switch (name) {
                case "CompositionEnd" -> customInputData(nativeEvent);
                case "KeyPress" -> {
                    if (32 == eventNumber(source, "which")) {
                        spaceKeypress = true;
                        yield " ";
                    }
                    yield null;
                }
                case "TextInput" -> {
                    final var data = source.get("data");
                    yield spaceKeypress && " ".equals(data) ? null : data;
                }
                default -> null;
            };
        }
        return switch (name) {
            case "CompositionEnd" -> source.get("data");
            case "KeyPress" -> {
                final boolean ctrl = Js.isTruthy(source.get("ctrlKey"));
                final boolean alt = Js.isTruthy(source.get("altKey"));
                if ((ctrl || alt || Js.isTruthy(source.get("metaKey"))) && !(ctrl && alt)) {
                    yield null;
                }
                final var chars = source.get("char");
                final int which = eventNumber(source, "which");
                if (chars instanceof String text && text.length() > 1) {
                    yield text;
                }
                yield 0 == which ? null : String.valueOf((char) which);
            }
            default -> null;
        };
    }

    private static void collectHandlers(
            final DispatchBatch batch,
            final DomHost.Element origin,
            final DomHost.Node target,
            final String name,
            final NativeEvent nativeEvent,
            @Nullable final Object data) {
        final List<Invocation> captures = new ArrayList<>();
        final List<Invocation> bubbles = new ArrayList<>();
        final var property = "on" + name;
        for (DomHost.Element element = origin; null != element; ) {
            final var state = STATES.get(element);
            if (null == state) {
                break;
            }
            if (!mouseDisabled(element, name)) {
                addHandler(captures, element, state.inputs.get(property + "Capture"));
                addHandler(bubbles, element, state.inputs.get(property));
            }
            element = state.logicalParent;
        }
        batch.add(target, name, nativeEvent, null, data, captures, bubbles);
    }

    private static void collectEnterLeave(
            final DispatchBatch batch, final DomHost.Element origin, final String name, final NativeEvent nativeEvent) {
        final boolean leaving = name.endsWith("Out");
        final var source = DomHost.properties(nativeEvent);
        final var related = source.get("relatedTarget");
        final var relatedNode = Js.<DomHost.Node>uncheckedCast(
                null != related ? related : source.get(leaving ? "toElement" : "fromElement"));
        if (!leaving && null != relatedNode) {
            return;
        }
        final var from = leaving ? origin : null;
        final var to = leaving ? closestManaged(relatedNode) : origin;
        if (from == to) {
            return;
        }
        final var leavePath = logicalPath(from);
        final var enterPath = logicalPath(to);
        var leaveLength = leavePath.size();
        var enterLength = enterPath.size();
        while (0 < leaveLength && 0 < enterLength && leavePath.get(leaveLength - 1) == enterPath.get(enterLength - 1)) {
            leaveLength--;
            enterLength--;
        }
        final var prefix = name.startsWith("Pointer") ? "Pointer" : "Mouse";
        final var window = DomHost.properties(origin.document()).get("defaultView");
        if (null != from) {
            final var leaveName = prefix + "Leave";
            final var calls = pathHandlers(leavePath, leaveLength, leaveName);
            batch.add(from, leaveName, nativeEvent, null != to ? to : window, null, List.of(), calls);
        }
        if (null != to) {
            final var enterName = prefix + "Enter";
            final var calls = pathHandlers(enterPath, enterLength, enterName);
            batch.add(to, enterName, nativeEvent, null != from ? from : window, null, calls, List.of());
        }
    }

    private static DomHost.@Nullable Element closestManaged(DomHost.@Nullable Node node) {
        for (var current = node; null != current; current = current.parentNode) {
            if (null != STATES.get(current)) {
                return Js.uncheckedCast(current);
            }
        }
        return null;
    }

    private static List<DomHost.Element> logicalPath(DomHost.@Nullable Element origin) {
        final List<DomHost.Element> path = new ArrayList<>();
        for (var element = origin; null != element; ) {
            final var state = STATES.get(element);
            if (null == state) {
                break;
            }
            path.add(element);
            element = state.logicalParent;
        }
        return path;
    }

    private static List<Invocation> pathHandlers(
            final List<DomHost.Element> path, final int length, final String name) {
        final List<Invocation> calls = new ArrayList<>();
        final var property = "on" + name;
        for (int i = 0; i < length; i++) {
            final var element = path.get(i);
            final var state = STATES.get(element);
            if (null != state && !mouseDisabled(element, name)) {
                addHandler(calls, element, state.inputs.get(property));
            }
        }
        return calls;
    }

    private static boolean mouseDisabled(final DomHost.Element element, final String name) {
        return MOUSE_ON_DISABLED.contains(name)
                && DISABLABLE.contains(element.localName)
                && Boolean.TRUE.equals(DomHost.properties(element).get("disabled"));
    }

    private static final class DispatchBatch {
        private final List<QueuedDispatch> events = new ArrayList<>();
        private @Nullable Throwable failure;

        void add(
                final DomHost.Node target,
                final String name,
                final NativeEvent nativeEvent,
                @Nullable final Object relatedTarget,
                @Nullable final Object data,
                final List<Invocation> captures,
                final List<Invocation> bubbles) {
            if (captures.isEmpty() && bubbles.isEmpty()) {
                return;
            }
            final var pool = eventKind(name).pool;
            final var available = pool.pollLast();
            final var event = null == available ? new Dispatch() : available;
            event.initialize(target, name, nativeEvent, relatedTarget, data);
            events.add(new QueuedDispatch(event, pool, captures, bubbles));
        }

        void run() {
            for (final var entry : events) {
                final var event = entry.event();
                final var captures = entry.captures();
                final var bubbles = entry.bubbles();
                for (int i = captures.size() - 1; 0 <= i && !event.stopped; i--) {
                    call(event, captures.get(i));
                }
                for (int i = 0; i < bubbles.size() && !event.stopped; i++) {
                    call(event, bubbles.get(i));
                }
                if (!event.persistent) {
                    event.release();
                    final var pool = entry.pool();
                    if (pool.size() < 10) {
                        pool.addLast(event);
                    }
                }
            }
            if (failure instanceof RuntimeException exception) {
                throw exception;
            }
            if (failure instanceof Error error) {
                throw error;
            }
        }

        private void call(final Dispatch event, final Invocation invocation) {
            try {
                event.call(invocation);
            } catch (RuntimeException | Error error) {
                if (null == failure) {
                    failure = error;
                }
            }
        }
    }

    private record QueuedDispatch(
            Dispatch event, ArrayDeque<Dispatch> pool, List<Invocation> captures, List<Invocation> bubbles) {}

    private static void addHandler(
            final List<Invocation> calls, final DomHost.Element element, @Nullable final Object handler) {
        if ("function".equals(Js.typeof(handler))) {
            calls.add(new Invocation(element, Js.uncheckedCast(handler)));
        }
    }

    static final class State {
        final Boundary boundary;
        DomHost.@Nullable Element logicalParent;
        JsPropertyMap<@Nullable Object> inputs;
        boolean controlInitialized;
        boolean wasMultiple;
        boolean controlledInput;
        String initialValue = "";

        @Nullable
        String lastValue;

        @Nullable
        String trackedProperty;

        State(
                final Boundary boundary,
                final DomHost.@Nullable Element logicalParent,
                final JsPropertyMap<@Nullable Object> inputs) {
            this.boundary = boundary;
            this.logicalParent = logicalParent;
            this.inputs = inputs;
        }
    }

    private static final class Boundary {
        final DomHost.Node node;
        final Map<String, Listener> listeners = new HashMap<>();
        final Listener selectionListener;
        private DomHost.@Nullable Element focused;
        private @Nullable Selection lastSelection;
        private boolean mouseDown;
        private final boolean hasTextInput;
        int hosts;

        Boundary(final DomHost.Node node) {
            this.node = node;
            final var window = DomHost.properties(node.document()).get("defaultView");
            hasTextInput = null != window && DomHost.properties(window).has("TextEvent");
            for (final var name : EVENTS) {
                final var nativeName = nativeName(name);
                final Listener listener = event -> dispatch(this, name, event);
                listeners.put(nativeName, listener);
                node.addEventListener(nativeName, listener, NON_BUBBLING.contains(nativeName));
            }
            selectionListener = event -> {
                switch (event.type) {
                    case "mousedown" -> {
                        mouseDown = true;
                        return;
                    }
                    case "mouseup", "contextmenu", "dragend" -> mouseDown = false;
                    default -> {}
                }
                final var selected = changedSelection();
                if (null != selected) {
                    ReactDOM.batchedUpdates(() -> {
                        final var batch = new DispatchBatch();
                        collectHandlers(batch, selected, selected, "Select", event, null);
                        batch.run();
                    });
                }
            };
            final var document = node.document();
            for (final var event : DOCUMENT_SELECTION_EVENTS) {
                document.addEventListener(event, selectionListener, false);
            }
        }

        private DomHost.@Nullable Element selectTarget(final String name, final DomHost.Element element) {
            switch (name) {
                case "Focus" -> {
                    final var props = DomHost.properties(element);
                    if ("textarea".equals(element.localName)
                            || "input".equals(element.localName)
                                    && TEXT_INPUT_TYPES.contains(DomHost.stringify(props.get("type")))
                            || "true".equals(props.get("contentEditable"))) {
                        focused = element;
                        lastSelection = null;
                    }
                }
                case "Blur" -> {
                    focused = null;
                    lastSelection = null;
                }
                case "MouseDown" -> mouseDown = true;
                case "ContextMenu", "DragEnd", "MouseUp" -> {
                    mouseDown = false;
                    return changedSelection();
                }
                case "KeyDown", "KeyUp" -> {
                    return changedSelection();
                }
                default -> {}
            }
            return null;
        }

        private DomHost.@Nullable Element changedSelection() {
            final var document = node.document();
            if (mouseDown
                    || null == focused
                    || focused != DomHost.properties(document).get("activeElement")) {
                return null;
            }
            final var state = STATES.get(focused);
            boolean listening = false;
            for (var current = state; null != current; ) {
                if (current.inputs.has("onSelect") || current.inputs.has("onSelectCapture")) {
                    listening = true;
                    break;
                }
                current = null == current.logicalParent ? null : STATES.get(current.logicalParent);
            }
            if (!listening) {
                return null;
            }
            final var currentSelection = Selection.read(document, focused);
            if (currentSelection.equals(lastSelection)) {
                return null;
            }
            lastSelection = currentSelection;
            return focused;
        }
    }

    private record Selection(
            @Nullable Object anchor,
            @Nullable Object focus,
            @Nullable Object start,
            @Nullable Object end) {
        static Selection read(final DomHost.Document document, final DomHost.Element element) {
            final var props = DomHost.properties(element);
            final var start = props.get("selectionStart");
            if (null != start) {
                return new Selection(null, null, start, props.get("selectionEnd"));
            }
            final var selection = document.getSelection();
            if (null == selection) {
                return new Selection(null, null, null, null);
            }
            return new Selection(
                    selection.get("anchorNode"),
                    selection.get("focusNode"),
                    selection.get("anchorOffset"),
                    selection.get("focusOffset"));
        }
    }

    private static EventKind eventKind(final String name) {
        return switch (name) {
            case "AnimationEnd", "AnimationIteration", "AnimationStart" -> EventKind.ANIMATION;
            case "BeforeInput" -> EventKind.INPUT;
            case "Blur", "Focus" -> EventKind.FOCUS;
            case "Copy", "Cut", "Paste" -> EventKind.CLIPBOARD;
            case "CompositionEnd", "CompositionStart", "CompositionUpdate" -> EventKind.COMPOSITION;
            case "Drag", "DragEnd", "DragEnter", "DragExit", "DragLeave", "DragOver", "DragStart", "Drop" ->
                EventKind.DRAG;
            case "KeyDown", "KeyPress", "KeyUp" -> EventKind.KEYBOARD;
            case "AuxClick",
                    "Click",
                    "ContextMenu",
                    "DoubleClick",
                    "MouseDown",
                    "MouseEnter",
                    "MouseLeave",
                    "MouseMove",
                    "MouseOut",
                    "MouseOver",
                    "MouseUp" -> EventKind.MOUSE;
            case "GotPointerCapture",
                    "LostPointerCapture",
                    "PointerCancel",
                    "PointerDown",
                    "PointerEnter",
                    "PointerLeave",
                    "PointerMove",
                    "PointerOut",
                    "PointerOver",
                    "PointerUp" -> EventKind.POINTER;
            case "Scroll" -> EventKind.UI;
            case "TouchCancel", "TouchEnd", "TouchMove", "TouchStart" -> EventKind.TOUCH;
            case "TransitionEnd" -> EventKind.TRANSITION;
            case "Wheel" -> EventKind.WHEEL;
            default -> EventKind.EVENT;
        };
    }

    private enum EventKind {
        ANIMATION,
        INPUT,
        FOCUS,
        CLIPBOARD,
        COMPOSITION,
        DRAG,
        KEYBOARD,
        MOUSE,
        POINTER,
        UI,
        TOUCH,
        TRANSITION,
        WHEEL,
        EVENT;

        final ArrayDeque<Dispatch> pool = new ArrayDeque<>();
    }

    private static int eventNumber(final JsPropertyMap<@Nullable Object> event, final String name) {
        final var value = event.get(name);
        return "number".equals(Js.typeof(value)) ? Js.asInt(value) : 0;
    }

    private static int charCode(final JsPropertyMap<@Nullable Object> event) {
        final int keyCode = eventNumber(event, "keyCode");
        final int charCode = event.has("charCode") ? eventNumber(event, "charCode") : keyCode;
        if (10 == charCode || 0 == charCode && 13 == keyCode) {
            return 13;
        }
        return 32 <= charCode || 13 == charCode ? charCode : 0;
    }

    private static String eventKey(
            final JsPropertyMap<@Nullable Object> event, final boolean keyPress, final int code) {
        final var key = event.get("key");
        if (null != key && !"".equals(key)) {
            final var text = DomHost.stringify(key);
            final var normalized = KEY_ALIASES.getOrDefault(text, text);
            if (!"Unidentified".equals(normalized)) {
                return normalized;
            }
        }
        if (keyPress) {
            return 13 == code ? "Enter" : String.valueOf((char) code);
        }
        return 112 <= code && code <= 123 ? "F" + (code - 111) : LEGACY_KEYS.getOrDefault(code, "Unidentified");
    }

    private record Invocation(DomHost.Element element, ReactEventHandler handler) {}

    private static final class Dispatch {
        private final JsPropertyMap<@Nullable Object> properties = JsPropertyMap.of();
        private final SyntheticEvent<?> event = Js.uncheckedCast(properties);
        private boolean stopped;
        private boolean persistent;
        private boolean released;

        void initialize(
                final DomHost.Node target,
                final String name,
                final NativeEvent nativeEvent,
                @Nullable final Object relatedTarget,
                @Nullable final Object data) {
            stopped = false;
            persistent = false;
            if (DEVELOPMENT && released) {
                RELEASED_PROPERTIES.keySet().forEach(properties::delete);
            }
            final var source = DomHost.properties(nativeEvent);
            for (final var property : EVENT_PROPERTIES) {
                properties.set(property, source.get(property));
            }
            properties.set(
                    "type",
                    switch (name) {
                        case "Blur" -> "blur";
                        case "Change" -> "change";
                        case "Focus" -> "focus";
                        case "Select" -> "select";
                        case "MouseEnter", "MouseLeave", "PointerEnter", "PointerLeave" -> nativeName(name);
                        default -> nativeEvent.type;
                    });
            if (EventKind.KEYBOARD == eventKind(name)) {
                final boolean keyPress = "KeyPress".equals(name);
                final int code = keyPress ? charCode(source) : eventNumber(source, "keyCode");
                properties.set("key", eventKey(source, keyPress, code));
                properties.set("charCode", keyPress ? code : 0);
                properties.set("keyCode", keyPress ? 0 : code);
                properties.set("which", code);
            }
            properties.set("target", target);
            if (null != relatedTarget) {
                properties.set("relatedTarget", relatedTarget);
            }
            if (null != data) {
                properties.set("data", data);
            }
            properties.set("currentTarget", null);
            properties.set("nativeEvent", nativeEvent);
            properties.set("defaultPrevented", nativeEvent.defaultPrevented);
            properties.set("preventDefault", (Action) () -> {
                nativeEvent.preventDefault();
                properties.set("defaultPrevented", true);
            });
            properties.set("stopPropagation", (Action) () -> {
                stopped = true;
                nativeEvent.stopPropagation();
            });
            properties.set("isPropagationStopped", (Flag) () -> stopped);
            properties.set("isDefaultPrevented", (Flag) () -> Boolean.TRUE.equals(properties.get("defaultPrevented")));
            properties.set("getModifierState", (Modifier) key -> {
                if ("function".equals(Js.typeof(source.get("getModifierState")))) {
                    return nativeEvent.getModifierState(key);
                }
                return switch (key) {
                    case "Alt" -> Js.isTruthy(source.get("altKey"));
                    case "Control" -> Js.isTruthy(source.get("ctrlKey"));
                    case "Meta" -> Js.isTruthy(source.get("metaKey"));
                    case "Shift" -> Js.isTruthy(source.get("shiftKey"));
                    default -> false;
                };
            });
            properties.set("persist", (Action) () -> persistent = true);
            properties.set("isPersistent", (Flag) () -> persistent);
        }

        void release() {
            for (final var property : EVENT_PROPERTIES) {
                releaseProperty(property, null);
            }
            releaseProperty("type", null);
            releaseProperty("target", null);
            releaseProperty("currentTarget", null);
            releaseProperty("nativeEvent", null);
            releaseProperty("defaultPrevented", null);
            releaseProperty("preventDefault", (Action) () -> {});
            releaseProperty("stopPropagation", (Action) () -> {});
            releaseProperty("isDefaultPrevented", (Flag) () -> false);
            releaseProperty("isPropagationStopped", (Flag) () -> false);
            releaseProperty("getModifierState", null);
            released = true;
        }

        private void releaseProperty(final String name, @Nullable final Object replacement) {
            if (DEVELOPMENT) {
                final var descriptor = RELEASED_PROPERTIES.computeIfAbsent(name, key -> {
                    final var warning = JsPropertyMap.<@Nullable Object>of();
                    warning.set("configurable", true);
                    warning.set("get", (ReleasedGetter) () -> {
                        warnReleased(key, replacement, false);
                        return replacement;
                    });
                    warning.set("set", (Action) () -> warnReleased(key, replacement, true));
                    return warning;
                });
                DomHost.defineProperty(properties, name, descriptor);
            } else {
                properties.set(name, replacement);
            }
        }

        void call(final Invocation call) {
            properties.set("currentTarget", call.element());
            try {
                call.handler().onReactEvent(event);
            } finally {
                properties.set("currentTarget", null);
            }
        }
    }

    private static void warnReleased(final String name, @Nullable final Object value, final boolean writing) {
        final var method = "function".equals(Js.typeof(value));
        final var action = (writing ? "setting" : "accessing") + (method ? " the method" : " the property");
        final var result =
                writing ? "This is effectively a no-op" : method ? "This is a no-op function" : "This is set to null";
        warn("Warning: This synthetic event is reused for performance reasons. If you're seeing this, you're "
                + action + " `" + name + "` on a released/nullified synthetic event. " + result
                + ". If you must keep the original synthetic event around, use event.persist()."
                + " See https://fb.me/react-event-pooling for more information.");
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void warn(String message);

    @JsFunction
    private interface ReleasedGetter {
        @Nullable
        Object get();
    }

    @JsFunction
    interface Listener {
        void accept(NativeEvent event);
    }

    @JsFunction
    private interface Action {
        void run();
    }

    @JsFunction
    private interface Flag {
        boolean get();
    }

    @JsFunction
    private interface Modifier {
        boolean get(String key);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Event")
    @SuppressWarnings("NullAway.Init")
    static class NativeEvent {
        DomHost.@Nullable Node target;
        String type;
        boolean defaultPrevented;

        native void preventDefault();

        native void stopPropagation();

        native boolean getModifierState(String key);
    }
}
