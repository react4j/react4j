package react4j.dom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
    private static final Set<String> MEDIA_EVENTS = Set.of(
            "abort",
            "canplay",
            "canplaythrough",
            "durationchange",
            "emptied",
            "encrypted",
            "ended",
            "error",
            "loadeddata",
            "loadedmetadata",
            "loadstart",
            "pause",
            "play",
            "playing",
            "progress",
            "ratechange",
            "seeked",
            "seeking",
            "stalled",
            "suspend",
            "timeupdate",
            "volumechange",
            "waiting");
    private static final Set<String> FORM_EVENTS = Set.of("reset", "submit");
    private static final Set<String> INVALID_EVENT = Set.of("invalid");
    private static final Set<String> ERROR_EVENT = Set.of("error");
    private static final Set<String> NON_BUBBLING =
            Set.of("blur", "cancel", "close", "focus", "load", "scroll", "toggle");
    private static final Set<String> REGISTERED_EVENTS = Set.of(EVENTS);
    private static final JsPropertyMap<Boolean> OBSERVED_HANDLERS = JsPropertyMap.of();
    private static final List<String> DOCUMENT_SELECTION_EVENTS =
            List.of("selectionchange", "mousedown", "mouseup", "contextmenu", "dragend");
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
    private static final DomHost.WeakMap<DomHost.Node, Boundary> BOUNDARIES = new DomHost.WeakMap<>();
    private static final DomHost.WeakMap<DomHost.Node, State> STATES = new DomHost.WeakMap<>();
    private static final DomHost.WeakMap<DomHost.Node, DocumentSubscriptions> DOCUMENT_SUBSCRIPTIONS =
            new DomHost.WeakMap<>();
    private static final LegacyMovement MOVEMENT_X = new LegacyMovement();
    private static final LegacyMovement MOVEMENT_Y = new LegacyMovement();
    private static final int EXTERNAL_MOUSE = 1;
    private static final int EXTERNAL_POINTER = 2;
    private static final int DOCUMENT_SELECTION = 4;

    private static int paused;
    private static boolean spaceKeypress;
    private static boolean dispatching;
    private static DomHost.@Nullable Element changedControl;
    private static @Nullable List<DomHost.Element> moreChangedControls;

    private DomEvents() {}

    static boolean isRegistered(final String property) {
        if (!property.startsWith("on")) {
            return false;
        }
        final var name = property.substring(2);
        return switch (name) {
            case "MouseEnter",
                    "MouseLeave",
                    "PointerEnter",
                    "PointerLeave",
                    "BeforeInput",
                    "BeforeInputCapture",
                    "Select",
                    "SelectCapture" -> true;
            default -> {
                final var event = name.endsWith("Capture") ? name.substring(0, name.length() - 7) : name;
                yield !"TextInput".equals(event) && REGISTERED_EVENTS.contains(event);
            }
        };
    }

    static @Nullable String standardHandler(final String lower) {
        if ("ondblclick".equals(lower)) {
            return "onDoubleClick";
        }
        final var capture = lower.endsWith("capture");
        final var suffix = capture ? "Capture" : "";
        final var base = capture ? lower.substring(0, lower.length() - 7) : lower;
        for (final var event : EVENTS) {
            if (base.equalsIgnoreCase("on" + event) && isRegistered("on" + event + suffix)) {
                return "on" + event + suffix;
            }
        }
        for (final var event :
                new String[] {"MouseEnter", "MouseLeave", "PointerEnter", "PointerLeave", "BeforeInput", "Select"}) {
            final var handler = "on" + event + suffix;
            if (lower.equalsIgnoreCase(handler) && isRegistered(handler)) {
                return handler;
            }
        }
        return null;
    }

    static boolean observeHandler(final String name, @Nullable final Object value) {
        if (null == value) {
            return false;
        }
        if (OBSERVED_HANDLERS.has(name)) {
            return true;
        }
        if (isRegistered(name)) {
            OBSERVED_HANDLERS.set(name, true);
            return true;
        }
        return false;
    }

    static String invalidListener(final String property, final String type) {
        return "Expected `" + property + "` listener to be a function, instead got a value of `" + type + "` type.";
    }

    static void observeDocumentEvent(final State state, final String name, @Nullable final Object handler) {
        final int event =
                switch (name) {
                    case "onMouseEnter", "onMouseLeave" -> EXTERNAL_MOUSE;
                    case "onPointerEnter", "onPointerLeave" -> EXTERNAL_POINTER;
                    case "onSelect", "onSelectCapture" -> DOCUMENT_SELECTION;
                    default -> 0;
                };
        if (0 != event && 0 == (state.documentEvents & event) && Js.isTruthy(handler)) {
            state.documentEvents |= event;
            final var root = state.rootContainer;
            if (null != root) {
                Objects.requireNonNull(DOCUMENT_SUBSCRIPTIONS.get(root)).observe(event);
            }
        }
    }

    static void pause() {
        paused++;
    }

    static void resume() {
        paused--;
    }

    static void attach(
            final State state,
            final DomHost.Node container,
            final DomHost.Node rootContainer,
            @Nullable final State logicalParent) {
        final var element = state.element;
        var boundary = BOUNDARIES.get(container);
        if (null == boundary) {
            boundary = new Boundary(container);
            BOUNDARIES.set(container, boundary);
        }
        boundary.hosts++;
        state.boundary = boundary;
        state.rootContainer = rootContainer;
        state.logicalParent = logicalParent;
        STATES.set(element, state);
        var subscriptions = DOCUMENT_SUBSCRIPTIONS.get(rootContainer);
        if (null == subscriptions) {
            subscriptions = new DocumentSubscriptions();
            DOCUMENT_SUBSCRIPTIONS.set(rootContainer, subscriptions);
        }
        subscriptions.attach(boundary);
        subscriptions.observe(state.documentEvents);
        final var events = localEvents(state.tag);
        if (null != events) {
            for (final var event : events) {
                element.addEventListener(event, Objects.requireNonNull(boundary.listeners.get(event)), false);
            }
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
        final var boundary = Objects.requireNonNull(state.boundary);
        final var root = Objects.requireNonNull(state.rootContainer);
        final var subscriptions = Objects.requireNonNull(DOCUMENT_SUBSCRIPTIONS.get(root));
        if (subscriptions.detach(boundary)) {
            DOCUMENT_SUBSCRIPTIONS.delete(root);
        }
        final var events = localEvents(state.tag);
        if (null != events) {
            for (final var event : events) {
                element.removeEventListener(event, Objects.requireNonNull(boundary.listeners.get(event)), false);
            }
        }
        if (0 == --boundary.hosts) {
            boundary.listeners.forEach(name -> {
                if (isDelegated(name)) {
                    boundary.node.removeEventListener(
                            name, Objects.requireNonNull(boundary.listeners.get(name)), NON_BUBBLING.contains(name));
                }
            });
            boundary.clearDocumentListeners();
            BOUNDARIES.delete(boundary.node);
        }
    }

    private static boolean isDelegated(final String event) {
        return !MEDIA_EVENTS.contains(event) && !FORM_EVENTS.contains(event) && !INVALID_EVENT.contains(event);
    }

    private static @Nullable Set<String> localEvents(final String tag) {
        return switch (tag) {
            case "audio", "video" -> MEDIA_EVENTS;
            case "form" -> FORM_EVENTS;
            case "input", "select", "textarea" -> INVALID_EVENT;
            case "image", "img", "link", "source" -> ERROR_EVENT;
            default -> null;
        };
    }

    private static String nativeName(final String name) {
        return switch (name) {
            case "DoubleClick" -> "dblclick";
            case "TextInput" -> "textInput";
            default -> name.toLowerCase(Locale.ROOT);
        };
    }

    private static void dispatch(
            final Boundary boundary, final String name, final NativeEvent nativeEvent, final boolean interactive) {
        if (0 != paused) {
            return;
        }
        if (interactive) {
            Reconciler.interactiveUpdates(() -> dispatchBatched(boundary, name, nativeEvent));
        } else {
            dispatchBatched(boundary, name, nativeEvent);
        }
    }

    private static boolean isInteractive(String name) {
        return switch (name) {
            case "Blur",
                    "Cancel",
                    "Click",
                    "Close",
                    "ContextMenu",
                    "Copy",
                    "Cut",
                    "AuxClick",
                    "DoubleClick",
                    "DragEnd",
                    "DragStart",
                    "Drop",
                    "Focus",
                    "Input",
                    "Invalid",
                    "KeyDown",
                    "KeyPress",
                    "KeyUp",
                    "MouseDown",
                    "MouseUp",
                    "Paste",
                    "Pause",
                    "Play",
                    "PointerCancel",
                    "PointerDown",
                    "PointerUp",
                    "RateChange",
                    "Reset",
                    "Seeked",
                    "Submit",
                    "TouchCancel",
                    "TouchEnd",
                    "TouchStart",
                    "VolumeChange" -> true;
            default -> false;
        };
    }

    private static void dispatchBatched(final Boundary boundary, final String name, final NativeEvent nativeEvent) {
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
            final DomHost.Node actualTarget = eventTarget;
            final var outerRoots = outerRoots(state);
            batchDispatch(() -> {
                dispatchAt(element, state, actualTarget, name, nativeEvent);
                if (null != outerRoots) {
                    for (final var outer : outerRoots) {
                        dispatchAt(outer.element(), outer.state(), actualTarget, name, nativeEvent);
                    }
                }
            });
            return;
        }
    }

    private static void batchDispatch(final Runnable action) {
        if (dispatching) {
            action.run();
            return;
        }
        dispatching = true;
        try {
            Reconciler.batchedUpdates(action::run);
        } finally {
            dispatching = false;
            final var control = changedControl;
            final var remaining = moreChangedControls;
            changedControl = null;
            moreChangedControls = null;
            if (null != control) {
                Reconciler.flushInteractive();
                DomHost.restoreAfterEvent(control);
                if (null != remaining) {
                    remaining.forEach(DomHost::restoreAfterEvent);
                }
            }
        }
    }

    private static @Nullable List<Origin> outerRoots(final State inner) {
        final var container = DomHost.requireNode(inner.rootContainer);
        final var outer = closestManaged(container);
        if (null == outer) {
            return null;
        }
        final List<Origin> roots = new ArrayList<>();
        final Set<DomHost.Node> seen = new HashSet<>();
        seen.add(container);
        for (var element = outer; null != element; ) {
            final var state = STATES.get(element);
            if (null == state) {
                break;
            }
            final var outerContainer = DomHost.requireNode(state.rootContainer);
            if (!seen.add(outerContainer)) {
                break;
            }
            roots.add(new Origin(element, state));
            element = closestManaged(outerContainer);
        }
        return roots;
    }

    private record Origin(DomHost.Element element, State state) {}

    private static void dispatchAt(
            final DomHost.Element element,
            final State state,
            final DomHost.Node target,
            final String name,
            final NativeEvent nativeEvent) {
        final boolean change = isChange(name, element, state);
        if (change) {
            if (null == changedControl) {
                changedControl = element;
            } else {
                if (null == moreChangedControls) {
                    moreChangedControls = new ArrayList<>();
                }
                moreChangedControls.add(element);
            }
        }
        final var boundary = Objects.requireNonNull(state.boundary);
        if ("Blur".equals(name)) {
            DomHost.commitNumberOnBlur(element, state);
        }
        final var selected = boundary.selectTarget(name, element);
        final var batch = new DispatchBatch();
        try {
            if (!"Change".equals(name)
                    && !"TextInput".equals(name)
                    && !("KeyPress".equals(name) && 0 == charCode(DomHost.properties(nativeEvent)))
                    && !("Click".equals(name) && 2 == eventNumber(DomHost.properties(nativeEvent), "button"))) {
                collectHandlers(
                        batch,
                        state,
                        target,
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
                collectHandlers(batch, state, target, "Change", nativeEvent, null);
            }
            if (null != selected) {
                collectHandlers(batch, STATES.get(selected), selected, "Select", nativeEvent, null);
            }
            final var beforeInput = beforeInputData(boundary, name, nativeEvent);
            if (Js.isTruthy(beforeInput)) {
                collectHandlers(batch, state, target, "BeforeInput", nativeEvent, beforeInput);
            }
        } catch (RuntimeException | Error error) {
            batch.discard();
            throw error;
        }
        batch.run();
    }

    private static boolean isChange(final String event, final DomHost.Element element, final State state) {
        if (!"Click".equals(event) && !"Input".equals(event) && !"Change".equals(event)) {
            return false;
        }
        final var tag = state.controlInitialized ? state.tag : element.localName;
        final var type = DomHost.properties(element).get("type");
        if ("select".equals(tag) || "input".equals(tag) && "file".equals(type)) {
            return "Change".equals(event);
        }
        final boolean checkable = "input".equals(tag) && DomHost.isCheckableType(type);
        final boolean text = "textarea".equals(tag)
                || "input".equals(tag) && null != type && TEXT_INPUT_TYPES.contains(DomHost.stringify(type));
        if (checkable ? !"Click".equals(event) : !text || !"Input".equals(event) && !"Change".equals(event)) {
            return false;
        }
        if (null == state.trackedProperty) {
            return true;
        }
        final var value = DomHost.stringify(DomHost.controlValue(element, checkable));
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
            @Nullable final State origin,
            final DomHost.Node target,
            final String name,
            final NativeEvent nativeEvent,
            @Nullable final Object data) {
        Invocation captures = null;
        Invocation bubbles = null;
        final var property = "on" + name;
        final var captureProperty = property + "Capture";
        final boolean hasCapture = OBSERVED_HANDLERS.has(captureProperty);
        final boolean hasBubble = OBSERVED_HANDLERS.has(property);
        if (!hasCapture && !hasBubble) {
            return;
        }
        final boolean checkDisabled =
                switch (name) {
                    case "Click", "DoubleClick", "MouseDown", "MouseMove", "MouseUp" -> true;
                    default -> false;
                };
        for (var state = origin; null != state; state = state.logicalParent) {
            if (state.hasHandlers) {
                final var capture = hasCapture ? state.inputs.get(captureProperty) : null;
                final var bubble = hasBubble ? state.inputs.get(property) : null;
                if ((null != capture || null != bubble)
                        && !(checkDisabled
                                && DISABLABLE.contains(state.tag)
                                && Js.isTruthy(state.inputs.get("disabled")))) {
                    captures = collectHandler(captures, state.element, captureProperty, capture, true);
                    bubbles = collectHandler(bubbles, state.element, property, bubble, false);
                }
            }
        }
        batch.add(target, name, nativeEvent, null, data, captures, bubbles);
    }

    private static void collectEnterLeave(
            final DispatchBatch batch, final DomHost.Element origin, final String name, final NativeEvent nativeEvent) {
        final boolean leaving = name.endsWith("Out");
        final var relatedNode = movementRelated(nativeEvent, leaving);
        if (!leaving && null != relatedNode) {
            return;
        }
        final var from = leaving ? origin : null;
        final var to = leaving ? closestManaged(relatedNode) : origin;
        collectEnterLeave(batch, from, to, name, nativeEvent);
    }

    private static DomHost.@Nullable Node movementRelated(final NativeEvent nativeEvent, final boolean leaving) {
        final var source = DomHost.properties(nativeEvent);
        final var related = source.get("relatedTarget");
        return Js.uncheckedCast(null != related ? related : source.get(leaving ? "toElement" : "fromElement"));
    }

    private static void collectEnterLeave(
            final DispatchBatch batch,
            final DomHost.@Nullable Element from,
            final DomHost.@Nullable Element to,
            final String name,
            final NativeEvent nativeEvent) {
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
        final var document = DomHost.requireNode(null != from ? from : to).document();
        final var window = DomHost.properties(document).get("defaultView");
        if (null != from) {
            final var leaveName = prefix + "Leave";
            final var calls = pathHandlers(leavePath, leaveLength, leaveName, false);
            batch.add(from, leaveName, nativeEvent, null != to ? to : window, null, null, calls);
        }
        if (null != to) {
            final var enterName = prefix + "Enter";
            final var calls = pathHandlers(enterPath, enterLength, enterName, true);
            batch.add(to, enterName, nativeEvent, null != from ? from : window, null, calls, null);
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

    private static List<State> logicalPath(DomHost.@Nullable Element origin) {
        final List<State> path = new ArrayList<>();
        if (null != origin) {
            for (var state = STATES.get(origin); null != state; state = state.logicalParent) {
                path.add(state);
            }
        }
        return path;
    }

    private static @Nullable Invocation pathHandlers(
            final List<State> path, final int length, final String name, final boolean capture) {
        Invocation calls = null;
        final var property = "on" + name;
        for (int i = 0; i < length; i++) {
            final var state = path.get(i);
            final var handler = state.inputs.get(property);
            if ("function".equals(Js.typeof(handler))) {
                calls = new Invocation(state.element, Js.uncheckedCast(handler)).link(calls, capture);
            }
        }
        return calls;
    }

    private static final class DispatchBatch {
        private @Nullable Dispatch first;
        private @Nullable Dispatch last;
        private @Nullable Throwable failure;

        void add(
                final DomHost.Node target,
                final String name,
                final NativeEvent nativeEvent,
                @Nullable final Object relatedTarget,
                @Nullable final Object data,
                @Nullable final Invocation captures,
                @Nullable final Invocation bubbles) {
            if (null == captures && null == bubbles) {
                return;
            }
            final var kind = eventKind(name);
            final var available = kind.pool().pollLast();
            final var event = null == available ? new Dispatch(kind) : available;
            event.initialize(target, name, nativeEvent, relatedTarget, data);
            event.captures = captures;
            event.bubbles = bubbles;
            if (null == last) {
                first = event;
            } else {
                last.next = event;
            }
            last = event;
        }

        void run() {
            for (var event = first; null != event; ) {
                invoke(event, event.captures);
                invoke(event, event.bubbles);
                event = release(event);
            }
            if (failure instanceof RuntimeException exception) {
                throw exception;
            }
            if (failure instanceof Error error) {
                throw error;
            }
        }

        void discard() {
            for (var event = first; null != event; ) {
                event = release(event);
            }
        }

        private static @Nullable Dispatch release(final Dispatch event) {
            final var next = event.next;
            event.next = null;
            event.captures = null;
            event.bubbles = null;
            if (!event.persistent) {
                event.release();
                final var pool = event.kind.pool();
                if (pool.size() < 10) {
                    pool.addLast(event);
                }
            }
            return next;
        }

        private void invoke(final Dispatch event, @Nullable final Invocation tail) {
            if (null != tail) {
                var invocation = tail.next;
                while (!event.stopped) {
                    call(event, invocation);
                    if (Js.isTripleEqual(invocation, tail)) {
                        break;
                    }
                    invocation = invocation.next;
                }
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

    private static @Nullable Invocation collectHandler(
            @Nullable final Invocation tail,
            final DomHost.Element element,
            final String property,
            @Nullable final Object handler,
            final boolean capture) {
        if (!Js.isTruthy(handler)) {
            return tail;
        }
        final var type = Js.typeof(handler);
        if (!"function".equals(type)) {
            throw new IllegalArgumentException(invalidListener(property, type));
        }
        return new Invocation(element, Js.uncheckedCast(handler)).link(tail, capture);
    }

    static final class State {
        private @Nullable Boundary boundary;
        private DomHost.@Nullable Node rootContainer;
        final DomHost.Element element;
        final String tag;
        private @Nullable State logicalParent;
        JsPropertyMap<@Nullable Object> inputs;
        boolean hasHandlers;
        private int documentEvents;
        boolean controlInitialized;
        boolean wasMultiple;
        boolean controlledInput;
        String initialValue = "";

        @Nullable
        Object initialChecked;

        @Nullable
        String lastValue;

        @Nullable
        String trackedProperty;

        State(final DomHost.Element element, final JsPropertyMap<@Nullable Object> inputs, final String tag) {
            this.element = element;
            this.tag = tag;
            this.inputs = inputs;
        }
    }

    private static final class DocumentSubscriptions {
        private @Nullable Boundary first;
        private int firstHosts;
        private @Nullable Map<Boundary, Integer> others;
        private int events;

        void attach(final Boundary boundary) {
            if (boundary == first) {
                firstHosts++;
                return;
            }
            if (null == first && null == others) {
                first = boundary;
                firstHosts = 1;
            } else {
                if (null == others) {
                    others = new HashMap<>();
                }
                final var count = others.get(boundary);
                others.put(boundary, null == count ? 1 : count + 1);
            }
            if (0 != events) {
                boundary.listenToDocument(events);
            }
        }

        void observe(final int observed) {
            final int added = observed & ~events;
            if (0 != added) {
                events |= added;
                if (null != first) {
                    first.listenToDocument(added);
                }
                if (null != others) {
                    others.keySet().forEach(boundary -> boundary.listenToDocument(added));
                }
            }
        }

        boolean detach(final Boundary boundary) {
            if (boundary == first) {
                if (0 == --firstHosts) {
                    first = null;
                }
            } else {
                final var boundaries = Objects.requireNonNull(others);
                final int count = Objects.requireNonNull(boundaries.get(boundary));
                if (1 == count) {
                    boundaries.remove(boundary);
                    if (boundaries.isEmpty()) {
                        others = null;
                    }
                } else {
                    boundaries.put(boundary, count - 1);
                }
            }
            return null == first && null == others;
        }
    }

    private static final class Boundary {
        final DomHost.Node node;
        final JsPropertyMap<Listener> listeners = JsPropertyMap.of();
        final Listener selectionListener;
        final Listener externalMovement;
        private DomHost.@Nullable Element focused;
        private @Nullable Selection lastSelection;
        private boolean mouseDown;
        private final boolean hasTextInput;
        private int documentEvents;
        int hosts;

        Boundary(final DomHost.Node node) {
            this.node = node;
            final var window = DomHost.properties(node.document()).get("defaultView");
            hasTextInput = null != window && DomHost.properties(window).has("TextEvent");
            for (final var name : EVENTS) {
                final var nativeName = nativeName(name);
                final boolean interactive = isInteractive(name);
                final Listener listener = event -> dispatch(this, name, event, interactive);
                listeners.set(nativeName, listener);
                if (isDelegated(nativeName)) {
                    node.addEventListener(nativeName, listener, NON_BUBBLING.contains(nativeName));
                }
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
                    Reconciler.batchedUpdates(() -> {
                        final var batch = new DispatchBatch();
                        collectHandlers(batch, STATES.get(selected), selected, "Select", event, null);
                        batch.run();
                    });
                }
            };
            externalMovement = event -> {
                final var target = Js.<DomHost.@Nullable Node>uncheckedCast(event.target);
                if (0 != paused || null != closestManaged(target)) {
                    return;
                }
                final var to = closestManaged(movementRelated(event, true));
                if (null == to) {
                    return;
                }
                final var state = STATES.get(to);
                if (null != state && state.boundary == this) {
                    Reconciler.batchedUpdates(() -> {
                        final var batch = new DispatchBatch();
                        collectEnterLeave(
                                batch, null, to, "pointerout".equals(event.type) ? "PointerOut" : "MouseOut", event);
                        batch.run();
                    });
                }
            };
        }

        private void listenToDocument(final int requested) {
            final int added = requested & ~documentEvents;
            documentEvents |= added;
            final var document = node.document();
            if (0 != (added & EXTERNAL_MOUSE)) {
                document.addEventListener("mouseout", externalMovement, false);
            }
            if (0 != (added & EXTERNAL_POINTER)) {
                document.addEventListener("pointerout", externalMovement, false);
            }
            if (0 != (added & DOCUMENT_SELECTION)) {
                for (final var event : DOCUMENT_SELECTION_EVENTS) {
                    document.addEventListener(event, selectionListener, false);
                }
            }
        }

        private void clearDocumentListeners() {
            final var document = node.document();
            if (0 != (documentEvents & EXTERNAL_MOUSE)) {
                document.removeEventListener("mouseout", externalMovement, false);
            }
            if (0 != (documentEvents & EXTERNAL_POINTER)) {
                document.removeEventListener("pointerout", externalMovement, false);
            }
            if (0 != (documentEvents & DOCUMENT_SELECTION)) {
                for (final var event : DOCUMENT_SELECTION_EVENTS) {
                    document.removeEventListener(event, selectionListener, false);
                }
            }
        }

        private DomHost.@Nullable Element selectTarget(final String name, final DomHost.Element element) {
            switch (name) {
                case "Focus" -> {
                    final var props = DomHost.properties(element);
                    final var tag = element.localName;
                    if ("textarea".equals(tag)
                            || "input".equals(tag) && TEXT_INPUT_TYPES.contains(DomHost.stringify(props.get("type")))
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
                current = current.logicalParent;
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
        EVENT("eventPhase", "bubbles", "cancelable", "isTrusted"),
        UI(EVENT, "view", "detail"),
        FOCUS(UI, "relatedTarget"),
        MOUSE(
                UI,
                "screenX",
                "screenY",
                "clientX",
                "clientY",
                "pageX",
                "pageY",
                "ctrlKey",
                "shiftKey",
                "altKey",
                "metaKey",
                "button",
                "buttons",
                "relatedTarget",
                "movementX",
                "movementY"),
        DRAG(MOUSE, "dataTransfer"),
        POINTER(
                MOUSE,
                "pointerId",
                "width",
                "height",
                "pressure",
                "tangentialPressure",
                "tiltX",
                "tiltY",
                "twist",
                "pointerType",
                "isPrimary"),
        WHEEL(MOUSE, "deltaX", "deltaY", "deltaZ", "deltaMode"),
        KEYBOARD(UI, "location", "ctrlKey", "shiftKey", "altKey", "metaKey", "repeat", "locale"),
        TOUCH(UI, "touches", "targetTouches", "changedTouches", "altKey", "metaKey", "ctrlKey", "shiftKey"),
        ANIMATION(EVENT, "animationName", "elapsedTime", "pseudoElement"),
        TRANSITION(EVENT, "propertyName", "elapsedTime", "pseudoElement"),
        CLIPBOARD(EVENT, "clipboardData"),
        COMPOSITION(EVENT, "data"),
        INPUT(EVENT, "data");

        private final @Nullable EventKind base;
        private final String[] ownProperties;
        private @Nullable ArrayDeque<Dispatch> pool;
        private String @Nullable [] allProperties;

        EventKind(final String... properties) {
            this(null, properties);
        }

        EventKind(@Nullable final EventKind base, final String... properties) {
            this.base = base;
            ownProperties = properties;
        }

        ArrayDeque<Dispatch> pool() {
            if (null == pool) {
                pool = new ArrayDeque<>();
            }
            return pool;
        }

        String[] properties() {
            if (null == base) {
                return ownProperties;
            }
            if (null == allProperties) {
                final var inherited = base.properties();
                final var count = inherited.length;
                allProperties = Arrays.copyOf(inherited, count + ownProperties.length);
                System.arraycopy(ownProperties, 0, allProperties, count, ownProperties.length);
            }
            return allProperties;
        }
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
            final var normalized =
                    switch (text) {
                        case "Esc" -> "Escape";
                        case "Spacebar" -> " ";
                        case "Left" -> "ArrowLeft";
                        case "Up" -> "ArrowUp";
                        case "Right" -> "ArrowRight";
                        case "Down" -> "ArrowDown";
                        case "Del" -> "Delete";
                        case "Win" -> "OS";
                        case "Menu", "Apps" -> "ContextMenu";
                        case "Scroll" -> "ScrollLock";
                        case "MozPrintableKey" -> "Unidentified";
                        default -> text;
                    };
            if (!"Unidentified".equals(normalized)) {
                return normalized;
            }
        }
        if (keyPress) {
            return 13 == code ? "Enter" : String.valueOf((char) code);
        }
        if (112 <= code && code <= 123) {
            return "F" + (code - 111);
        }
        return switch (code) {
            case 8 -> "Backspace";
            case 9 -> "Tab";
            case 12 -> "Clear";
            case 13 -> "Enter";
            case 16 -> "Shift";
            case 17 -> "Control";
            case 18 -> "Alt";
            case 19 -> "Pause";
            case 20 -> "CapsLock";
            case 27 -> "Escape";
            case 32 -> " ";
            case 33 -> "PageUp";
            case 34 -> "PageDown";
            case 35 -> "End";
            case 36 -> "Home";
            case 37 -> "ArrowLeft";
            case 38 -> "ArrowUp";
            case 39 -> "ArrowRight";
            case 40 -> "ArrowDown";
            case 45 -> "Insert";
            case 46 -> "Delete";
            case 144 -> "NumLock";
            case 145 -> "ScrollLock";
            case 224 -> "Meta";
            default -> "Unidentified";
        };
    }

    private static final class Invocation {
        final DomHost.Element element;
        final ReactEventHandler handler;
        Invocation next = this;

        Invocation(final DomHost.Element element, final ReactEventHandler handler) {
            this.element = element;
            this.handler = handler;
        }

        // The tail points back to the first call. Capture inserts at the front and bubble at the back.
        Invocation link(@Nullable final Invocation tail, final boolean capture) {
            if (null == tail) {
                return this;
            }
            next = tail.next;
            tail.next = this;
            return capture ? tail : this;
        }
    }

    private static final class LegacyMovement {
        private @Nullable Double previous;

        double read(final @Nullable Object position, final boolean moving) {
            final var before = previous;
            final double current = DomHost.number(position);
            previous = current;
            return moving && null != before ? current - before : 0;
        }
    }

    private static final class Dispatch {
        private static final Flag TRUE = () -> true;
        private static final Flag FALSE = () -> false;
        private static final Action NO_OP = () -> {};

        private final EventKind kind;
        private final String[] fields;
        private final JsPropertyMap<@Nullable Object> properties = JsPropertyMap.of();
        private final SyntheticEvent<?> event = Js.uncheckedCast(properties);
        private final Action preventDefault = this::preventDefault;
        private final Action stopPropagation = this::stopPropagation;
        private final Modifier getModifierState = this::getModifierState;
        private final Action persist = this::persist;
        private @Nullable Dispatch next;
        private @Nullable Invocation captures;
        private @Nullable Invocation bubbles;
        private boolean stopped;
        private boolean persistent;
        private boolean released;

        Dispatch(final EventKind kind) {
            this.kind = kind;
            fields = kind.properties();
        }

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
            for (final var property : fields) {
                properties.set(property, source.get(property));
            }
            final var timeStamp = source.get("timeStamp");
            properties.set("timeStamp", Js.isTruthy(timeStamp) ? timeStamp : now());
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
            switch (kind) {
                case KEYBOARD -> {
                    final boolean keyPress = "KeyPress".equals(name);
                    final int code = keyPress ? charCode(source) : eventNumber(source, "keyCode");
                    properties.set("key", eventKey(source, keyPress, code));
                    properties.set("charCode", keyPress ? code : 0);
                    if (keyPress) {
                        properties.set("keyCode", 0);
                        properties.set("which", code);
                    } else {
                        properties.set("keyCode", source.get("keyCode"));
                        properties.set("which", source.get("keyCode"));
                    }
                }
                case CLIPBOARD -> {
                    if (!source.has("clipboardData")) {
                        properties.set("clipboardData", Js.global().get("clipboardData"));
                    }
                }
                case MOUSE, DRAG, POINTER, WHEEL -> {
                    final boolean moving = "mousemove".equals(nativeEvent.type);
                    if (!source.has("movementX")) {
                        properties.set("movementX", MOVEMENT_X.read(source.get("screenX"), moving));
                    }
                    if (!source.has("movementY")) {
                        properties.set("movementY", MOVEMENT_Y.read(source.get("screenY"), moving));
                    }
                    final var related = source.get("relatedTarget");
                    if (!Js.isTruthy(related)) {
                        final var from = source.get("fromElement");
                        properties.set(
                                "relatedTarget",
                                Js.isTripleEqual(from, source.get("srcElement")) ? source.get("toElement") : from);
                    }
                    if (EventKind.WHEEL == kind) {
                        normalizeWheel(source);
                    }
                }
                default -> {}
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
            final var defaultPrevented = source.get("defaultPrevented");
            properties.set("defaultPrevented", defaultPrevented);
            final boolean prevented = null != defaultPrevented
                    ? Js.isTruthy(defaultPrevented)
                    : Js.isTripleEqual(false, source.get("returnValue"));
            properties.set("preventDefault", preventDefault);
            properties.set("stopPropagation", stopPropagation);
            properties.set("isPropagationStopped", FALSE);
            properties.set("isDefaultPrevented", prevented ? TRUE : FALSE);
            properties.set("getModifierState", getModifierState);
            properties.set("persist", persist);
            properties.set("isPersistent", FALSE);
        }

        private @Nullable NativeEvent nativeEvent() {
            return Js.uncheckedCast(properties.get("nativeEvent"));
        }

        private void preventDefault() {
            properties.set("defaultPrevented", true);
            final var nativeEvent = nativeEvent();
            if (null != nativeEvent) {
                final var source = DomHost.properties(nativeEvent);
                if (Js.isTruthy(source.get("preventDefault"))) {
                    nativeEvent.preventDefault();
                } else {
                    source.set("returnValue", false);
                }
                properties.set("isDefaultPrevented", TRUE);
            }
        }

        private void stopPropagation() {
            final var nativeEvent = nativeEvent();
            if (null != nativeEvent) {
                final var source = DomHost.properties(nativeEvent);
                if (Js.isTruthy(source.get("stopPropagation"))) {
                    nativeEvent.stopPropagation();
                } else {
                    source.set("cancelBubble", true);
                }
                stopped = true;
                properties.set("isPropagationStopped", TRUE);
            }
        }

        private boolean getModifierState(final String key) {
            final var nativeEvent = nativeEvent();
            if (null == nativeEvent) {
                return false;
            }
            final var source = DomHost.properties(nativeEvent);
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
        }

        private void persist() {
            persistent = true;
            properties.set("isPersistent", TRUE);
        }

        private void normalizeWheel(final JsPropertyMap<@Nullable Object> source) {
            if (!source.has("deltaX")) {
                properties.set("deltaX", source.has("wheelDeltaX") ? -Js.asDouble(source.get("wheelDeltaX")) : 0);
            }
            if (!source.has("deltaY")) {
                properties.set(
                        "deltaY",
                        source.has("wheelDeltaY")
                                ? -Js.asDouble(source.get("wheelDeltaY"))
                                : source.has("wheelDelta") ? -Js.asDouble(source.get("wheelDelta")) : 0);
            }
        }

        void release() {
            for (final var property : fields) {
                releaseProperty(property, null);
            }
            if (EventKind.KEYBOARD == kind) {
                releaseProperty("key", null);
                releaseProperty("charCode", null);
                releaseProperty("keyCode", null);
                releaseProperty("which", null);
            }
            releaseProperty("timeStamp", null);
            releaseProperty("type", null);
            releaseProperty("target", null);
            releaseProperty("currentTarget", null);
            releaseProperty("nativeEvent", null);
            releaseProperty("defaultPrevented", null);
            releaseProperty("preventDefault", NO_OP);
            releaseProperty("stopPropagation", NO_OP);
            releaseProperty("isDefaultPrevented", FALSE);
            releaseProperty("isPropagationStopped", FALSE);
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
            properties.set("currentTarget", call.element);
            try {
                call.handler.onReactEvent(event);
            } finally {
                properties.set("currentTarget", null);
            }
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Date.now")
    private static native double now();

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

        native void preventDefault();

        native void stopPropagation();

        native boolean getModifierState(String key);
    }
}
