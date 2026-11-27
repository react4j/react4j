package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.events.ReactEventHandler;
import react4j.dom.events.SyntheticEvent;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.RefConsumer;
import react4j.dom.proptypes.html.SelectProps;
import react4j.dom.proptypes.html.TextAreaProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.webtesting.Browser;

@NullMarked
public final class EventsBrowserTest {
    private EventsBrowserTest() {}

    @JsMethod
    public static void runPayloads() {
        final var root = Browser.append("div");
        try {
            checkPayload(
                    root,
                    "onClick",
                    "click",
                    "button buttons clientX clientY pageX pageY screenX screenY altKey ctrlKey metaKey shiftKey");
            checkPayload(root, "onKeyDown", "keydown", "key location repeat locale altKey ctrlKey metaKey shiftKey");
            checkPayload(root, "onFocus", "focus", "relatedTarget");
            checkPayload(root, "onCopy", "copy", "clipboardData");
            checkPayload(root, "onCompositionEnd", "compositionend", "data");
            checkPayload(root, "onDragStart", "dragstart", "dataTransfer buttons screenX screenY detail view");
            checkPayload(root, "onWheel", "wheel", "deltaX deltaY deltaZ deltaMode");
            checkPayload(
                    root,
                    "onPointerDown",
                    "pointerdown",
                    "pointerId pointerType pressure tangentialPressure width height tiltX tiltY twist isPrimary");
            checkPayload(root, "onTouchStart", "touchstart", "touches targetTouches changedTouches altKey view detail");
            checkPayload(root, "onAnimationEnd", "animationend", "animationName elapsedTime pseudoElement");
            checkPayload(root, "onTransitionEnd", "transitionend", "propertyName elapsedTime pseudoElement");
            checkPayload(root, "onScroll", "scroll", "detail view");
            final var legacyWheel = capturePayload(root, "onWheel", "legacyWheel");
            check(Js.isTripleEqual(-12, Browser.property(legacyWheel, "deltaX")), "Legacy wheel X direction");
            check(Js.isTripleEqual(34, Browser.property(legacyWheel, "deltaY")), "Legacy wheel Y direction");
            final var legacyVerticalWheel = capturePayload(root, "onWheel", "legacyVerticalWheel");
            check(
                    Js.isTripleEqual(0, Browser.property(legacyVerticalWheel, "deltaX")),
                    "Missing wheel X defaults to zero");
            check(Js.isTripleEqual(-56, Browser.property(legacyVerticalWheel, "deltaY")), "Scalar legacy wheel delta");
            final var legacyMouse = capturePayload(root, "onMouseMove", "legacyMouse");
            check(
                    legacyMouse.getTarget() == Browser.property(legacyMouse, "relatedTarget"),
                    "Legacy related mouse target");
            check(0 < legacyMouse.getTimeStamp(), "Zero native timestamp falls back to the current time");
            final var legacyCopy = capturePayload(root, "onCopy", "legacyCopy");
            check(
                    null != Browser.property(legacyCopy, "clipboardData"),
                    "Missing native clipboard data falls back to the window");
            final var canceled = capturePayload(root, "onClick", "legacyCanceled");
            check(canceled.isDefaultPrevented(), "Legacy canceled event uses the return value");
            final var click = capturePayload(root, "onClick", "onClick");
            final var before = Js.<FlagProbe>uncheckedCast(Browser.property(click, "isDefaultPrevented"));
            click.preventDefault();
            check(click.isDefaultPrevented() && !before.read(), "Saved event flags describe their original state");

            final SyntheticEvent<?>[] first = new SyntheticEvent<?>[1];
            final Object[] prevent = new Object[1];
            final ReactEventHandler save = event -> {
                first[0] = event;
                prevent[0] = Objects.requireNonNull(Browser.property(event, "preventDefault"));
            };
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", save)), root);
            final var button = Browser.query(root, "button");
            final var original = dispatch(button, "onClick");
            releasedWarnings(() -> callSaved(Objects.requireNonNull(prevent[0]), Objects.requireNonNull(first[0])));
            check(
                    Boolean.FALSE.equals(Browser.property(original, "defaultPrevented")),
                    "Released action leaves native event alone");

            final ReactEventHandler use = event -> {
                check(event == first[0], "The next click reuses the synthetic event");
                callSaved(Objects.requireNonNull(prevent[0]), event);
            };
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", use)), root);
            final var reused = dispatch(button, "onClick");
            check(
                    Boolean.TRUE.equals(Browser.property(reused, "defaultPrevented")),
                    "Saved action uses the active native event");
            check(
                    Boolean.FALSE.equals(Browser.property(original, "defaultPrevented")),
                    "Reused action leaves prior native event alone");
            final var noKeyCode = capturePayload(root, "onKeyDown", "legacyMissingKeyCode");
            check(Js.isTripleEqual(Js.undefined(), Browser.property(noKeyCode, "keyCode")), "Missing native key code");
            check(Js.isTripleEqual(Js.undefined(), Browser.property(noKeyCode, "which")), "Missing legacy which");
            capturePayload(root, "onClick", "legacyEventMethods");
            final var movement = new StringBuilder();
            final ReactEventHandler measure = event -> movement.append(Browser.property(event, "movementX"))
                    .append('/')
                    .append(Browser.property(event, "movementY"))
                    .append(' ');
            ReactDOM.render(host("button", JsPropertyMap.of("onMouseMove", measure, "onMouseDown", measure)), root);
            final var mouse = Browser.query(root, "button");
            legacyMovement(mouse, "mousedown", 2, 3);
            legacyMovement(mouse, "mousemove", 8, 11);
            legacyMovement(mouse, "mousedown", 25, 65);
            check("0/0 6/8 0/0 ".contentEquals(movement), "Mouse movement falls back to screen coordinate deltas");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void checkPayload(Browser.Element root, String name, String type, String properties) {
        final var event = capturePayload(root, name, name);
        check(type.equals(event.getType()), name + " event type");
        check(Js.isTripleEqual(event.getTarget(), Browser.query(root, "button")), name + " event target");
        final var source = event.getNativeEvent();
        check(0 < event.getEventPhase(), name + " captures the native dispatch phase");
        for (final var property : ("bubbles cancelable isTrusted timeStamp " + properties).split(" ")) {
            check(
                    Js.isTripleEqual(Browser.property(event, property), Browser.property(source, property)),
                    name + " preserves native " + property);
        }
    }

    private static SyntheticEvent<?> capturePayload(Browser.Element root, String name, String fixture) {
        final SyntheticEvent<?>[] received = new SyntheticEvent<?>[1];
        final ReactEventHandler handler = event -> {
            check(null == received[0], name + " should be dispatched once");
            check(Js.isTripleEqual(event.getCurrentTarget(), Browser.query(root, "button")), name + " current target");
            if ("legacyEventMethods".equals(fixture)) {
                event.preventDefault();
                check(event.isDefaultPrevented(), "Legacy preventDefault marks the synthetic event");
                check(
                        Js.isTripleEqual(false, Browser.property(event.getNativeEvent(), "returnValue")),
                        "Legacy preventDefault marks the native event");
                event.stopPropagation();
                check(event.isPropagationStopped(), "Legacy stopPropagation marks the synthetic event");
                check(
                        Js.isTripleEqual(true, Browser.property(event.getNativeEvent(), "cancelBubble")),
                        "Legacy stopPropagation marks the native event");
            }
            event.persist();
            received[0] = event;
        };
        ReactDOM.render(host("button", JsPropertyMap.of(name, handler)), root);
        final Object source = dispatch(Browser.query(root, "button"), fixture);
        final var event = Objects.requireNonNull(received[0], name + " was not dispatched");
        check(event.getNativeEvent() == source, name + " native event identity");
        return event;
    }

    @JsMethod
    public static void run() {
        var root = Browser.append("div");
        try {
            for (var name : new String[] {
                "onClick",
                "onKeyDown",
                "onFocus",
                "onCopy",
                "onCompositionEnd",
                "onDragStart",
                "onWheel",
                "onTouchStart",
                "onScroll"
            }) {
                int[] received = {0};
                final ReactEventHandler handler = event -> {
                    received[0]++;
                    for (var overlay : eventOverlays(name)) {
                        getters(overlay, event);
                    }
                    check(!event.isDefaultPrevented(), "Initial default state");
                    event.preventDefault();
                    check(event.isDefaultPrevented(), "Default prevention");
                    event.stopPropagation();
                    check(event.isPropagationStopped(), "Propagation stop");
                    event.persist();
                };
                final var props = JsPropertyMap.<@Nullable Object>of();
                props.set(name, handler);
                props.set("children", ReactNode.of("events"));
                ReactDOM.render(ReactElement.createHostElement("button", null, null, props), root);
                dispatch(Browser.query(root, "button"), name);
                check(1 == received[0], name + " was not dispatched");
            }
            inspectWindow(EventsBrowserTest::getters);
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runBehavior() {
        final var root = Browser.append("div");
        final var portal = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler capture = event -> {
                check(
                        "SECTION".equals(Browser.property(event.getCurrentTarget(), "tagName")),
                        "Capture current target");
                check("BUTTON".equals(Browser.property(event.getTarget(), "tagName")), "Portal event target");
                trace.append("capture ");
                ReactDOM.render(ReactNode.of("after click"), root);
            };
            final ReactEventHandler bubble = event -> {
                trace.append("bubble");
                check(Browser.exists(root, "section"), "Render waits for the event batch");
            };
            final ReactEventHandler child = event -> trace.append("child ");
            final var button = ReactElement.createHostElement("button", null, null, JsPropertyMap.of("onClick", child));
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "section",
                            null,
                            null,
                            JsPropertyMap.of(
                                    "onClickCapture", capture,
                                    "onClick", bubble,
                                    "children", ReactDOM.createPortal(button, portal))),
                    root);
            final var portalButton = Browser.query(portal, "button");
            check(
                    "function".equals(Js.typeof(Browser.property(portalButton, "onclick"))),
                    "A click handler traps native taps");
            check(
                    "function".equals(Js.typeof(Browser.property(portal, "onclick"))),
                    "A portal container traps native taps");
            check(null == Browser.property(root, "onclick"), "The React root has no native click trap");
            portalButton.click();
            check("capture child bubble".contentEquals(trace), "Portal capture and bubble order");
            check("after click".equals(root.getTextContent()), "Event batch commits after dispatch");

            final ReactEventHandler change = event -> {
                trace.append(Browser.property(event.getTarget(), "value"));
                check("change".equals(event.getType()), "Synthetic change type");
            };
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input", null, null, JsPropertyMap.of("onChange", change, "value", "committed")),
                    root);
            final var input = Browser.query(root, "input");
            trace.setLength(0);
            Browser.setValue(input, "edited");
            check("edited".contentEquals(trace), "Change handler reads the edited value");
            check("committed".equals(input.getValue()), "Controlled value is restored after the handler");

            radioGroups(root);

            final ReactEventHandler outerCapture = event -> {
                check("BUTTON".equals(Browser.property(event.getTarget(), "tagName")), "Nested root event target");
                trace.append("outer capture ");
            };
            final ReactEventHandler outerBubble = event -> {
                check(
                        "SECTION".equals(Browser.property(event.getCurrentTarget(), "tagName")),
                        "Nested root current target");
                trace.append("outer bubble");
            };
            ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onClickCapture", outerCapture,
                                    "onClick", outerBubble,
                                    "children", host("div", JsPropertyMap.of()))),
                    root);
            final var nestedRoot = Browser.query(root, "div");
            try {
                final ReactEventHandler innerCapture = event -> trace.append("inner capture ");
                final ReactEventHandler innerBubble = event -> {
                    trace.append("inner bubble ");
                    event.stopPropagation();
                    ReactDOM.render(host("span", JsPropertyMap.of("children", "clicked")), nestedRoot);
                };
                ReactDOM.render(
                        host("button", JsPropertyMap.of("onClickCapture", innerCapture, "onClick", innerBubble)),
                        nestedRoot);
                trace.setLength(0);
                final var disappearing = Browser.query(nestedRoot, "button");
                disappearing.click();
                check(
                        "inner capture inner bubble outer capture outer bubble".contentEquals(trace),
                        "A nested root dispatches before its enclosing root");
                check(!nestedRoot.contains(disappearing), "The original event target leaves the tree after dispatch");
                check(
                        "clicked".equals(Browser.query(nestedRoot, "span").getTextContent()),
                        "The click commits a replacement host");
            } finally {
                ReactDOM.unmountComponentAtNode(nestedRoot);
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            portal.remove();
        }
    }

    @JsMethod
    public static void runEmitterPropagation() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler record = event -> {
                final var id = Browser.property(event.getCurrentTarget(), "id");
                check(
                        Js.isTripleEqual(event.getCurrentTarget(), Browser.query(root, "#" + id)),
                        "A delegated currentTarget is the exact registered DOM element");
                appendEmitterTarget(trace, event);
            };
            emitterTree(root, record, record, record);
            final var child = Browser.query(root, "#emitter-child");
            child.click();
            equal("emitter-child emitter-parent emitter-grandparent ", trace.toString());

            final ReactEventHandler replacement = event -> trace.append("updated ");
            emitterTree(root, record, record, replacement);
            trace.setLength(0);
            child.click();
            equal("emitter-child emitter-parent updated ", trace.toString());

            final ReactEventHandler throwing = event -> {
                appendEmitterTarget(trace, event);
                throw new IllegalStateException("Emitter handler interrupted");
            };
            emitterTree(root, record, throwing, record);
            trace.setLength(0);
            check(clickError(child).contains("Emitter handler interrupted"), "Dispatch surfaces the throwing listener");
            equal("emitter-child emitter-parent emitter-grandparent ", trace.toString());

            final ReactEventHandler stop = event -> {
                appendEmitterTarget(trace, event);
                event.stopPropagation();
            };
            emitterTree(root, record, stop, record);
            trace.setLength(0);
            child.click();
            equal("emitter-child emitter-parent ", trace.toString());

            final ReactEventHandler overrideStop = event -> {
                appendEmitterTarget(trace, event);
                Js.<JsPropertyMap<Object>>uncheckedCast(event).set("isPropagationStopped", (FlagProbe) () -> true);
            };
            emitterTree(root, record, overrideStop, record);
            trace.setLength(0);
            child.click();
            equal("emitter-child emitter-parent ", trace.toString());

            emitterTree(root, stop, record, record);
            trace.setLength(0);
            child.click();
            equal("emitter-child ", trace.toString());

            final EmitterBooleanHandler returnsFalse = event -> {
                appendEmitterTarget(trace, event);
                return false;
            };
            emitterTree(root, Js.uncheckedCast(returnsFalse), record, record);
            trace.setLength(0);
            child.click();
            equal("emitter-child emitter-parent emitter-grandparent ", trace.toString());

            final int[] parentCalls = {0};
            final ReactEventHandler parent = event -> parentCalls[0]++;
            emitterTree(root, event -> emitterTree(root, null, null, null), parent, null);
            child.click();
            check(1 == parentCalls[0], "The ongoing click includes a listener removed while bubbling");
            emitterTree(root, event -> emitterTree(root, null, parent, null), null, null);
            child.click();
            check(1 == parentCalls[0], "The ongoing click excludes a newly inserted listener");
            child.click();
            check(2 == parentCalls[0], "The next click sees the newly inserted listener");

            final int[] entered = {0};
            final ReactEventHandler enter = event -> {
                check(Js.isTripleEqual(child, event.getCurrentTarget()), "Mouse enter targets the registered child");
                entered[0]++;
            };
            ReactDOM.render(
                    emitterNode(
                            "emitter-grandparent",
                            null,
                            emitterNode(
                                    "emitter-parent",
                                    null,
                                    host("div", JsPropertyMap.of("id", "emitter-child", "onMouseEnter", enter)))),
                    root);
            movement(child, "mouseover", null);
            check(1 == entered[0], "One native mouseover extracts one current-target mouse enter");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runEmitterSubscriptions() {
        final var root = Browser.append("div");
        try {
            final int[] clicks = {0};
            final int[] oldChanges = {0};
            final int[] newChanges = {0};
            final ReactEventHandler click = event -> clicks[0]++;
            final ReactEventHandler oldChange = event -> oldChanges[0]++;
            final ReactEventHandler newChange = event -> {
                equal("change", event.getType());
                newChanges[0]++;
            };
            final var registered = observeRootListeners(root, () -> {
                emitterTree(root, click, null, null);
                emitterTree(root, click, click, null);
                Browser.query(root, "#emitter-child").click();
                check(2 == clicks[0], "One native click reaches each registered listener once");

                ReactDOM.render(emitterControls(oldChange), root);
                ReactDOM.render(emitterControls(newChange), root);
                Browser.setValue(Browser.query(root, "#emitter-text"), "entered");
                check(1 == newChanges[0], "A native input event delivers one synthetic text change");
                Browser.query(root, "#emitter-checkbox").click();
                check(2 == newChanges[0], "A native click delivers one synthetic checkbox change");
                final var select = Browser.query(root, "#emitter-select");
                Objects.requireNonNull(Js.asPropertyMap(select)).set("value", "after");
                nativeInputEvent(select, "change");
                check(3 == newChanges[0], "A native change event delivers one synthetic select change");
                check(0 == oldChanges[0], "Renewal replaces the prior change handlers");
            });
            final var registrations = Arrays.asList(registered.split(" "));
            for (final var name : new String[] {"click", "input", "change"}) {
                check(
                        1 == Collections.frequency(registrations, name),
                        "Handler renewal attaches one native " + name + " listener");
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static ReactElement emitterControls(ReactEventHandler change) {
        final var text = host("input", JsPropertyMap.of("id", "emitter-text", "onChange", change));
        text.setKey("text");
        final var checkbox =
                host("input", JsPropertyMap.of("id", "emitter-checkbox", "type", "checkbox", "onChange", change));
        checkbox.setKey("checkbox");
        final var before = host("option", JsPropertyMap.of("value", "before"));
        before.setKey("before");
        final var after = host("option", JsPropertyMap.of("value", "after"));
        after.setKey("after");
        final var select = host(
                "select",
                JsPropertyMap.of("id", "emitter-select", "children", ReactNode.of(before, after), "onChange", change));
        select.setKey("select");
        return host("div", JsPropertyMap.of("children", ReactNode.of(text, checkbox, select)));
    }

    @JsMethod
    private static native String observeRootListeners(Browser.Element root, Probe probe);

    @JsMethod
    public static void runSyntheticLifecycle(boolean production) {
        final var root = Browser.append("div");
        try {
            final int[] calls = {0};
            final SyntheticEvent<?>[] saved = new SyntheticEvent<?>[1];
            final ReactEventHandler preserve = event -> {
                check(!event.isDefaultPrevented(), "A fresh click starts uncanceled");
                event.preventDefault();
                check(
                        event.isDefaultPrevented() && Boolean.TRUE.equals(Browser.property(event, "defaultPrevented")),
                        "Preventing default sets both synthetic indicators");
                check(!event.isPropagationStopped(), "A fresh click can propagate");
                event.stopPropagation();
                check(event.isPropagationStopped(), "Stopping propagation sets the synthetic flag");
                persistEvent(event);
                saved[0] = event;
                calls[0]++;
            };
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", preserve)), root);
            final var button = Browser.query(root, "button");
            button.click();
            final var kept = Objects.requireNonNull(saved[0]);
            equal("click", kept.getType());
            check(kept.isBubbles() && kept.isCancelable(), "Persisted clicks keep their native flags");
            check(1 == calls[0], "One click invokes the persistent handler once");

            calls[0] = 0;
            final ReactEventHandler canceled = event -> {
                check(event.isDefaultPrevented(), "Native cancellation is visible in the synthetic event");
                calls[0]++;
            };
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", canceled)), root);
            dispatch(button, "preCanceled");
            dispatch(button, "legacyCanceled");
            check(2 == calls[0], "Both modern and legacy native cancellation dispatch");

            calls[0] = 0;
            final ReactEventHandler pooled = event -> {
                saved[0] = event;
                calls[0]++;
            };
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", pooled)), root);
            button.click();
            final var released = Objects.requireNonNull(saved[0]);
            check(1 == calls[0], "A released click was delivered exactly once");
            releasedDiagnostic(
                    "type",
                    production,
                    () -> check(Js.isTripleEqual(null, released.getType()), "Released type is null"));
            releasedDiagnostic(
                    "nativeEvent",
                    production,
                    () -> check(Js.isTripleEqual(null, released.getNativeEvent()), "Released native event is null"));
            releasedDiagnostic(
                    "target",
                    production,
                    () -> check(Js.isTripleEqual(null, released.getTarget()), "Released target is null"));
            releasedDiagnostic(
                    "type",
                    production,
                    () -> Js.<JsPropertyMap<Object>>uncheckedCast(released).set("type", "MouseEvent"));
            releasedDiagnostic("preventDefault", production, released::preventDefault);
            releasedDiagnostic("stopPropagation", production, released::stopPropagation);
            releasedDiagnostic(
                    "isPropagationStopped",
                    production,
                    () -> check(!released.isPropagationStopped(), "Released propagation state resets"));
            releasedDiagnostic(
                    "isDefaultPrevented",
                    production,
                    () -> check(!released.isDefaultPrevented(), "Released default state resets"));
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void releasedDiagnostic(String member, boolean production, Probe action) {
        final var warning = captureWarnings(action);
        if (production) {
            equal("", warning);
        } else {
            check(
                    warning.contains("`" + member + "`") && warning.contains("released/nullified synthetic event"),
                    "Expected a warning for released event member " + member + ": " + warning);
        }
    }

    @JsMethod
    public static void runSyntheticSpecializations() {
        final var root = Browser.append("div");
        try {
            final var wheels = new ArrayList<SyntheticEvent<?>>();
            final ReactEventHandler wheel = event -> {
                check(!event.isDefaultPrevented(), "Wheel begins uncanceled");
                event.preventDefault();
                check(event.isDefaultPrevented(), "Wheel supports preventing default");
                persistEvent(event);
                wheels.add(event);
            };
            ReactDOM.render(host("div", JsPropertyMap.of("onWheel", wheel)), root);
            final var node = Browser.query(root, "div");
            dispatch(node, "mouseWheelButton");
            check(1 == wheels.size(), "One native wheel with mouse fields invokes the handler once");
            equal("wheel", wheels.get(0).getType());
            check(Js.isTripleEqual(1, Browser.property(wheels.get(0), "button")), "Wheel inherits the mouse button");
            wheels.clear();
            dispatch(node, "normalizedWheel");
            dispatch(node, "normalizedLegacyWheel");
            check(2 == wheels.size(), "Modern and legacy wheel events are delivered");
            for (final var event : wheels) {
                check(Js.isTripleEqual(10, Browser.property(event, "deltaX")), "Wheel preserves normalized deltaX");
                check(Js.isTripleEqual(-50, Browser.property(event, "deltaY")), "Wheel preserves normalized deltaY");
            }

            final var clipboardEvents = new ArrayList<SyntheticEvent<?>>();
            final ReactEventHandler clipboard = event -> {
                check(!event.isDefaultPrevented(), "Clipboard begins uncanceled");
                event.preventDefault();
                check(event.isDefaultPrevented(), "Clipboard supports preventing default");
                check(!event.isPropagationStopped(), "Clipboard begins with propagation enabled");
                event.stopPropagation();
                check(event.isPropagationStopped(), "Clipboard supports stopping propagation");
                persistEvent(event);
                clipboardEvents.add(event);
            };
            ReactDOM.render(
                    host("div", JsPropertyMap.of("onCopy", clipboard, "onCut", clipboard, "onPaste", clipboard)), root);
            for (final var type : new String[] {"Copy", "Cut", "Paste"}) {
                final var nativeEvent = dispatch(node, "on" + type);
                final var received = clipboardEvents.get(clipboardEvents.size() - 1);
                final var data = Browser.property(nativeEvent, "clipboardData");
                check(
                        null != data && data == Browser.property(received, "clipboardData"),
                        "Clipboard event exposes the native DataTransfer instance");
            }
            check(3 == clipboardEvents.size(), "Copy, cut and paste each dispatch one event");
            equal("copy", clipboardEvents.get(0).getType());
            equal("cut", clipboardEvents.get(1).getType());
            equal("paste", clipboardEvents.get(2).getType());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void persistEvent(SyntheticEvent<?> event) {
        check(
                !Js.<FlagProbe>uncheckedCast(Browser.property(event, "isPersistent"))
                        .read(),
                "A fresh synthetic event is pooled");
        event.persist();
        check(
                Js.<FlagProbe>uncheckedCast(Browser.property(event, "isPersistent"))
                        .read(),
                "Persist keeps the event after dispatch");
    }

    private static void emitterTree(
            Browser.Element root,
            @Nullable ReactEventHandler child,
            @Nullable ReactEventHandler parent,
            @Nullable ReactEventHandler grandparent) {
        ReactDOM.render(
                emitterNode(
                        "emitter-grandparent",
                        grandparent,
                        emitterNode("emitter-parent", parent, emitterNode("emitter-child", child, null))),
                root);
    }

    private static ReactElement emitterNode(
            String id, @Nullable ReactEventHandler listener, @Nullable ReactNode child) {
        final var props = JsPropertyMap.<@Nullable Object>of("id", id, "children", child);
        if (null != listener) {
            props.set("onClick", listener);
        }
        return host("div", props);
    }

    private static void appendEmitterTarget(StringBuilder trace, SyntheticEvent<?> event) {
        trace.append(Browser.property(event.getCurrentTarget(), "id")).append(' ');
    }

    @JsFunction
    private interface EmitterBooleanHandler {
        boolean handle(SyntheticEvent<?> event);
    }

    private static void radioGroups(final Browser.Element root) {
        final ReactEventHandler ignore = event -> {};
        final var otherForm = host("form", JsPropertyMap.of("children", radio("other", "fruit", true)));
        ReactDOM.render(
                ReactElement.createFragment(
                        null, radio("first", "fruit", true), radio("second", "fruit", false), otherForm),
                root);
        final var first = Browser.query(root, "#first");
        final var second = Browser.query(root, "#second");
        final var other = Browser.query(root, "#other");
        second.click();
        check(
                Boolean.TRUE.equals(Browser.property(first, "checked")),
                "Controlled radio is restored after a peer click");
        check(Boolean.FALSE.equals(Browser.property(second, "checked")), "Controlled radio peer is reverted");
        check(Boolean.TRUE.equals(Browser.property(other, "checked")), "A different form retains its checked radio");
        attribute(first, "checked", "");
        attribute(second, "checked", null);

        final ReactEventHandler rename = event -> ReactDOM.render(
                ReactElement.createFragment(
                        null,
                        host("button", JsPropertyMap.of("onClick", ignore)),
                        radio("first", "new-name", true),
                        radio("second", "new-name", false)),
                root);
        ReactDOM.render(
                ReactElement.createFragment(
                        null,
                        host("button", JsPropertyMap.of("onClick", rename)),
                        radio("first", "old-name", false),
                        radio("second", "old-name", true)),
                root);
        final var renamedFirst = Browser.query(root, "#first");
        final var renamedSecond = Browser.query(root, "#second");
        check(Boolean.FALSE.equals(Browser.property(renamedFirst, "checked")), "The first radio starts unchecked");
        Browser.query(root, "button").click();
        attribute(renamedFirst, "name", "new-name");
        attribute(renamedSecond, "name", "new-name");
        check(
                Boolean.TRUE.equals(Browser.property(renamedFirst, "checked")),
                "Renaming the group retains the new selection");
        check(Boolean.FALSE.equals(Browser.property(renamedSecond, "checked")), "The prior selection is cleared");

        final var detached = Browser.append("div");
        detached.remove();
        try {
            ReactDOM.render(radio("detached-radio", "fruit", true), detached);
            final var detachedRadio = Browser.query(detached, "input");
            final var source = JsPropertyMap.<@Nullable Object>of(
                    "type", "radio", "name", "fruit", "checked", Browser.bool(false));
            final ReactEventHandler attach =
                    event -> ReactDOM.render(host("input", source), root, () -> appendExisting(root, detached));
            source.set("onChange", attach);
            ReactDOM.render(host("input", source), root);
            final var edited = Browser.query(root, "input");
            setCheckedUntracked(edited, true);
            setCheckedUntracked(detachedRadio, false);
            nativeInputEvent(edited, "click");
            check(root.contains(detached), "The change commit joins two radio groups");
            check(Boolean.FALSE.equals(Browser.property(edited, "checked")), "The edited radio is restored");
            check(
                    Boolean.TRUE.equals(Browser.property(detachedRadio, "checked")),
                    "A radio that joined during the update is restored");
        } finally {
            ReactDOM.unmountComponentAtNode(detached);
            detached.remove();
        }
    }

    @JsMethod
    public static void runStockChangeEvents() {
        final var root = Browser.append("div");
        try {
            final int[] calls = {0};
            final ReactEventHandler onChange = event -> {
                equal("change", event.getType());
                calls[0]++;
            };
            final var text =
                    host("input", JsPropertyMap.of("type", "text", "defaultValue", "foo", "onChange", onChange));
            ReactDOM.render(text, root);
            var input = Browser.query(root, "input");
            nativeInputEvent(input, "input");
            nativeInputEvent(input, "change");
            check(0 == calls[0], "Initial text value is tracked");
            writeValue(input, "bar");
            nativeInputEvent(input, "input");
            equal("bar", input.getValue());
            check(0 == calls[0], "Programmatic text changes do not dispatch");
            Browser.setValue(input, "foo");
            equal("foo", input.getValue());
            check(1 == calls[0], "Untracked text changes dispatch");
            nativeInputEvent(input, "input");
            check(1 == calls[0], "Repeated input without a value change is quiet");

            ReactDOM.unmountComponentAtNode(root);
            calls[0] = 0;
            ReactDOM.render(
                    host("input", JsPropertyMap.of("type", "text", "defaultValue", "42", "onChange", onChange)), root);
            input = Browser.query(root, "input");
            Objects.requireNonNull(Js.asPropertyMap(input)).set("value", Browser.number(42));
            nativeInputEvent(input, "input");
            equal("42", input.getValue());
            check(0 == calls[0], "Equivalent string and number values are deduplicated");

            for (final boolean initialChecked : new boolean[] {true, false}) {
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(
                        host(
                                "input",
                                JsPropertyMap.of(
                                        "type", "checkbox", "defaultChecked", initialChecked, "onChange", onChange)),
                        root);
                input = Browser.query(root, "input");
                setCheckedUntracked(input, !initialChecked);
                input.click();
                check(0 == calls[0], "Initial checkbox state is tracked");
            }

            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(host("input", JsPropertyMap.of("type", "checkbox", "onChange", onChange)), root);
            input = Browser.query(root, "input");
            check(Boolean.FALSE.equals(Browser.property(input, "checked")), "Checkbox starts unchecked");
            input.click();
            check(Boolean.TRUE.equals(Browser.property(input, "checked")), "First click checks the box");
            check(1 == calls[0], "First click emits change");
            input.click();
            check(Boolean.FALSE.equals(Browser.property(input, "checked")), "Second click unchecks the box");
            check(2 == calls[0], "Second click emits change");

            calls[0] = 0;
            Objects.requireNonNull(Js.asPropertyMap(input)).set("checked", true);
            setCheckedUntracked(input, false);
            input.click();
            check(Boolean.TRUE.equals(Browser.property(input, "checked")), "The click checks the box again");
            check(0 == calls[0], "Programmatic checked state is tracked");
            input.click();
            check(1 == calls[0], "Next real click changes the tracked state");

            ReactDOM.unmountComponentAtNode(root);
            check(!root.contains(input), "Unmount removes the tracked node");
            for (final var type : new String[] {"text", "number", "range"}) {
                for (final var initialEvent : new String[] {"input", "change"}) {
                    for (final var editedEvent : new String[] {"input", "change"}) {
                        calls[0] = 0;
                        ReactDOM.render(host("input", JsPropertyMap.of("type", type, "onChange", onChange)), root);
                        input = Browser.query(root, "input");
                        nativeInputEvent(input, initialEvent);
                        setValueUntracked(input, "42");
                        nativeInputEvent(input, editedEvent);
                        nativeInputEvent(input, initialEvent);
                        check(
                                1 == calls[0],
                                type + " change is deduplicated across " + editedEvent + " and " + initialEvent);
                        ReactDOM.unmountComponentAtNode(root);
                    }
                }
            }

            ReactDOM.render(host("input", JsPropertyMap.of("type", "range", "onChange", onChange)), root);
            input = Browser.query(root, "input");
            calls[0] = 0;
            Browser.setValue(input, "10");
            setValueUntracked(input, "20");
            nativeInputEvent(input, "change");
            check(2 == calls[0], "Range listens for both input and change");
            calls[0] = 0;
            Browser.setValue(input, "40");
            nativeInputEvent(input, "change");
            Browser.setValue(input, "foo");
            nativeInputEvent(input, "change");
            check(2 == calls[0], "Range dispatches only for changes in sanitized value");

            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(host("input", JsPropertyMap.of("type", "radio", "onChange", onChange)), root);
            calls[0] = 0;
            final var radio = Browser.query(root, "input");
            setCheckedUntracked(radio, true);
            nativeInputEvent(radio, "click");
            nativeInputEvent(radio, "click");
            check(1 == calls[0], "A checked radio emits change only once");

            final int[] secondCalls = {0};
            final ReactEventHandler secondChange = event -> {
                equal("change", event.getType());
                secondCalls[0]++;
            };
            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(
                    ReactNode.of(
                            host("input", JsPropertyMap.of("type", "radio", "name", "group", "id", "first"))
                                    .input("onChange", onChange),
                            host("input", JsPropertyMap.of("type", "radio", "name", "group", "id", "second"))
                                    .input("onChange", secondChange)),
                    root);
            calls[0] = 0;
            Browser.query(root, "#first").click();
            check(1 == calls[0] && 0 == secondCalls[0], "The first radio dispatches alone");
            Browser.query(root, "#second").click();
            check(1 == calls[0] && 1 == secondCalls[0], "The second radio dispatches alone");
            Browser.query(root, "#first").click();
            check(2 == calls[0] && 1 == secondCalls[0], "Returning to the first radio dispatches again");

            ReactDOM.unmountComponentAtNode(root);
            withCustomInputValue(() -> {
                final var customInput = host("input", JsPropertyMap.of("type", "text"));
                ReactDOM.render(customInput, root);
                ReactDOM.render(host("input", JsPropertyMap.of("type", "text")), root);
                nativeInputEvent(Browser.query(root, "input"), "change");
                ReactDOM.unmountComponentAtNode(root);
            });
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    private static native void setValueUntracked(Browser.Element input, String value);

    @JsMethod
    private static native void withCustomInputValue(Probe action);

    @JsMethod
    public static void runStockComponentTree(boolean production) {
        final var root = Browser.append("div");
        try {
            final var target = new StringBuilder();
            final ReactEventHandler record = event -> {
                target.setLength(0);
                target.append(Browser.property(event.getCurrentTarget(), "id"));
            };
            ReactDOM.render(
                    host(
                            "div",
                            JsPropertyMap.of(
                                    "id",
                                    "mouseOverID",
                                    "onMouseOver",
                                    record,
                                    "children",
                                    host("div", JsPropertyMap.of("id", "clickID", "onClick", record)))),
                    root);
            check(0 == target.length(), "No current target before an event");
            Browser.mouse(Browser.query(root, "#mouseOverID"), "mouseover", false);
            check("mouseOverID".contentEquals(target), "Mouseover current target resolves to its host");
            Browser.query(root, "#clickID").click();
            check("clickID".contentEquals(target), "Click current target resolves to its host");

            target.setLength(0);
            ReactDOM.render(
                    host(
                            "div",
                            JsPropertyMap.of(
                                    "id",
                                    "closestInstance",
                                    "onClick",
                                    record,
                                    "dangerouslySetInnerHTML",
                                    JsPropertyMap.of("__html", "<div id='unmanaged'></div>"))),
                    root);
            check(0 == target.length(), "Unmanaged child has not been clicked");
            Browser.query(root, "#unmanaged").click();
            check("closestInstance".contentEquals(target), "Unmanaged DOM resolves to the nearest React host");

            final ReactEventHandler a = event -> target.append("A");
            final ReactEventHandler b = event -> target.append("B");
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOver", a)), root);
            final var div = Browser.query(root, "div");
            target.setLength(0);
            Browser.mouse(div, "mouseover", false);
            check("A".contentEquals(target), "Initial host handler is used");
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOver", b)), root);
            target.setLength(0);
            Browser.mouse(div, "mouseover", false);
            check("B".contentEquals(target), "Updated props supply the new host handler");

            final ReactEventHandler control = event -> {
                final var value = Browser.property(event.getCurrentTarget(), "value");
                ReactDOM.render(
                        host("input", JsPropertyMap.of("type", "text", "value", value, "readOnly", true)), root);
            };
            ReactDOM.render(
                    host("input", JsPropertyMap.of("type", "text", "onChange", control, "value", Js.undefined())),
                    root);
            final var input = Browser.query(root, "input");
            final var controlWarning = captureWarnings(() -> Browser.setValue(input, "finish"));
            equal("finish", input.getValue());
            if (!production) {
                check(
                        controlWarning.contains("uncontrolled input of type text to be controlled"),
                        "An edited uncontrolled input becoming controlled warns");
            }

            ReactDOM.render(
                    host("div", JsPropertyMap.of("children", host("span", JsPropertyMap.of("children", "Hello")))),
                    root);
            final var managed = Browser.query(root, "div");
            final var unmountWarning = captureWarnings(
                    () -> check(!ReactDOM.unmountComponentAtNode(managed), "A managed child is not a root container"));
            final var renderWarning = captureWarnings(() -> ReactDOM.render(host("div", JsPropertyMap.of()), managed));
            try {
                if (!production) {
                    check(
                            unmountWarning.contains("was rendered by React and is not a top-level container"),
                            "Unmounting a managed host emits the container diagnostic");
                    check(
                            renderWarning.contains("Replacing React-rendered children with a new root component"),
                            "Replacing managed children with a new root emits the container diagnostic");
                }
            } finally {
                ReactDOM.unmountComponentAtNode(managed);
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runStockEventListeners() {
        final var outerRoot = Browser.append("div");
        final var middleRoot = Browser.append("div");
        final var innerRoot = Browser.append("div");
        final var unmanaged = Browser.append("h1");
        try {
            final var targets = new ArrayList<Object>();
            final ReactEventHandler currentTarget =
                    event -> targets.add(Objects.requireNonNull(event.getCurrentTarget()));
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseEnter", currentTarget)), outerRoot);
            movement(unmanaged, "mouseout", Browser.query(outerRoot, "div"));
            check(1 == targets.size(), "Mouse enters a React tree from unmanaged DOM");
            targets.clear();

            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOut", currentTarget)), innerRoot);
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOut", currentTarget)), middleRoot);
            final var inner = Browser.query(innerRoot, "div");
            final var middle = Browser.query(middleRoot, "div");
            appendExisting(middle, innerRoot);
            movement(inner, "mouseout", null);
            check(2 == targets.size(), "A mouseout dispatches through two nested roots");
            check(Browser.same(inner, targets.get(0)), "The inner root dispatches first");
            check(Browser.same(middle, targets.get(1)), "The parent root dispatches second");
            targets.clear();

            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOut", currentTarget)), outerRoot);
            final var outer = Browser.query(outerRoot, "div");
            appendExisting(outer, middleRoot);
            movement(inner, "mouseout", null);
            check(3 == targets.size(), "A mouseout dispatches through three nested roots");
            check(Browser.same(inner, targets.get(0)), "The inner root dispatches first");
            check(Browser.same(middle, targets.get(1)), "The middle root dispatches second");
            check(Browser.same(outer, targets.get(2)), "The outer root dispatches third");

            final var commitTrace = new ArrayList<String>();
            final ReactEventHandler innerHandler = event -> {
                ReactDOM.render(host("div", JsPropertyMap.of("children", "1")), innerRoot);
                commitTrace.add(inner.getTextContent());
            };
            final ReactEventHandler middleHandler = event -> {
                ReactDOM.render(host("div", JsPropertyMap.of("children", "2")), innerRoot);
                commitTrace.add(inner.getTextContent());
            };
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOut", innerHandler, "children", "Child")), innerRoot);
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseOut", middleHandler)), middleRoot);
            movement(inner, "mouseout", null);
            check(commitTrace.equals(Arrays.asList("Child", "Child")), "Both roots observe the pre-commit value");
            equal("2", inner.getTextContent());

            final var origins = new ArrayList<Object>();
            final ReactEventHandler origin = event -> origins.add(Objects.requireNonNull(event.getTarget()));
            ReactDOM.render(
                    host("div", JsPropertyMap.of("onMouseOut", origin, "children", host("span", JsPropertyMap.of()))),
                    innerRoot);
            final var span = Browser.query(innerRoot, "span");
            movement(span, "mouseout", null);
            check(1 == origins.size(), "A single root dispatches the bubbling event once");
            check(Browser.same(span, origins.get(0)), "The event retains the deepest host target");

            formAndMediaEvents(outerRoot);
            mediaOnlyDocumentListeners();
        } finally {
            ReactDOM.unmountComponentAtNode(innerRoot);
            ReactDOM.unmountComponentAtNode(middleRoot);
            ReactDOM.unmountComponentAtNode(outerRoot);
            innerRoot.remove();
            middleRoot.remove();
            outerRoot.remove();
            unmanaged.remove();
        }
    }

    @JsMethod
    public static void runStockSimpleClicks() {
        final var root = Browser.append("div");
        try {
            final int[] calls = {0};
            final ReactEventHandler onClick = event -> calls[0]++;
            final var buttonProps = JsPropertyMap.<@Nullable Object>of(
                    "disabled", true, "onClick", onClick, "children", host("span", JsPropertyMap.of()));
            final var childProps = JsPropertyMap.<@Nullable Object>of("onClick", onClick);
            final var captureProps = JsPropertyMap.<@Nullable Object>of("onClickCapture", onClick);
            final var cases = new ReactElement[] {
                host("div", JsPropertyMap.of("onClick", onClick)),
                host("div", JsPropertyMap.of("onClick", onClick, "children", host("span", JsPropertyMap.of()))),
                host("button", buttonProps),
                host("button", JsPropertyMap.of("disabled", true, "children", host("span", childProps))),
                host(
                        "div",
                        JsPropertyMap.of(
                                "onClickCapture",
                                onClick,
                                "children",
                                host(
                                        "button",
                                        JsPropertyMap.of(
                                                "disabled", true, "children", host("span", JsPropertyMap.of()))))),
                host("button", JsPropertyMap.of("disabled", true, "children", host("span", captureProps)))
            };
            for (int index = 0; index < cases.length; index++) {
                calls[0] = 0;
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(cases[index], root);
                Browser.query(root, 0 == index ? "div" : "span").click();
                check((2 == index ? 0 : 1) == calls[0], "Click routing for disabled child case " + index);
            }
            for (final var tag : new String[] {"button", "input", "select", "textarea"}) {
                final var enabled = host(tag, JsPropertyMap.of("onClick", onClick));
                final var disabled = host(tag, JsPropertyMap.of("onClick", onClick, "disabled", true));
                final var explicitlyEnabled = host(tag, JsPropertyMap.of("onClick", onClick, "disabled", false));
                final var casesForTag = new ReactElement[][] {
                    {enabled}, {disabled}, {disabled, enabled}, {enabled, disabled}, {disabled, explicitlyEnabled}
                };
                for (int index = 0; index < casesForTag.length; index++) {
                    calls[0] = 0;
                    ReactDOM.unmountComponentAtNode(root);
                    for (final var version : casesForTag[index]) {
                        ReactDOM.render(version, root);
                    }
                    Browser.query(root, tag).click();
                    check((1 == index || 3 == index ? 0 : 1) == calls[0], tag + " click case " + index);
                }
            }
            for (final var tag : new String[] {"button", "div"}) {
                calls[0] = 0;
                ReactDOM.render(host(tag, JsPropertyMap.of("onClick", onClick)), root);
                Browser.mouse(Browser.query(root, tag), "click", false);
                check(1 == calls[0], "Bubbling click invokes " + tag + " once");
                nonBubblingClick(Browser.query(root, tag));
                check(1 == calls[0], "Nonbubbling click does not invoke " + tag);
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    private static native void nonBubblingClick(Browser.Element node);

    @JsMethod
    public static void runStockSelectEvents() {
        for (final var trigger : new String[] {"keydown", "mouseup", "dragend"}) {
            final var root = Browser.append("div");
            try {
                final var selections = new ArrayList<Object>();
                final ReactEventHandler onSelect = event -> {
                    equal("object", Js.typeof(event));
                    equal("select", event.getType());
                    check(Browser.same(Browser.query(root, "input"), event.getTarget()), "Select target is the input");
                    selections.add(Objects.requireNonNull(event.getCurrentTarget()));
                };
                final boolean lateSubscription = "keydown".equals(trigger);
                final ReactEventHandler mouseDown = event -> {};
                ReactDOM.render(
                        host(
                                "input",
                                lateSubscription
                                        ? JsPropertyMap.of("type", "text", "onMouseDown", mouseDown)
                                        : JsPropertyMap.of("type", "text", "onSelect", onSelect)),
                        root);
                final var input = Browser.query(root, "input");
                focus(input);
                if (lateSubscription) {
                    Browser.mouse(input, "mousedown", false);
                    Browser.mouse(input, "mouseup", false);
                    ReactDOM.render(host("input", JsPropertyMap.of("type", "text", "onSelect", onSelect)), root);
                    focus(input);
                    stockKey(input, "keydown", "", 0, 0, false);
                } else {
                    Browser.mouse(input, "focus", false);
                    check(selections.isEmpty(), "Focus alone does not dispatch select");
                    Browser.mouse(input, "mousedown", false);
                    check(selections.isEmpty(), "A pending mouse selection does not dispatch");
                    Browser.mouse(input, trigger, false);
                }
                check(1 == selections.size(), trigger + " dispatches exactly one select");
                check(Browser.same(input, selections.get(0)), "Select currentTarget is the input");
            } finally {
                ReactDOM.unmountComponentAtNode(root);
                root.remove();
            }
        }
    }

    @JsMethod
    public static void runStockEnterLeave() {
        final var iframe = Browser.append("iframe");
        final var frameWindow = Objects.requireNonNull(Browser.property(iframe, "contentWindow"));
        final var frameDocument = Objects.requireNonNull(Browser.property(iframe, "contentDocument"));
        final Browser.Element frameBody = Js.uncheckedCast(Browser.property(frameDocument, "body"));
        final var root = Browser.append("div");
        try {
            final var enters = new ArrayList<SyntheticEvent<?>>();
            final var leaves = new ArrayList<SyntheticEvent<?>>();
            final ReactEventHandler enter = event -> {
                event.persist();
                enters.add(event);
            };
            final ReactEventHandler leave = event -> {
                event.persist();
                leaves.add(event);
            };
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseEnter", enter, "onMouseLeave", leave)), frameBody);
            final var div = Browser.query(frameBody, "div");
            movement(div, "mouseout", Js.uncheckedCast(frameWindow));
            check(1 == leaves.size(), "Iframe dispatches one leave");
            check(Browser.same(div, leaves.get(0).getTarget()), "Iframe leave targets the host div");
            check(
                    Browser.same(frameWindow, Browser.property(leaves.get(0), "relatedTarget")),
                    "Iframe leave uses the iframe window as relatedTarget");
            movement(div, "mouseover", null);
            check(1 == enters.size(), "Iframe dispatches one enter");
            check(Browser.same(div, enters.get(0).getTarget()), "Iframe enter targets the host div");
            check(
                    Browser.same(frameWindow, Browser.property(enters.get(0), "relatedTarget")),
                    "Iframe enter uses the iframe window as relatedTarget");

            enters.clear();
            ReactDOM.render(host("div", JsPropertyMap.of("onMouseEnter", enter)), root);
            final ReactEventHandler childEnter = event -> {
                event.persist();
                leaves.add(event);
            };
            ReactDOM.render(
                    host(
                            "div",
                            JsPropertyMap.of(
                                    "onMouseEnter",
                                    enter,
                                    "children",
                                    host("span", JsPropertyMap.of("onMouseEnter", childEnter)))),
                    root);
            leaves.clear();
            movement(Browser.query(root, "div"), "mouseout", Browser.query(root, "span"));
            check(1 == leaves.size(), "A child inserted on the first update receives enter");
            check(enters.isEmpty(), "The shared parent does not receive enter again");
        } finally {
            ReactDOM.unmountComponentAtNode(frameBody);
            ReactDOM.unmountComponentAtNode(root);
            iframe.remove();
            root.remove();
        }
    }

    @JsMethod
    public static void runEnterLeave() {
        final var root = Browser.append("div");
        final var portal = Browser.append("div");
        try {
            final var child = ReactDOM.createPortal(host("button", JsPropertyMap.of()), portal);
            ReactDOM.render(host("section", JsPropertyMap.of("children", child)), root);
            final var entered = new StringBuilder();
            final ReactEventHandler lateEnter = event -> entered.append(event.getType());
            ReactDOM.render(host("section", JsPropertyMap.of("children", child, "onMouseEnter", lateEnter)), root);
            movement(root, "mouseout", Browser.query(portal, "button"));
            check("mouseenter".contentEquals(entered), "A late ancestor subscription receives portal entry");
            ReactDOM.unmountComponentAtNode(root);
            final var trace = new StringBuilder();
            final ReactEventHandler parentEnter = event -> trace.append("parent-in ");
            final ReactEventHandler parentLeave = event -> trace.append("parent-out ");
            final ReactEventHandler sourceEnter = event -> trace.append("source-in ");
            final SyntheticEvent<?>[] mouseOut = new SyntheticEvent<?>[1];
            final ReactEventHandler onMouseOut = event -> mouseOut[0] = event;
            final ReactEventHandler sourceLeave = event -> {
                check(mouseOut[0] != event, "One native event produces distinct mouse out and leave events");
                check("mouseleave".equals(event.getType()), "Mouse leave has a synthetic event type");
                check("source".equals(Browser.property(event.getTarget(), "id")), "Mouse leave keeps the origin");
                check(
                        "destination"
                                .equals(Browser.property(
                                        Objects.requireNonNull(Browser.property(event, "relatedTarget")), "id")),
                        "Mouse leave related target");
                trace.append("source-out ");
            };
            final ReactEventHandler destinationEnter = event -> {
                check("mouseenter".equals(event.getType()), "Mouse enter has a synthetic event type");
                check(
                        "destination".equals(Browser.property(event.getTarget(), "id")),
                        "Mouse enter targets the destination");
                check(
                        "source"
                                .equals(Browser.property(
                                        Objects.requireNonNull(Browser.property(event, "relatedTarget")), "id")),
                        "Mouse enter related target");
                trace.append("destination-in ");
            };
            final ReactEventHandler pointerEnter = event -> {
                check("pointerenter".equals(event.getType()), "Pointer enter has a synthetic event type");
                check("pen".equals(Browser.property(event, "pointerType")), "Pointer enter retains pointer metadata");
                trace.append("pen-in ");
            };
            final ReactEventHandler pointerLeave = event -> trace.append("pen-out ");
            final var sourceProps = JsPropertyMap.<@Nullable Object>of(
                    "id", "source", "onMouseEnter", sourceEnter, "onMouseLeave", sourceLeave);
            sourceProps.set("onMouseOut", onMouseOut);
            sourceProps.set("dangerouslySetInnerHTML", JsPropertyMap.of("__html", "<i id=\"nested\"></i>"));
            final var destinationProps = JsPropertyMap.<@Nullable Object>of(
                    "id", "destination", "onMouseEnter", destinationEnter, "onPointerEnter", pointerEnter);
            destinationProps.set("onPointerLeave", pointerLeave);
            ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onMouseEnter",
                                    parentEnter,
                                    "onMouseLeave",
                                    parentLeave,
                                    "children",
                                    ReactNode.of(
                                            host("button", sourceProps),
                                            ReactDOM.createPortal(host("button", destinationProps), portal)))),
                    root);
            final var source = Browser.query(root, "#source");
            final var destination = Browser.query(portal, "#destination");
            movement(source, "mouseover", null);
            check("parent-in source-in ".contentEquals(trace), "Enter dispatches from ancestor to descendant");
            movement(source, "mouseout", Browser.query(root, "#nested"));
            check(
                    "parent-in source-in ".contentEquals(trace),
                    "Moving within one logical target keeps its handlers quiet");
            movement(source, "mouseout", destination);
            movement(destination, "mouseover", source);
            check(
                    "parent-in source-in source-out destination-in ".contentEquals(trace),
                    "Moving into a portal skips the shared ancestor and does not duplicate enter");
            movement(destination, "mouseout", null);
            check(
                    "parent-in source-in source-out destination-in parent-out ".contentEquals(trace),
                    "Moving outside leaves the logical ancestor");
            movement(destination, "pointerover", null);
            movement(destination, "pointerout", null);
            check(
                    "parent-in source-in source-out destination-in parent-out pen-in pen-out ".contentEquals(trace),
                    "Pointer over and out synthesize enter and leave");
            trace.setLength(0);
            movement(portal, "mouseout", source);
            movement(source, "mouseover", portal);
            movement(root, "pointerout", destination);
            movement(destination, "pointerover", root);
            check(
                    "parent-in source-in pen-in ".contentEquals(trace),
                    "Movement from unmanaged DOM enters the managed target exactly once");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            portal.remove();
        }
    }

    @JsMethod
    static native void movement(Browser.Element element, String type, Browser.@Nullable Element related);

    @JsMethod
    public static void runStockBeforeInput() {
        for (final var tag : new String[] {"input", "div"}) {
            runStockBeforeInput(tag, InputEnvironment.TEXT_EVENT);
            runStockBeforeInput(tag, InputEnvironment.COMPOSITION_EVENT);
        }
    }

    @JsMethod
    public static void runLegacyBeforeInput() {
        runStockBeforeInput("input", InputEnvironment.DOCUMENT_MODE);
        runStockBeforeInput("div", InputEnvironment.DOCUMENT_MODE);
        runStockBeforeInput("input", InputEnvironment.KEYBOARD_COMPOSITION);
    }

    private enum InputEnvironment {
        TEXT_EVENT,
        COMPOSITION_EVENT,
        DOCUMENT_MODE,
        KEYBOARD_COMPOSITION
    }

    private static void runStockBeforeInput(String tag, InputEnvironment environment) {
        final boolean textInput = InputEnvironment.TEXT_EVENT == environment;
        final boolean ie = InputEnvironment.DOCUMENT_MODE == environment;
        final boolean keyboard = InputEnvironment.KEYBOARD_COMPOSITION == environment;
        final var root = Browser.append("div");
        try {
            final var before = new ArrayList<String>();
            final var composition = new ArrayList<String>();
            final var end = new ArrayList<String>();
            final ReactEventHandler onBefore =
                    event -> before.add(event.getType() + "=" + Browser.property(event, "data"));
            final ReactEventHandler onComposition =
                    event -> composition.add(event.getType() + "=" + Browser.property(event, "data"));
            final var props = JsPropertyMap.<@Nullable Object>of(
                    "onBeforeInput",
                    onBefore,
                    "onCompositionStart",
                    onComposition,
                    "onCompositionUpdate",
                    onComposition);
            props.set("onCompositionEnd", (ReactEventHandler)
                    event -> end.add(event.getType() + "=" + Browser.property(event, "data")));
            if ("div".equals(tag)) {
                props.set("contentEditable", true);
            } else {
                props.set("type", "text");
            }
            final Probe mount = () -> ReactDOM.render(host(tag, props), root);
            switch (environment) {
                case TEXT_EVENT -> mount.run();
                case COMPOSITION_EVENT -> withoutTextInput(mount);
                case DOCUMENT_MODE -> mountLegacyInput(mount, true);
                case KEYBOARD_COMPOSITION -> mountLegacyInput(mount, false);
            }
            final var input = Browser.query(root, tag);
            stockComposition(input, "compositionstart", "test");
            check(before.isEmpty(), "Composition start does not synthesize before-input");
            check(
                    composition.equals(keyboard ? List.of() : List.of("compositionstart=test")),
                    "Composition start dispatches native data");
            composition.clear();
            stockComposition(input, "compositionupdate", "test string");
            check(before.isEmpty(), "Composition update does not synthesize before-input");
            check(
                    composition.equals(keyboard ? List.of() : List.of("compositionupdate=test string")),
                    "Composition update dispatches native data");
            composition.clear();
            stockComposition(input, "compositionend", "test string 3");
            beforeInputEquals(before, ie || keyboard ? null : "compositionend=test string 3");
            beforeInputEquals(end, keyboard ? null : "compositionend=test string 3");
            stockComposition(input, "textInput", "abcß");
            beforeInputEquals(before, textInput ? "textInput=abcß" : null);
            fallbackKeyPress(input, 'a', false, false, false, null);
            beforeInputEquals(before, textInput ? null : "keypress=a");
            fallbackKeyPress(input, ' ', false, false, false, null);
            beforeInputEquals(before, "keypress= ");
            stockComposition(input, "textInput", " ");
            beforeInputEquals(before, null);
            fallbackKeyPress(input, 'a', true, false, false, null);
            beforeInputEquals(before, null);
            fallbackKeyPress(input, 'b', false, true, false, null);
            beforeInputEquals(before, null);
            fallbackKeyPress(input, 'c', true, true, false, null);
            beforeInputEquals(before, textInput ? null : "keypress=c");
            fallbackKeyPress(input, 'X', false, false, false, "😊");
            beforeInputEquals(before, textInput ? null : "keypress=😊");
            stockComposition(input, "textInput", "😊");
            beforeInputEquals(before, textInput ? "textInput=😊" : null);
            final var valueTarget = Objects.requireNonNull(Js.asPropertyMap(input));
            for (int index = 0; index < 3; index++) {
                valueTarget.set("value", 0 == index ? "foo" : "foofoo");
                stockKey(input, "keydown", "", 229, 0, false);
                beforeInputEquals(before, null);
                if (keyboard) {
                    check(
                            composition.size() == 1 && composition.get(0).startsWith("keydown="),
                            "IME keydown starts a keyboard composition");
                    composition.clear();
                } else {
                    check(composition.isEmpty(), "Native composition does not start on IME keydown");
                }
                valueTarget.set("value", 0 == index ? "foobar" : 1 == index ? "fooBARfoo" : "Barfoofoo");
                if (2 == index) {
                    legacyKey(input, "keypress", 60);
                } else {
                    stockKey(input, 0 == index ? "keydown" : "keyup", "", 9, 0, false);
                }
                final var composed =
                        switch (index) {
                            case 0 -> "keydown=bar";
                            case 1 -> "keyup=BAR";
                            default -> "keypress=Bar";
                        };
                beforeInputEquals(before, keyboard ? composed : null);
                beforeInputEquals(end, keyboard ? composed : null);
            }
            if (ie) {
                if ("div".equals(tag)) {
                    valueTarget.delete("value");
                }
                setCompositionText(input, "prefixsuffix");
                stockComposition(input, "compositionstart", "incorrect");
                setCompositionText(input, "prefix　suffix");
                stockComposition(input, "compositionend", "incorrect");
                beforeInputEquals(before, "compositionend=　");
                beforeInputEquals(end, "compositionend=　");
                stockComposition(input, "compositionstart", "한", "ko");
                stockComposition(input, "compositionend", "한", "ko");
                beforeInputEquals(before, "compositionend=한");
                beforeInputEquals(end, "compositionend=한");
            }
            dispatch(input, "onPaste");
            beforeInputEquals(before, null);
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void beforeInputEquals(ArrayList<String> before, @Nullable String expected) {
        if (null == expected) {
            check(before.isEmpty(), "Expected no before-input event, got " + before);
        } else {
            check(before.size() == 1 && expected.equals(before.get(0)), "Expected " + expected + ", got " + before);
        }
        before.clear();
    }

    @JsMethod
    private static native void stockComposition(Browser.Element input, String type, String data);

    @JsMethod(name = "stockLocalizedComposition")
    private static native void stockComposition(Browser.Element input, String type, String data, String locale);

    @JsMethod
    private static native void mountLegacyInput(Probe mount, boolean ie);

    @JsMethod
    private static native void setCompositionText(Browser.Element input, String text);

    @JsMethod
    public static void runBeforeInput() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler composition = event -> trace.append(event.getType())
                    .append('=')
                    .append(Browser.property(event, "data"))
                    .append(',');
            final ReactEventHandler before = event -> trace.append("before=")
                    .append(Browser.property(event, "data"))
                    .append(',');
            final ReactEventHandler capture = event -> trace.append("capture,");
            final var inputs = JsPropertyMap.<@Nullable Object>of(
                    "onCompositionStart",
                    composition,
                    "onCompositionUpdate",
                    composition,
                    "onCompositionEnd",
                    composition);
            inputs.set("onBeforeInput", before);
            inputs.set("onBeforeInputCapture", capture);
            ReactDOM.render(host("input", inputs), root);
            final var input = Browser.query(root, "input");
            dispatch(input, "onCompositionStart");
            dispatch(input, "onCompositionUpdate");
            dispatch(input, "onCompositionEnd");
            check(
                    "compositionstart=t,compositionupdate=ty,compositionend=typed,".contentEquals(trace),
                    "Chromium composition events carry native data without duplicating before-input");
            dispatch(input, "onNativeBeforeInput");
            check(
                    "compositionstart=t,compositionupdate=ty,compositionend=typed,".contentEquals(trace),
                    "React 16 extracts before-input from textInput");
            dispatch(input, "onTextInput");
            dispatch(input, "onSpaceKeyPress");
            dispatch(input, "onSpaceTextInput");
            dispatch(input, "onCustomCompositionEnd");
            check(
                    ("compositionstart=t,compositionupdate=ty,compositionend=typed,capture,before=typed,capture,before="
                                    + " ,compositionend=custom,capture,before=custom,")
                            .contentEquals(trace),
                    "Text input, space de-duplication and custom IME data dispatch in order");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runBeforeInputFallback() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler before =
                    event -> trace.append(Browser.property(event, "data")).append(' ');
            withoutTextInput(() -> ReactDOM.render(host("input", JsPropertyMap.of("onBeforeInput", before)), root));
            final var input = Browser.query(root, "input");
            fallbackKeyPress(input, 97, false, false, false, null);
            fallbackKeyPress(input, 98, true, false, false, null);
            fallbackKeyPress(input, 99, false, true, false, null);
            fallbackKeyPress(input, 100, false, false, true, null);
            fallbackKeyPress(input, 101, true, true, false, null);
            fallbackKeyPress(input, 0, false, false, false, null);
            fallbackKeyPress(input, 0, false, false, false, "語字");
            dispatch(input, "onCompositionEnd");
            dispatch(input, "onTextInput");
            dispatch(input, "onCopy");
            check(
                    "a e 語字 typed ".contentEquals(trace),
                    "Keypress fallback accepts text and AltGr, skips shortcuts and native textInput");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    private static native void withoutTextInput(Probe mount);

    @JsMethod
    private static native void fallbackKeyPress(
            Browser.Element input, int which, boolean ctrl, boolean alt, boolean meta, @Nullable String chars);

    @JsMethod
    public static void runNestingDiagnostics(final boolean production) {
        final var root = Browser.append("div");
        final var portal = Browser.append("div");
        try {
            final var allowed = captureWarnings(() -> {
                ReactDOM.render(nestedHosts("table", "tbody", "tr", "td", "b"), root);
                ReactDOM.render(nestedHosts("a", "object", "a"), root);
                ReactDOM.render(nestedHosts("p", "button", "p"), root);
                ReactDOM.render(nestedHosts("p", "svg", "foreignObject", "p"), root);
                ReactDOM.render(nestedHosts("ul", "ul", "li"), root);
                ReactDOM.render(nestedHosts("ul", "li", "section", "li"), root);
                ReactDOM.render(nestedHosts("ul", "li", "dd", "li"), root);
                ReactDOM.render(nestedHosts("label", "div"), root);
                ReactDOM.render(
                        host("p", JsPropertyMap.of("children", ReactDOM.createPortal(nestedHosts("p"), portal))), root);
                ReactDOM.render(host("span", JsPropertyMap.of("children", "inline text")), root);
            });
            check(allowed.isEmpty(), "Nesting diagnostics preserve the allowed parser structures: " + allowed);

            final var warnings = captureWarnings(() -> {
                ReactDOM.render(nestedHosts("table", "tr"), root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(nestedHosts("table", "tr"), root);
                ReactDOM.render(nestedHosts("p", "div", "p"), root);
                ReactDOM.render(nestedHosts("ul", "li", "div", "li"), root);
                ReactDOM.render(nestedHosts("form", "form"), root);
                ReactDOM.render(host("table", JsPropertyMap.of("children", "fostered")), root);
                ReactDOM.render(
                        host("table", JsPropertyMap.of("children", host("tbody", JsPropertyMap.of("children", " ")))),
                        root);
                ReactDOM.render(
                        host(
                                "table",
                                JsPropertyMap.of(
                                        "children",
                                        host(
                                                "thead",
                                                JsPropertyMap.of(
                                                        "children",
                                                        ReactNode.of(
                                                                ReactNode.of("separate text"),
                                                                nestedHosts("tr", "th")))))),
                        root);
            });
            if (production) {
                check(warnings.isEmpty(), "Optimized rendering omits nesting diagnostics");
            } else {
                for (final var expected : new String[] {
                    "<tr> cannot appear as a child of <table>. Add a <tbody>",
                    "<div> cannot appear as a descendant of <p>",
                    "<li> cannot appear as a descendant of <li>",
                    "<form> cannot appear as a descendant of <form>",
                    "Text nodes cannot appear as a child of <table>",
                    "Whitespace text nodes cannot appear as a child of <tbody>",
                    "Text nodes cannot appear as a child of <thead>"
                }) {
                    final int first = warnings.indexOf(expected);
                    check(
                            0 <= first && first == warnings.lastIndexOf(expected),
                            "Expected one nesting warning: " + expected + "\nObserved:\n" + warnings);
                }
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            portal.remove();
        }
    }

    private static ReactElement nestedHosts(final String... tags) {
        var host = host(tags[tags.length - 1], JsPropertyMap.of());
        for (int i = tags.length - 2; 0 <= i; i--) {
            host = host(tags[i], JsPropertyMap.of("children", host));
        }
        return host;
    }

    @JsMethod
    public static void runFormDiagnostics(final boolean production) {
        final var root = Browser.append("div");
        try {
            final var ignoring = captureWarnings(() -> {
                ReactDOM.render(DOM.input(new InputProps().value("readonly").readonly()), root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(
                        DOM.textarea(new TextAreaProps().value("disabled").disabled()), root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(DOM.input(new InputProps().type(InputType.radio).value("radio")), root);
                ReactDOM.unmountComponentAtNode(root);
            });
            check(ignoring.isEmpty(), "Readonly and disabled controls can omit onChange");

            final var warnings = captureWarnings(() -> {
                ReactDOM.render(DOM.input(new InputProps().type(InputType.text).value("")), root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(DOM.input(new InputProps().type(InputType.text).value("another")), root);
                ReactDOM.render(DOM.input(new InputProps().type(InputType.text).defaultValue("reset")), root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(DOM.input(new InputProps().type(InputType.text).defaultValue("reset")), root);
                ReactDOM.render(
                        DOM.input(new InputProps()
                                .type(InputType.text)
                                .value("controlled")
                                .readonly()),
                        root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(DOM.input(new InputProps().type(InputType.radio).checked(false)), root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(
                        DOM.input(new InputProps()
                                .type(InputType.radio)
                                .checked()
                                .defaultChecked()
                                .readonly()),
                        root);
                ReactDOM.unmountComponentAtNode(root);
                ReactDOM.render(
                        DOM.input(new InputProps()
                                .type(InputType.text)
                                .value("value")
                                .defaultValue("default")
                                .readonly()),
                        root);
                ReactDOM.render(
                        DOM.textarea(new TextAreaProps()
                                .value("value")
                                .defaultValue("default")
                                .readonly()),
                        root);
                ReactDOM.render(
                        DOM.select(new SelectProps()
                                .value("value")
                                .defaultValue("default")
                                .disabled()),
                        root);
                ReactDOM.render(DOM.input(new InputProps().value(null)), root);
            });
            if (production) {
                check(warnings.isEmpty(), "Optimized form controls omit development warnings");
            } else {
                for (final var expected : new String[] {
                    "You provided a `value` prop to a form field without an `onChange` handler",
                    "You provided a `checked` prop to a form field without an `onChange` handler",
                    "changing a controlled input of type text to be uncontrolled",
                    "changing an uncontrolled input of type text to be controlled",
                    "with both checked and defaultChecked props",
                    "an input of type text with both value and defaultValue props",
                    "Textarea elements must be either controlled or uncontrolled",
                    "Select elements must be either controlled or uncontrolled",
                    "`value` prop on `input` should not be null"
                }) {
                    final int first = warnings.indexOf(expected);
                    check(
                            0 <= first && first == warnings.lastIndexOf(expected),
                            "Expected one form warning: " + expected + "\nObserved:\n" + warnings);
                }
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runControlsAndEvents() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler change = event -> trace.append(Browser.property(event.getTarget(), "value"));
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "textarea", null, null, JsPropertyMap.of("onChange", change, "value", "committed")),
                    root);
            final var textarea = Browser.query(root, "textarea");
            writeValue(textarea, "programmatic");
            check(0 == trace.length(), "A programmatic value assignment does not synthesize change");
            check("programmatic".equals(textarea.getValue()), "An ordinary input event does not restore the value");
            Browser.setValue(textarea, "user");
            check("user".contentEquals(trace), "A native edit synthesizes change");
            check("committed".equals(textarea.getValue()), "Controlled textarea restores its value");
            attribute(textarea, "value", null);

            ReactDOM.render(host("input", JsPropertyMap.of("onChange", change, "defaultValue", "initial")), root);
            final var deduplicated = Browser.query(root, "input");
            trace.setLength(0);
            nativeInputEvent(deduplicated, "change");
            check(0 == trace.length(), "A change event for the initial input value stays quiet");
            Browser.setValue(deduplicated, "edited");
            nativeInputEvent(deduplicated, "change");
            check("edited".contentEquals(trace), "An input event and a change event report the same edit once");

            ReactDOM.render(select("b", null, false, change), root);
            final var select = Browser.query(root, "select");
            check("b".equals(select.getValue()), "Select applies the controlled value after creating options");
            trace.setLength(0);
            Browser.setValue(select, "a");
            check("a".contentEquals(trace), "Select change reads the selected option");
            check("b".equals(select.getValue()), "Controlled select restores the selected option");
            writeValue(select, "a");
            check("a".equals(select.getValue()), "Changing selected options without a change event remains visible");
            ReactDOM.render(select("b", null, false, change), root);
            check("b".equals(select.getValue()), "Rerendering a controlled select reasserts its value");
            ReactDOM.render(select(null, null, false, change), root);
            check("b".equals(select.getValue()), "A select keeps its value when becoming uncontrolled");
            Browser.setValue(select, "a");
            ReactDOM.render(select(null, "b", false, change), root);
            check("a".equals(select.getValue()), "Updating an uncontrolled select default preserves a new selection");
            ReactDOM.render(host("select", JsPropertyMap.of("children", ReactNode.of(option("a"), option("b")))), root);
            trace.setLength(0);
            Browser.setValue(select, "b");
            check("b".equals(select.getValue()), "Native change remains usable after removing onChange");
            check(0 == trace.length(), "The removed select listener does not fire");

            ReactDOM.render(select(null, new String[] {"b"}, true, change), root);
            ReactDOM.render(select(null, "a", false, change), root);
            check("a".equals(select.getValue()), "Changing multiple reapplies the uncontrolled default");

            final SyntheticEvent<?>[] released = new SyntheticEvent<?>[1];
            final SyntheticEvent<?>[] retained = new SyntheticEvent<?>[1];
            final ReactEventHandler keyDown = event -> {
                check("ArrowLeft".equals(Browser.property(event, "key")), "Keyboard key aliases are normalized");
                check(Browser.same(Browser.number(0), Browser.property(event, "charCode")), "Keydown charCode is zero");
                check(
                        Browser.same(Browser.number(37), Browser.property(event, "which")),
                        "Keydown which comes from keyCode");
                check(null != event.getCurrentTarget(), "Current target is set in the handler");
                if (null == released[0]) {
                    released[0] = event;
                } else {
                    check(released[0] == event, "Non-persistent keyboard events are reused");
                    event.persist();
                    retained[0] = event;
                }
            };
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input",
                            null,
                            null,
                            JsPropertyMap.of("onKeyDown", keyDown, "autoFocus", Browser.bool(true))),
                    root);
            final var input = Browser.query(root, "input");
            check(isFocused(input), "Autofocus runs after placement");
            check(
                    Boolean.FALSE.equals(Browser.property(input, "autofocus")),
                    "Autofocus does not set an autofocus attribute");
            dispatch(input, "onLegacyKeyDown");
            dispatch(input, "onLegacyKeyDown");
            final var persisted = Objects.requireNonNull(retained[0]);
            check("keydown".equals(persisted.getType()), "Persisted events retain their type after dispatch");
            check(null == Browser.property(persisted, "currentTarget"), "Persisted events clear currentTarget");

            trace.setLength(0);
            final ReactEventHandler onSelect = event -> {
                check("select".equals(event.getType()), "Synthetic select type");
                check(
                        Browser.same(Browser.query(root, "input"), event.getTarget()),
                        "Select targets the focused input");
                trace.append("select ");
            };
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input",
                            "selection",
                            null,
                            JsPropertyMap.of(
                                    "onSelect", onSelect, "value", "selected", "autoFocus", Browser.bool(true))),
                    root);
            final var selectable = Browser.query(root, "input");
            setSelection(selectable, 1, 3);
            check("select ".contentEquals(trace), "Selection change emits a select event");
            setSelection(selectable, 1, 3);
            check("select ".contentEquals(trace), "Unchanged selection does not dispatch");
            setSelection(selectable, 2, 2);
            check("select select ".contentEquals(trace), "A collapsed selection change also emits select");
            Browser.mouse(selectable, "mousedown", false);
            setSelection(selectable, 0, 4);
            check("select select ".contentEquals(trace), "Selection changes are held while dragging");
            Browser.mouse(root, "mouseup", false);
            check("select select select ".contentEquals(trace), "Releasing outside the managed input emits select");
            Browser.mouse(selectable, "mousedown", false);
            setSelection(selectable, 1, 2);
            check("select select select ".contentEquals(trace), "Selection remains deferred during the next drag");
            nativeInputEvent(selectable, "dragend");
            check("select select select select ".contentEquals(trace), "Drag end releases a pending selection");

            final var keyed = ReactElement.createHostElement(
                    "input",
                    "keyed",
                    null,
                    JsPropertyMap.of("value", "range", "onChange", change, "autoFocus", Browser.bool(true)));
            final var sibling = ReactElement.createHostElement("span", "sibling", null, JsPropertyMap.of());
            ReactDOM.render(ReactNode.of(keyed, sibling), root);
            final var moved = Browser.query(root, "input");
            setSelection(moved, 1, 4);
            ReactDOM.render(ReactNode.of(sibling, keyed), root);
            check(isFocused(moved), "Moving a keyed input retains focus");
            check(
                    Browser.same(Browser.number(1), Browser.property(moved, "selectionStart")),
                    "Selection start is retained");
            check(
                    Browser.same(Browser.number(4), Browser.property(moved, "selectionEnd")),
                    "Selection end is retained");

            final var number = ReactElement.createHostElement(
                    "input",
                    "number",
                    null,
                    JsPropertyMap.of("type", "number", "value", Browser.number(1), "onChange", change));
            ReactDOM.render(number, root);
            final var numeric = Browser.query(root, "input");
            focus(numeric);
            Browser.setValue(numeric, "1.0");
            check("1.0".equals(numeric.getValue()), "Equivalent number edits retain the displayed value");
            check("1".equals(numeric.getAttribute("value")), "Focused number edits defer the value attribute");
            blur(numeric);
            check("1.0".equals(numeric.getAttribute("value")), "Blurring syncs the number value attribute");
            reentrantChanges(root);
            inputValueVariants(root);
            rawFormValues(root);
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void rawFormValues(final Browser.Element root) {
        final ReactEventHandler ignore = event -> {};
        for (final var value : nonTextValues()) {
            for (final var tag : new String[] {"input", "textarea", "select"}) {
                final var props = JsPropertyMap.<@Nullable Object>of("value", "ordinary", "onChange", ignore);
                if ("select".equals(tag)) {
                    final var other = host("option", JsPropertyMap.of("value", value, "children", "other"));
                    props.set("children", ReactNode.of(option("ordinary"), other));
                }
                ReactDOM.render(host(tag, props), root);
                final var control = Browser.query(root, tag);
                check("ordinary".equals(control.getValue()), tag + " starts with the controlled text value");
                final var next = JsPropertyMap.<@Nullable Object>of("value", value, "onChange", ignore);
                if ("select".equals(tag)) {
                    next.set("children", props.get("children"));
                }
                ReactDOM.render(host(tag, next), root);
                check("".equals(control.getValue()), tag + " normalizes a non-text scalar to an empty value");
                if ("select".equals(tag)) {
                    attribute(Browser.query(control, "option:last-child"), "value", "");
                }
            }
        }
    }

    private static void reentrantChanges(final Browser.Element root) {
        final ReactEventHandler ignore = event -> {};
        ReactDOM.render(host("input", JsPropertyMap.of("value", "lion", "readOnly", Browser.bool(true))), root);
        final var readOnly = Browser.query(root, "input");
        Browser.setValue(readOnly, "giraffe");
        check("lion".equals(readOnly.getValue()), "Controlled values restore even without a change handler");

        final var trace = new StringBuilder();
        final ReactEventHandler blur = event ->
                trace.append(Browser.property(event.getTarget(), "value")).append(' ');
        final var destination = host("input", JsPropertyMap.of("id", "destination"));
        final ReactEventHandler change = event -> {
            final var first = Browser.query(root, "#editing");
            final var edited = first.getValue();
            ReactDOM.render(
                    ReactNode.of(
                            host("input", JsPropertyMap.of("id", "editing", "value", edited, "onChange", ignore)),
                            destination),
                    root);
            nativeInputEvent(first, "input");
            focus(Browser.query(root, "#destination"));
        };
        final var editing = JsPropertyMap.<@Nullable Object>of("id", "editing", "value", "lion", "onChange", change);
        editing.set("onBlur", blur);
        ReactDOM.render(ReactNode.of(host("input", editing), destination), root);
        final var first = Browser.query(root, "#editing");
        focus(first);
        Browser.setValue(first, "giraffe");
        check("giraffe ".contentEquals(trace), "Reentrant blur sees the live value before restoration");
        check("giraffe".equals(first.getValue()), "A batched controlled update commits the edited value");
        check(isFocused(Browser.query(root, "#destination")), "Reentrant change moves focus to the other input");

        trace.setLength(0);
        final ReactEventHandler checkboxChange = event -> trace.append(Browser.property(event.getTarget(), "checked"));
        final ReactEventHandler nested = event -> {
            final var checkbox = Browser.query(root, "#other");
            checkbox.click();
            trace.append('/').append(Browser.property(checkbox, "checked"));
        };
        final var checkbox =
                JsPropertyMap.<@Nullable Object>of("id", "other", "type", "checkbox", "checked", Browser.bool(true));
        checkbox.set("onChange", checkboxChange);
        ReactDOM.render(
                ReactNode.of(
                        host("input", JsPropertyMap.of("value", "lion", "onChange", nested)), host("input", checkbox)),
                root);
        final var changed = Browser.query(root, "input");
        Browser.setValue(changed, "giraffe");
        check("false/false".contentEquals(trace), "Nested changes stay visible until the enclosing handler finishes");
        check("lion".equals(changed.getValue()), "The outer controlled input is restored");
        check(
                Boolean.TRUE.equals(Browser.property(Browser.query(root, "#other"), "checked")),
                "The nested controlled checkbox is restored");
    }

    private static void inputValueVariants(final Browser.Element root) {
        final ReactEventHandler ignore = event -> {};
        for (final var type : new String[] {"text", "number", "checkbox", "radio"}) {
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input", type, null, JsPropertyMap.of("type", type, "value", "", "onChange", ignore)),
                    root);
            final var input = Browser.query(root, "input");
            check("".equals(input.getValue()), type + " begins with an empty value");
            attribute(input, "value", "");
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input",
                            type,
                            null,
                            JsPropertyMap.of("type", type, "value", Browser.number(0), "onChange", ignore)),
                    root);
            check("0".equals(input.getValue()), type + " accepts numeric zero after an empty value");
            attribute(input, "value", "0");
            if ("number".equals(type) || "text".equals(type)) {
                focus(input);
                Browser.setValue(input, "0.0");
                check(
                        ("number".equals(type) ? "0.0" : "0").equals(input.getValue()),
                        type + " handles a decimal zero edit");
                blur(input);
                ReactDOM.render(
                        ReactElement.createHostElement(
                                "input", type, null, JsPropertyMap.of("type", type, "value", "", "onChange", ignore)),
                        root);
                check("".equals(input.getValue()), type + " accepts an empty value after zero");
            }
        }

        final String[] decimals = {".98", "0.98", "3.0000", "3"};
        for (final var decimal : decimals) {
            ReactDOM.render(
                    host("input", JsPropertyMap.of("type", "number", "value", decimal, "onChange", ignore)), root);
            check(
                    decimal.equals(Browser.query(root, "input").getValue()),
                    "Number preserves the precision of " + decimal);
        }

        ReactDOM.render(
                ReactElement.createHostElement("input", "empty-default", null, JsPropertyMap.of("defaultValue", "")),
                root);
        attribute(Browser.query(root, "input"), "value", "");

        final var writable = JsPropertyMap.<@Nullable Object>of("onChange", ignore, "value", "0");
        ReactDOM.render(host("input", writable), root);
        final var input = Browser.query(root, "input");
        final int writes = inputValueWrites(input, () -> {
            ReactDOM.render(host("input", JsPropertyMap.of("onChange", ignore, "value", Browser.number(0))), root);
            ReactDOM.render(host("input", JsPropertyMap.of("onChange", ignore, "value", "true")), root);
            ReactDOM.render(host("input", JsPropertyMap.of("onChange", ignore, "value", Browser.bool(true))), root);
        });
        check(1 == writes, "Only serialized value changes write the input value property");
    }

    private static void formDefaults(final Browser.Element root) {
        for (final var tag : new String[] {"input", "textarea"}) {
            final ReactEventHandler ignore = event -> {};
            ReactDOM.render(host(tag, JsPropertyMap.of("value", "controlled", "onChange", ignore)), root);
            final var control = Browser.query(root, tag);
            ReactDOM.render(host(tag, JsPropertyMap.of("defaultValue", "reset")), root);
            check("controlled".equals(control.getValue()), tag + " keeps its live value when becoming uncontrolled");
            Browser.setValue(control, "edit");
            ReactDOM.render(host(tag, JsPropertyMap.of("defaultValue", "")), root);
            check("edit".equals(control.getValue()), tag + " preserves edits across default changes");
            check("".equals(Browser.property(control, "defaultValue")), tag + " can reset to an empty default");
        }

        ReactDOM.render(
                host(
                        "form",
                        JsPropertyMap.of(
                                "children",
                                ReactNode.of(
                                        host("input", JsPropertyMap.of("defaultValue", "initial")),
                                        host("input", JsPropertyMap.of("type", "reset"))))),
                root);
        final var form = Browser.query(root, "form");
        final var control = Browser.query(form, "input");
        Browser.setValue(control, "edit");
        ReactDOM.render(
                host(
                        "form",
                        JsPropertyMap.of(
                                "children",
                                ReactNode.of(
                                        host("input", JsPropertyMap.of("defaultValue", "next")),
                                        host("input", JsPropertyMap.of("type", "reset"))))),
                root);
        check("edit".equals(control.getValue()), "Updating the default preserves a user edit before form reset");
        Browser.query(form, "[type=reset]").click();
        check("next".equals(control.getValue()), "Form reset uses the latest input default");
    }

    @JsMethod
    public static void treeTraversal() {
        final var root = Browser.append("div");
        final var outside = Browser.append("div");
        final var far = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final var first = traversalNode("first", trace);
            final var second = traversalNode("second", trace);
            final var branch = traversalNode("branch", trace, first, second);
            final var parent = traversalNode("parent", trace, traversalNode("middle", trace, branch));
            ReactDOM.render(parent, root);
            final var firstHost = Browser.query(root, "#first");
            final var secondHost = Browser.query(root, "#second");
            final var parentHost = Browser.query(root, "#parent");
            final var branchHost = Browser.query(root, "#branch");

            outside.click();
            equal("", trace.toString());
            firstHost.click();
            equal(
                    "parent capture middle capture branch capture first capture "
                            + "first bubble branch bubble middle bubble parent bubble ",
                    trace.toString());
            trace.setLength(0);
            parentHost.click();
            equal("parent capture parent bubble ", trace.toString());
            trace.setLength(0);

            movement(outside, "mouseout", far);
            movement(firstHost, "mouseout", firstHost);
            equal("", trace.toString());
            movement(firstHost, "mouseout", secondHost);
            equal("first mouseleave second mouseenter ", trace.toString());
            trace.setLength(0);
            movement(firstHost, "mouseout", branchHost);
            equal("first mouseleave ", trace.toString());
            trace.setLength(0);
            movement(branchHost, "mouseover", null);
            equal("parent mouseenter middle mouseenter branch mouseenter ", trace.toString());
            trace.setLength(0);
            movement(parentHost, "mouseover", null);
            equal("parent mouseenter ", trace.toString());
            trace.setLength(0);
            movement(branchHost, "mouseout", outside);
            equal("branch mouseleave middle mouseleave parent mouseleave ", trace.toString());
            trace.setLength(0);
            movement(parentHost, "mouseout", outside);
            equal("parent mouseleave ", trace.toString());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            far.remove();
            outside.remove();
            root.remove();
        }
    }

    private static ReactElement traversalNode(String name, StringBuilder trace, ReactElement... children) {
        final ReactEventHandler capture = event -> trace.append(name).append(" capture ");
        final ReactEventHandler bubble = event -> trace.append(name).append(" bubble ");
        final ReactEventHandler movement =
                event -> trace.append(name).append(' ').append(event.getType()).append(' ');
        final var inputs = JsPropertyMap.<@Nullable Object>of("id", name, "onClickCapture", capture, "onClick", bubble);
        inputs.set("onMouseEnter", movement);
        inputs.set("onMouseLeave", movement);
        inputs.set("children", children);
        return ReactElement.createHostElement("div", name, null, inputs);
    }

    @JsMethod
    public static void nativeSimulationEquivalents() {
        final var root = Browser.append("div");
        try {
            final var targets = new @Nullable Object[1];
            final ReactEventHandler change = event -> targets[0] = event.getTarget();
            final var input = host("input", JsPropertyMap.of("onChange", change));
            final var values = new String[] {"giraffe", "zebra", "third"};
            for (int i = 0; i < values.length; i++) {
                ReactDOM.render(0 == i ? input : host("div", JsPropertyMap.of("children", input)), root);
                final var control = Browser.query(root, "input");
                targets[0] = null;
                Browser.setValue(control, values[i]);
                check(control == targets[0], "A real input change identifies the receiving DOM target");
            }
            final ReactEventHandler key = event -> {
                equal("keydown", event.getType());
                equal("keydown", Browser.string(Browser.property(event.getNativeEvent(), "type")));
            };
            final ReactEventHandler click = event -> check(
                    Js.isTripleEqual(Browser.number(100), Browser.property(event, "clientX")),
                    "Native mouse coordinate");
            ReactDOM.render(host("div", JsPropertyMap.of("onKeyDown", key, "onClick", click)), root);
            final var target = Browser.query(root, "div");
            nativeInputEvent(target, "keydown");
            nativeMouseWithX(target, 100);
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    private static native void nativeMouseWithX(Browser.Element target, int x);

    @JsMethod
    public static void independentTargets() {
        final var root = Browser.append("div");
        final var legacyRoot = Browser.append("div");
        try {
            final var clicks = new int[1];
            final ReactEventHandler click = event -> clicks[0]++;
            ReactDOM.render(
                    host(
                            "div",
                            JsPropertyMap.of(
                                    "onClick",
                                    click,
                                    "dangerouslySetInnerHTML",
                                    JsPropertyMap.of("__html", "<button data-reactid=legacy>click me</button>"))),
                    root);
            Browser.query(root, "button").click();
            check(1 == clicks[0], "Foreign descendants can bubble through the current root");

            clicks[0] = 0;
            legacyRoot.setAttribute("data-reactid", "legacy");
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", click)), legacyRoot);
            final var button = Browser.query(legacyRoot, "button");
            button.click();
            check(1 == clicks[0], "A foreign marker on the container does not intercept clicks");
            clicks[0] = 0;
            ReactDOM.unmountComponentAtNode(legacyRoot);
            button.click();
            check(0 == clicks[0], "A detached host does not retain an active listener");

            final var submits = new int[1];
            final ReactEventHandler submit = event -> {
                event.preventDefault();
                submits[0]++;
            };
            final var input = host("input", JsPropertyMap.of("type", "submit"));
            final var form = host("form", JsPropertyMap.of("children", input));
            ReactDOM.render(host("div", JsPropertyMap.of("children", form, "onSubmit", submit)), root);
            Browser.query(root, "input").click();
            check(1 == submits[0], "Submitting the nested form bubbles once");

            final var loads = new int[1];
            final ReactEventHandler load = event -> loads[0]++;
            ReactDOM.render(host("iframe", JsPropertyMap.of("onLoad", load)), root);
            final var before = loads[0];
            nativeIframeLoad(Browser.query(root, "iframe"));
            check(before + 1 == loads[0], "An iframe load reaches its handler once");
        } finally {
            ReactDOM.unmountComponentAtNode(legacyRoot);
            ReactDOM.unmountComponentAtNode(root);
            legacyRoot.remove();
            root.remove();
        }
    }

    @JsMethod
    private static native void nativeIframeLoad(Browser.Element iframe);

    @JsMethod
    public static void runInvalidListeners(final boolean production) {
        final var root = Browser.append("div");
        final var invalid = "Expected `onClick` listener to be a function, instead got a value of `string` type.";
        try {
            final var warning =
                    captureWarnings(() -> ReactDOM.render(host("div", JsPropertyMap.of("onClick", "invalid")), root));
            check(production ? warning.isEmpty() : warning.contains(invalid), "Invalid listener render warning");
            final var error = clickError(Browser.query(root, "div"));
            check(
                    production ? !error.isEmpty() : error.contains(invalid),
                    "Dispatch rejects a truthy invalid listener");

            final var quiet =
                    captureWarnings(() -> ReactDOM.render(host("div", JsPropertyMap.of("onClick", null)), root));
            check(quiet.isEmpty(), "A null listener does not warn");
            check(clickError(Browser.query(root, "div")).isEmpty(), "Dispatch accepts a null listener");
            final var falseWarning = captureWarnings(() ->
                    ReactDOM.render(host("div", JsPropertyMap.of("onClick", Browser.bool(false))), root));
            check(
                    production ? falseWarning.isEmpty() : falseWarning.contains("instead got `false`"),
                    "A false listener receives the conditional-listener guidance");
            check(clickError(Browser.query(root, "div")).isEmpty(), "Dispatch ignores a false listener");

            final var trace = new StringBuilder();
            final ReactEventHandler parent = event -> trace.append("parent ");
            captureWarnings(() -> ReactDOM.render(
                    host("input", JsPropertyMap.of("type", "checkbox", "onClick", parent, "onChange", "invalid")),
                    root));
            check(!clickError(Browser.query(root, "input")).isEmpty(), "Invalid change extraction rejects the click");
            check(0 == trace.length(), "Extraction failure discards the already collected click");
            captureWarnings(() -> ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onClick",
                                    parent,
                                    "children",
                                    host(
                                            "button",
                                            JsPropertyMap.of("disabled", Browser.bool(true), "onClick", "invalid")))),
                    root));
            check(
                    clickError(Browser.query(root, "button")).isEmpty(),
                    "Disabled controls suppress the invalid listener");
            check("parent ".contentEquals(trace), "Disabled controls still dispatch to ancestors");

            final ReactEventHandler replacement = event -> {
                trace.append("replacement ");
                ReactDOM.render(ReactNode.of("committed"), root);
            };
            ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onClick",
                                    parent,
                                    "children",
                                    host("button", JsPropertyMap.of("onClick", replacement)))),
                    root);
            check(clickError(Browser.query(root, "button")).isEmpty(), "A valid replacement handler dispatches");
            check("parent replacement parent ".contentEquals(trace), "The next dispatch completes both handlers");
            check("committed".equals(root.getTextContent()), "The next event batch commits");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runDomEventVariants() {
        mediaOnlyDocumentListeners();
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler capture = event -> trace.append("capture ");
            final ReactEventHandler button = event -> trace.append("button ");
            final ReactEventHandler parent = event -> trace.append("parent ");
            final ReactEventHandler enter = event -> trace.append("enter ");
            ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onClickCapture",
                                    capture,
                                    "onClick",
                                    parent,
                                    "children",
                                    host(
                                            "button",
                                            JsPropertyMap.of(
                                                    "disabled",
                                                    Browser.bool(true),
                                                    "onClick",
                                                    button,
                                                    "onMouseEnter",
                                                    enter)))),
                    root);
            final var disabled = Browser.query(root, "button");
            mouseButton(disabled, 2);
            check(0 == trace.length(), "Right clicks do not produce a synthetic click");
            mouseButton(disabled, 0);
            check(
                    "capture parent ".contentEquals(trace),
                    "Disabled buttons suppress their own click but preserve ancestors");
            movement(disabled, "mouseover", null);
            check("capture parent enter ".contentEquals(trace), "Disabled buttons still receive mouse enter");

            final ReactEventHandler stop = event -> {
                trace.append("stop ");
                event.stopPropagation();
            };
            ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onClick",
                                    parent,
                                    "onClickCapture",
                                    stop,
                                    "children",
                                    host("button", JsPropertyMap.of("onClick", button)))),
                    root);
            trace.setLength(0);
            mouseButton(disabled, 0);
            check("stop ".contentEquals(trace), "Stopping in capture skips later click handlers");
            ReactDOM.render(
                    host(
                            "section",
                            JsPropertyMap.of(
                                    "onClick",
                                    parent,
                                    "onClickCapture",
                                    capture,
                                    "children",
                                    host("button", JsPropertyMap.of("onClick", stop)))),
                    root);
            trace.setLength(0);
            mouseButton(disabled, 0);
            check("capture stop ".contentEquals(trace), "Stopping in bubble preserves completed capture");

            final ReactEventHandler textTarget = event -> {
                check(
                        "SPAN".equals(Browser.property(event.getTarget(), "tagName")),
                        "Text targets are retargeted to their element");
                trace.append("text ");
            };
            ReactDOM.render(host("span", JsPropertyMap.of("children", "text", "onClick", textTarget)), root);
            textClick(Browser.query(root, "span"));
            check("capture stop text ".contentEquals(trace), "Clicks from text reach the host handler");

            ReactDOM.render(host("button", JsPropertyMap.of()), root);
            final var late = Browser.query(root, "button");
            trace.setLength(0);
            mouseButton(late, 0);
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", button)), root);
            mouseButton(late, 0);
            ReactDOM.render(host("button", JsPropertyMap.of()), root);
            mouseButton(late, 0);
            ReactDOM.render(host("button", JsPropertyMap.of("onClick", parent)), root);
            mouseButton(late, 0);
            check("button parent ".contentEquals(trace), "Handlers can be added, removed and added again");

            final var audio = host("audio", JsPropertyMap.of());
            ReactDOM.render(host("section", JsPropertyMap.of("children", audio)), root);
            final var media = Browser.query(root, "audio");
            trace.setLength(0);
            dispatch(media, "onProgress");
            ReactDOM.render(host("section", JsPropertyMap.of("children", audio, "onProgressCapture", parent)), root);
            dispatch(media, "onProgress");
            check("parent ".contentEquals(trace), "Late capture reaches an already mounted media element");
            formAndMediaEvents(root);

            final ReactEventHandler selected =
                    event -> trace.append(event.getType()).append(' ');
            final var editable =
                    host("p", JsPropertyMap.of("contentEditable", Browser.bool(true), "children", "select text"));
            ReactDOM.render(host("section", JsPropertyMap.of("onSelectCapture", selected, "children", editable)), root);
            final var content = Browser.query(root, "p");
            focus(content);
            trace.setLength(0);
            editableSelection(content);
            check("select ".contentEquals(trace), "ContentEditable selections reach ancestor capture");
            editableSelection(content);
            check("select ".contentEquals(trace), "Unchanged content selection stays quiet");
            Browser.mouse(content, "mousedown", false);
            Browser.mouse(content, "contextmenu", false);
            check("select ".contentEquals(trace), "Context menu ends drag without repeating the selection");

            trace.setLength(0);
            final ReactEventHandler nativeTypes =
                    event -> trace.append(event.getType()).append(' ');
            final var props = JsPropertyMap.<@Nullable Object>of(
                    "onAnimationEnd", nativeTypes, "onTransitionEnd", nativeTypes, "onBlur", nativeTypes);
            props.set("onCancel", nativeTypes);
            props.set("onClose", nativeTypes);
            ReactDOM.render(host("dialog", props), root);
            final var dialog = Browser.query(root, "dialog");
            dispatch(dialog, "onAnimationEnd");
            dispatch(dialog, "onTransitionEnd");
            dispatch(dialog, "onCancel");
            dispatch(dialog, "onClose");
            dispatch(dialog, "onBlur");
            check(
                    "animationend transitionend cancel close blur ".contentEquals(trace),
                    "Animation, transition, dialog and blur events dispatch their native types");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void mediaOnlyDocumentListeners() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler record = event -> trace.append(event.getType());
            final var media =
                    JsPropertyMap.<@Nullable Object>of("onPlay", record, "onPause", record, "onProgress", record);
            final var events = observeDocumentListeners(root, () -> {
                ReactDOM.render(
                        ReactNode.of(
                                host("video", media),
                                host("audio", media),
                                host("form", JsPropertyMap.of("onReset", record, "onSubmit", record))),
                        root);
                dispatch(Browser.query(root, "video"), "onPlay");
            });
            check("".equals(events), "A media-only tree avoids document listeners, saw " + events);
            check("play".contentEquals(trace), "The media host receives its native event");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void formAndMediaEvents(final Browser.Element root) {
        final var trace = new StringBuilder();
        final ReactEventHandler record = event -> trace.append(event.getType()).append(' ');
        ReactDOM.render(
                host(
                        "section",
                        JsPropertyMap.of(
                                "children",
                                host(
                                        "form",
                                        JsPropertyMap.of(
                                                "onSubmit", record,
                                                "onReset", record,
                                                "children", host("input", JsPropertyMap.of("onInvalid", record)))))),
                root);
        final var intermediate = Browser.query(root, "section");
        stopNativePropagation(intermediate, "submit");
        stopNativePropagation(intermediate, "reset");
        dispatch(Browser.query(root, "input"), "onInvalid");
        final var form = Browser.query(root, "form");
        nativeInputEvent(form, "reset");
        nativeInputEvent(form, "submit");
        nativeInputEvent(form, "submit");
        check(
                "invalid reset submit submit ".contentEquals(trace),
                "Each form event dispatches once at the form or input");

        trace.setLength(0);
        ReactDOM.render(
                ReactNode.of(
                        host("img", JsPropertyMap.of("onLoadStart", record)),
                        host("video", JsPropertyMap.of("onLoadStart", record))),
                root);
        dispatch(Browser.query(root, "img"), "onLoadStart");
        check(0 == trace.length(), "An image does not trap loadstart");
        dispatch(Browser.query(root, "video"), "onLoadStart");
        check("loadstart ".contentEquals(trace), "A video traps its non-bubbling loadstart event");
    }

    @JsMethod
    private static native void mouseButton(Browser.Element element, int button);

    @JsMethod
    private static native void legacyMovement(Browser.Element element, String type, int x, int y);

    @JsMethod
    private static native void textClick(Browser.Element element);

    @JsMethod
    private static native void editableSelection(Browser.Element element);

    @JsMethod
    public static void runStockKeyboardEvents() {
        final var root = Browser.append("div");
        try {
            final var events = new ArrayList<KeyboardEvent>();
            final ReactEventHandler handler = event -> {
                final var key = Js.<KeyboardEvent>uncheckedCast(event);
                check(!key.isDefaultPrevented(), "Key event begins uncanceled");
                key.preventDefault();
                check(key.isDefaultPrevented(), "Key event supports preventing default");
                check(!key.isPropagationStopped(), "Key event begins with propagation enabled");
                key.stopPropagation();
                check(key.isPropagationStopped(), "Key event supports stopping propagation");
                persistEvent(key);
                events.add(key);
            };
            ReactDOM.render(
                    host("input", JsPropertyMap.of("onKeyDown", handler, "onKeyUp", handler, "onKeyPress", handler)),
                    root);
            final var input = Browser.query(root, "input");
            stockKey(input, "keypress", "", 13, 0, false);
            stockKey(input, "keypress", "", 0, 32, false);
            stockKey(input, "keypress", "", 0, 13, false);
            check(3 == events.size(), "Three printable keypresses dispatch");
            checkKeyPress(events.get(0), 13);
            checkKeyPress(events.get(1), 32);
            checkKeyPress(events.get(2), 13);
            stockKey(input, "keypress", "", 0, 31, false);
            check(3 == events.size(), "A non-printable charCode does not dispatch");
            stockKey(input, "keypress", "", 0, 10, false);
            stockKey(input, "keypress", "", 0, 10, true);
            checkKeyPress(events.get(3), 13);
            checkKeyPress(events.get(4), 13);
            legacyKey(input, "keypress", 32);
            legacyKey(input, "keypress", 13);
            checkKeyPress(events.get(5), 32);
            checkKeyPress(events.get(6), 13);
            legacyKey(input, "keypress", 31);
            check(7 == events.size(), "A legacy non-printable keyCode does not dispatch");

            events.clear();
            stockKey(input, "keydown", "Del", 0, 0, false);
            stockKey(input, "keydown", "", 0, 31, false);
            stockKey(input, "keydown", "", 40, 0, false);
            stockKey(input, "keyup", "Del", 0, 0, false);
            stockKey(input, "keyup", "", 40, 0, false);
            check(5 == events.size(), "All five keydown and keyup events dispatch");
            for (final var event : events) {
                check(0 == event.getCharCode(), "Only keypress has a charCode");
                check(event.getKeyCode() == event.getWhich(), "Keydown and keyup which equals keyCode");
            }
            check(40 == events.get(2).getKeyCode(), "Keydown preserves keyCode");
            check(40 == events.get(4).getKeyCode(), "Keyup preserves keyCode");
            equal("Delete", events.get(0).getKey());

            events.clear();
            stockKey(input, "keydown", "f", 0, 0, false);
            stockKey(input, "keypress", "", 0, 13, false);
            stockKey(input, "keypress", "", 0, 65, false);
            stockKey(input, "keydown", "", 45, 0, false);
            stockKey(input, "keydown", "", 1337, 0, false);
            check(5 == events.size(), "Each key-name scenario dispatches");
            equal("f", events.get(0).getKey());
            equal("Enter", events.get(1).getKey());
            equal("A", events.get(2).getKey());
            equal("Insert", events.get(3).getKey());
            equal("Unidentified", events.get(4).getKey());
            checkKeyPress(events.get(2), 65);

            events.clear();
            stockKey(input, "keydown", "", 40, 0, false);
            stockKey(input, "keyup", "", 40, 0, false);
            stockKey(input, "keypress", "", 40, 40, false);
            check(3 == events.size(), "All three key event types were canceled, stopped and persisted");
            equal("keydown", events.get(0).getType());
            equal("keyup", events.get(1).getType());
            equal("keypress", events.get(2).getType());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void checkKeyPress(KeyboardEvent event, int charCode) {
        check(charCode == event.getCharCode(), "Keypress normalized charCode " + charCode);
        check(0 == event.getKeyCode(), "Keypress keyCode is zero");
        check(charCode == event.getWhich(), "Keypress which equals charCode");
    }

    @JsMethod
    private static native void stockKey(
            Browser.Element input, String type, String key, int keyCode, int charCode, boolean ctrlKey);

    @JsMethod
    public static void runKeyboardVariants(final boolean production) {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final SyntheticEvent<?>[] released = new SyntheticEvent<?>[1];
            final ReactEventHandler keyboard = event -> {
                final var key = Js.<KeyboardEvent>uncheckedCast(event);
                trace.append(key.getKey())
                        .append('/')
                        .append(key.getCharCode())
                        .append('/')
                        .append(key.getKeyCode())
                        .append('/')
                        .append(key.getWhich())
                        .append(' ');
                if ("keydown".equals(key.getType()) && "A".equals(key.getKey())) {
                    check(key.getModifierState("Shift"), "Keyboard exposes native modifier state");
                    check(!key.getModifierState("Alt"), "An unset native modifier is false");
                }
                if ("keyup".equals(key.getType())) {
                    for (final var modifier : new String[] {"Alt", "Control", "Meta", "Shift"}) {
                        check(key.getModifierState(modifier), "Fallback modifier " + modifier);
                    }
                    check(!key.getModifierState("CapsLock"), "Fallback ignores lock modifiers");
                }
                if ("keypress".equals(key.getType())) {
                    check(
                            Js.isTripleEqual(Browser.number(0), Browser.property(key, "keyCode")),
                            "Keypress keyCode is the JavaScript number zero");
                    check(
                            Js.isTripleEqual(Browser.number(key.getCharCode()), Browser.property(key, "which")),
                            "Keypress which is the JavaScript character code");
                }
                released[0] = event;
            };
            ReactDOM.render(
                    host("input", JsPropertyMap.of("onKeyDown", keyboard, "onKeyUp", keyboard, "onKeyPress", keyboard)),
                    root);
            final var input = Browser.query(root, "input");
            keyboard(input, "keydown", "A", 65, 0);
            keyboard(input, "keydown", "", 112, 0);
            keyboard(input, "keydown", "MozPrintableKey", 222, 0);
            keyboard(input, "keypress", "", 0, 65);
            keyboard(input, "keypress", "", 13, 0);
            keyboard(input, "keypress", "", 0, 10);
            keyboard(input, "keypress", "", 0, 13);
            keyboard(input, "keypress", "", 0, 9);
            legacyKey(input, "keypress", 13);
            legacyKey(input, "keyup", 46);
            check(
                    ("A/0/65/65 F1/0/112/112 Unidentified/0/222/222 A/65/0/65 "
                                    + "Enter/13/0/13 Enter/13/0/13 Enter/13/0/13 Enter/13/0/13 Delete/0/46/46 ")
                            .contentEquals(trace),
                    "Keyboard normalizes both modern and legacy events");

            final var event = Objects.requireNonNull(released[0]);
            final int warnings = releasedWarnings(() -> {
                check(Browser.same(null, event.getTarget()), "Released events clear their DOM target");
                check(!event.isDefaultPrevented(), "Released events reset their default prevention state");
            });
            check((production ? 0 : 2) == warnings, "Reading a released event warns only in development");

            checkKey(input, trace, "Esc", 0, "Escape");
            checkKey(input, trace, "Spacebar", 0, " ");
            checkKey(input, trace, "Left", 0, "ArrowLeft");
            checkKey(input, trace, "Up", 0, "ArrowUp");
            checkKey(input, trace, "Right", 0, "ArrowRight");
            checkKey(input, trace, "Down", 0, "ArrowDown");
            checkKey(input, trace, "Del", 0, "Delete");
            checkKey(input, trace, "Win", 0, "OS");
            checkKey(input, trace, "Menu", 0, "ContextMenu");
            checkKey(input, trace, "Apps", 0, "ContextMenu");
            checkKey(input, trace, "Scroll", 0, "ScrollLock");
            checkKey(input, trace, "f", 70, "f");
            checkKey(input, trace, "MozPrintableKey", 45, "Insert");
            checkKey(input, trace, "Unidentified", 36, "Home");

            checkKey(input, trace, "", 8, "Backspace");
            checkKey(input, trace, "", 9, "Tab");
            checkKey(input, trace, "", 13, "Enter");
            checkKey(input, trace, "", 16, "Shift");
            checkKey(input, trace, "", 17, "Control");
            checkKey(input, trace, "", 18, "Alt");
            checkKey(input, trace, "", 20, "CapsLock");
            checkKey(input, trace, "", 32, " ");
            checkKey(input, trace, "", 33, "PageUp");
            checkKey(input, trace, "", 34, "PageDown");
            checkKey(input, trace, "", 35, "End");
            checkKey(input, trace, "", 40, "ArrowDown");
            checkKey(input, trace, "", 45, "Insert");
            checkKey(input, trace, "", 123, "F12");
            checkKey(input, trace, "", 1337, "Unidentified");

            trace.setLength(0);
            legacyKey(input, "keyup", 224);
            equal("Meta/0/224/224 ", trace.toString());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void checkKey(Browser.Element input, StringBuilder trace, String key, int keyCode, String expected) {
        trace.setLength(0);
        keyboard(input, "keydown", key, keyCode, 0);
        equal(expected + "/0/" + keyCode + "/" + keyCode + " ", trace.toString());
    }

    @JsFunction
    interface Probe {
        void run();
    }

    @JsFunction
    private interface FlagProbe {
        boolean read();
    }

    @JsMethod
    private static native int releasedWarnings(Probe probe);

    @JsMethod
    static native String captureWarnings(Probe probe);

    @JsMethod
    private static native String clickError(Browser.Element target);

    @JsMethod
    private static native int inputValueWrites(Browser.Element element, Probe probe);

    @JsMethod
    private static native void keyboard(Browser.Element element, String type, String key, int keyCode, int charCode);

    @JsMethod
    private static native void legacyKey(Browser.Element element, String type, int keyCode);

    @JsMethod
    public static void runUncontrolled() {
        final var root = Browser.append("div");
        try {
            formDefaults(root);
            ReactDOM.render(host("input", JsPropertyMap.of("defaultValue", "baseline")), root);
            final var input = Browser.query(root, "input");
            check("baseline".equals(input.getValue()), "Default input value initializes the live value");
            Browser.setValue(input, "edited");
            ReactDOM.render(host("input", JsPropertyMap.of("defaultValue", "updated")), root);
            check("edited".equals(input.getValue()), "Updating the input default preserves a user edit");
            attribute(input, "value", "updated");

            ReactDOM.render(host("textarea", JsPropertyMap.of("defaultValue", "baseline")), root);
            final var textarea = Browser.query(root, "textarea");
            check("baseline".equals(textarea.getValue()), "Default textarea value initializes the live value");
            Browser.setValue(textarea, "edited");
            ReactDOM.render(host("textarea", JsPropertyMap.of("defaultValue", "updated")), root);
            check("edited".equals(textarea.getValue()), "Updating the textarea default preserves a user edit");
            check("updated".equals(Browser.property(textarea, "defaultValue")), "Textarea updates its reset default");

            final var legacyTextarea = ReactElement.createHostElement(
                    "textarea", "legacy", null, JsPropertyMap.of("children", ReactNode.of("initial text")));
            ReactDOM.render(legacyTextarea, root);
            final var legacy = Browser.query(root, "textarea");
            check("initial text".equals(legacy.getValue()), "Textarea children seed its mount value");
            ReactDOM.render(
                    ReactElement.createHostElement("textarea", "legacy", null, JsPropertyMap.of("children", "ignored")),
                    root);
            check("initial text".equals(legacy.getValue()), "Textarea ignores child updates after mount");
            check("initial text".equals(legacy.getTextContent()), "Textarea retains the initial reset text");

            final var trace = new StringBuilder();
            final ReactEventHandler changed = event -> trace.append(Browser.property(event.getTarget(), "checked"));
            ReactDOM.render(
                    host(
                            "input",
                            JsPropertyMap.of(
                                    "type", "checkbox", "defaultChecked", Browser.bool(true), "onChange", changed)),
                    root);
            final var checkbox = Browser.query(root, "input");
            check(Boolean.TRUE.equals(Browser.property(checkbox, "checked")), "Default checked initializes a checkbox");
            attribute(checkbox, "checked", "");
            checkbox.click();
            check("false".contentEquals(trace), "Checkbox click exposes the user's changed state");
            check(Boolean.FALSE.equals(Browser.property(checkbox, "checked")), "Uncontrolled checkbox keeps the edit");
            ReactDOM.render(
                    host("input", JsPropertyMap.of("type", "checkbox", "defaultChecked", Browser.bool(false))), root);
            attribute(checkbox, "checked", null);

            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input",
                            "controlled",
                            null,
                            JsPropertyMap.of(
                                    "type", "checkbox", "checked", Browser.bool(true), "readOnly", Browser.bool(true))),
                    root);
            final var transitioning = Browser.query(root, "input");
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input",
                            "controlled",
                            null,
                            JsPropertyMap.of(
                                    "type",
                                    "checkbox",
                                    "checked",
                                    Browser.bool(false),
                                    "readOnly",
                                    Browser.bool(true))),
                    root);
            check(Boolean.FALSE.equals(Browser.property(transitioning, "checked")), "Controlled checked updates");
            ReactDOM.render(
                    ReactElement.createHostElement("input", "controlled", null, JsPropertyMap.of("type", "checkbox")),
                    root);
            check(
                    Boolean.TRUE.equals(Browser.property(transitioning, "checked")),
                    "Removing checked restores the original checked value");

            final ReactEventHandler ignore = event -> {};
            for (final var type : new String[] {"submit", "reset"}) {
                ReactDOM.render(
                        ReactElement.createHostElement(
                                "input", type, null, JsPropertyMap.of("type", type, "value", "", "onChange", ignore)),
                        root);
                final var button = Browser.query(root, "input");
                attribute(button, "value", "");
                ReactDOM.render(
                        ReactElement.createHostElement(
                                "input",
                                type,
                                null,
                                JsPropertyMap.of("type", type, "value", "Custom", "onChange", ignore)),
                        root);
                attribute(button, "value", "Custom");
                ReactDOM.render(
                        ReactElement.createHostElement(
                                "input", type, null, JsPropertyMap.of("type", type, "defaultValue", "unused")),
                        root);
                attribute(button, "value", null);
            }

            ReactDOM.render(select(null, "b", false, changed), root);
            final var select = Browser.query(root, "select");
            check("b".equals(select.getValue()), "Select uses the mount default");
            ReactDOM.render(select(null, null, true, changed), root);
            check("".equals(select.getValue()), "Enabling multiple clears an uncontrolled select without a default");
            ReactDOM.render(select(null, null, false, changed), root);
            check("a".equals(select.getValue()), "Disabling multiple selects the first available option");

            final var disabled = host(
                    "option",
                    JsPropertyMap.of("value", "disabled", "disabled", Browser.bool(true), "children", "disabled"));
            final var grouped = host("optgroup", JsPropertyMap.of("label", "group", "children", option("__proto__")));
            ReactDOM.render(
                    host(
                            "select",
                            JsPropertyMap.of(
                                    "value",
                                    "missing",
                                    "onChange",
                                    ignore,
                                    "children",
                                    ReactNode.of(disabled, grouped))),
                    root);
            check("__proto__".equals(select.getValue()), "A missing value falls back to the first enabled option");
            final var multiple = JsPropertyMap.<@Nullable Object>of(
                    "multiple", Browser.bool(true), "value", new String[] {"__proto__"}, "onChange", ignore);
            multiple.set("children", ReactNode.of(option("constructor"), grouped));
            ReactDOM.render(host("select", multiple), root);
            check(
                    "__proto__".equals(select.getValue()),
                    "Multiple select matches only the requested prototype-like value");
            check(
                    Boolean.FALSE.equals(Browser.property(Browser.query(select, "[value=constructor]"), "selected")),
                    "Multiple select leaves other prototype-like values unselected");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runHostProperties() {
        final var root = Browser.append("div");
        try {
            withoutReportedErrors(() -> invalidHostProps(root));
            final var html = JsPropertyMap.<@Nullable Object>of(
                    "id", "property-fixture", "className", "before", "contentEditable", Browser.bool(false));
            html.set("hidden", Browser.bool(true));
            html.set("data-flag", Browser.bool(false));
            html.set("aria-hidden", Browser.bool(true));
            html.set("title", "title");
            html.set("dangerouslySetInnerHTML", JsPropertyMap.of("__html", "<i>raw</i>"));
            final var styles = JsPropertyMap.<@Nullable Object>of(
                    "width", Browser.number(12), "opacity", Browser.number(0.5), "--swatch", "red");
            styles.set("marginTop", Browser.number(0));
            styles.set("cssFloat", "right");
            styles.set("WebkitMaskSize", "contain");
            html.set("style", styles);
            ReactDOM.render(host("section", html), root);
            final var section = Browser.query(root, "#property-fixture");
            attribute(section, "class", "before");
            attribute(section, "contenteditable", "false");
            attribute(section, "hidden", "");
            attribute(section, "data-flag", "false");
            attribute(section, "aria-hidden", "true");
            check("raw".equals(Browser.query(section, "i").getTextContent()), "Raw markup mounts");
            check("12px".equals(styleProperty(section, "width")), "Numeric length gains a px suffix");
            check("0.5".equals(styleProperty(section, "opacity")), "Numeric opacity has no suffix");
            check("0px".equals(styleProperty(section, "margin-top")), "Zero is accepted as a length");
            check("red".equals(styleProperty(section, "--swatch")), "Custom style is set");
            check("right".equals(styleProperty(section, "float")), "Float property is set");
            check("contain".equals(styleProperty(section, "-webkit-mask-size")), "Vendor-prefixed style is set");

            final var nextStyles = JsPropertyMap.<@Nullable Object>of(
                    "width", Browser.number(12), "opacity", Browser.bool(false), "--swatch", null);
            final var nextHtml = JsPropertyMap.<@Nullable Object>of(
                    "style",
                    nextStyles,
                    "hidden",
                    Browser.bool(false),
                    "dangerouslySetInnerHTML",
                    JsPropertyMap.of("__html", "<b>next</b>"));
            ReactDOM.render(host("section", nextHtml), root);
            attribute(section, "class", null);
            attribute(section, "hidden", null);
            attribute(section, "title", null);
            check("12px".equals(styleProperty(section, "width")), "Unchanged style value stays set");
            check("".equals(styleProperty(section, "opacity")), "Boolean style clears the value");
            check("".equals(styleProperty(section, "--swatch")), "Null custom style clears the value");
            check("".equals(styleProperty(section, "float")), "Omitted style clears the value");
            check(
                    "next".equals(Browser.query(section, "b").getTextContent()),
                    "Changed raw markup replaces its contents");
            ReactDOM.render(host("section", JsPropertyMap.of("children", ReactNode.of("child"))), root);
            check("child".equals(section.getTextContent()), "Children replace raw markup");

            final var inherited = inheritedProps(JsPropertyMap.of("title", "inherited title", "id", "inherited"));
            inherited.set("title", "own title");
            inherited.set("style", inheritedProps(JsPropertyMap.of("width", "40px")));
            ReactDOM.render(host("section", inherited), root);
            attribute(section, "title", "own title");
            attribute(section, "id", null);
            check("".equals(styleProperty(section, "width")), "Inherited styles are ignored");
            ReactDOM.render(host("section", inheritedProps(JsPropertyMap.of("title", "inherited title"))), root);
            attribute(section, "title", null);

            final var unknown = JsPropertyMap.<@Nullable Object>of(
                    "constructor", "attribute", "ÉCase", "yes", "invalid name", "ignored");
            ReactDOM.render(host("section", unknown), root);
            attribute(section, "constructor", "attribute");
            attribute(section, "Écase", "yes");
            attribute(section, "écase", null);
            attribute(section, "invalid name", null);
            ReactDOM.render(host("section", JsPropertyMap.of("invalid name", "still ignored")), root);
            attribute(section, "constructor", null);
            attribute(section, "Écase", null);

            final var rawNamespaced = JsPropertyMap.<@Nullable Object>of("xlink:href", "#raw", "xml:lang", "fr");
            rawNamespaced.set("panose1", "1");
            rawNamespaced.set("transformOrigin", "0 0");
            rawNamespaced.set("crossOrigin", "anonymous");
            ReactDOM.render(host("svg", JsPropertyMap.of("children", host("use", rawNamespaced))), root);
            final var rawUse = Browser.query(root, "use");
            attribute(rawUse, "panose1", "1");
            attribute(rawUse, "panose-1", null);
            attribute(rawUse, "transformOrigin", "0 0");
            attribute(rawUse, "transform-origin", null);
            attribute(rawUse, "crossOrigin", "anonymous");
            attribute(rawUse, "crossorigin", null);
            attribute(rawUse, "xlink:href", "#raw");
            attribute(rawUse, "xml:lang", "fr");
            check(null == attributeIn(rawUse, "http://www.w3.org/1999/xlink", "href"), "Raw XLink name");
            check(null == attributeIn(rawUse, "http://www.w3.org/XML/1998/namespace", "lang"), "Raw XML name");
            ReactDOM.render(host("svg", JsPropertyMap.of("children", host("use", JsPropertyMap.of()))), root);
            attribute(rawUse, "xlink:href", null);
            attribute(rawUse, "xml:lang", null);

            final var svgUse = JsPropertyMap.<@Nullable Object>of(
                    "xlinkHref", "#shape", "strokeWidth", Browser.number(3), "xmlLang", "en");
            svgUse.set("tabIndex", Browser.number(0));
            svgUse.set("focusable", Browser.bool(false));
            final var xlinkNames = new String[][] {
                {"xlinkActuate", "actuate", "onLoad"},
                {"xlinkArcrole", "arcrole", "urn:test:arcrole"},
                {"xlinkRole", "role", "urn:test:role"},
                {"xlinkShow", "show", "new"},
                {"xlinkTitle", "title", "Reusable shape"},
                {"xlinkType", "type", "simple"}
            };
            for (final var attribute : xlinkNames) {
                svgUse.set(attribute[0], attribute[2]);
            }
            svgUse.set("xmlBase", "/shapes/");
            svgUse.set("xmlSpace", "preserve");
            svgUse.set("id", "shape-use");
            final var foreign = host(
                    "foreignObject", JsPropertyMap.of("children", host("div", JsPropertyMap.of("id", "embedded"))));
            ReactDOM.render(
                    host(
                            "svg",
                            JsPropertyMap.of(
                                    "children", ReactNode.of(host("use", svgUse), foreign),
                                    "xmlnsXlink", "http://www.w3.org/1999/xlink",
                                    "viewBox", "0 0 64 64")),
                    root);
            final var use = Browser.query(root, "use");
            attribute(use, "stroke-width", "3");
            attribute(use, "tabindex", "0");
            attribute(use, "focusable", "false");
            attribute(use, "id", "shape-use");
            final var svg = Browser.query(root, "svg");
            attribute(svg, "viewBox", "0 0 64 64");
            attribute(svg, "xmlns:xlink", "http://www.w3.org/1999/xlink");
            for (final var attribute : xlinkNames) {
                check(
                        attribute[2].equals(attributeIn(use, "http://www.w3.org/1999/xlink", attribute[1])),
                        "XLink attribute " + attribute[1]);
            }
            check("/shapes/".equals(attributeIn(use, "http://www.w3.org/XML/1998/namespace", "base")), "XML base");
            check("preserve".equals(attributeIn(use, "http://www.w3.org/XML/1998/namespace", "space")), "XML space");
            check("#shape".equals(attributeIn(use, "http://www.w3.org/1999/xlink", "href")), "XLink namespace");
            check("en".equals(attributeIn(use, "http://www.w3.org/XML/1998/namespace", "lang")), "XML namespace");
            check("http://www.w3.org/2000/svg".equals(Browser.property(use, "namespaceURI")), "SVG namespace");
            check(
                    "http://www.w3.org/1999/xhtml"
                            .equals(Browser.property(Browser.query(root, "#embedded"), "namespaceURI")),
                    "ForeignObject children return to HTML");
            ReactDOM.render(host("svg", JsPropertyMap.of("children", host("use", JsPropertyMap.of()))), root);
            check(null == attributeIn(use, "http://www.w3.org/1999/xlink", "href"), "Removed XLink attribute");
            check(null == attributeIn(use, "http://www.w3.org/XML/1998/namespace", "lang"), "Removed XML attribute");

            ReactDOM.render(
                    ReactNode.of(
                            host(
                                    "math",
                                    JsPropertyMap.of(
                                            "children",
                                            ReactNode.of(
                                                    host("mi", JsPropertyMap.of("children", "x", "mixedCase", "yes")),
                                                    host("svg", JsPropertyMap.of("id", "inherited-namespace"))))),
                            host("a", JsPropertyMap.of("download", Browser.bool(true))),
                            host("img", JsPropertyMap.of("crossOrigin", "anonymous")),
                            host("input", JsPropertyMap.of("capture", "environment")),
                            host("input", JsPropertyMap.of("capture", Browser.bool(false), "id", "capture-off")),
                            host("form", JsPropertyMap.of("acceptCharset", "UTF-8")),
                            host("label", JsPropertyMap.of("htmlFor", "control")),
                            host("meta", JsPropertyMap.of("httpEquiv", "refresh")),
                            host("textarea", JsPropertyMap.of("cols", Browser.number(0), "rows", Browser.number(3))),
                            host("ol", JsPropertyMap.of("start", "invalid")),
                            host("ol", JsPropertyMap.of("start", Browser.number(-3), "id", "ordered"))),
                    root);
            check(
                    "http://www.w3.org/1998/Math/MathML"
                            .equals(Browser.property(Browser.query(root, "mi"), "namespaceURI")),
                    "MathML namespace");
            check(
                    "http://www.w3.org/1998/Math/MathML"
                            .equals(Browser.property(Browser.query(root, "#inherited-namespace"), "namespaceURI")),
                    "Nested names inherit the MathML namespace");
            attribute(Browser.query(root, "mi"), "mixedCase", "yes");
            attribute(Browser.query(root, "mi"), "mixedcase", null);
            attribute(Browser.query(root, "a"), "download", "");
            attribute(Browser.query(root, "img"), "crossorigin", "anonymous");
            attribute(Browser.query(root, "input"), "capture", "environment");
            attribute(Browser.query(root, "#capture-off"), "capture", null);
            attribute(Browser.query(root, "#ordered"), "start", "-3");
            attribute(Browser.query(root, "form"), "accept-charset", "UTF-8");
            attribute(Browser.query(root, "label"), "for", "control");
            attribute(Browser.query(root, "meta"), "http-equiv", "refresh");
            attribute(Browser.query(root, "textarea"), "cols", null);
            attribute(Browser.query(root, "textarea"), "rows", "3");
            attribute(Browser.query(root, "ol"), "start", null);

            ReactDOM.render(
                    host("script", JsPropertyMap.of("children", "document.currentScript.dataset.executed = 'yes'")),
                    root);
            attribute(Browser.query(root, "script"), "data-executed", null);

            final var custom = JsPropertyMap.<@Nullable Object>of(
                    "className", "sample", "hidden", Browser.bool(true), "oncustom", "value");
            custom.set("selected", Browser.bool(true));
            custom.set("onClick", (ReactEventHandler) event -> {});
            custom.set("invalid name", "ignored");
            ReactDOM.render(host("example-host", custom), root);
            final var customElement = Browser.query(root, "example-host");
            attribute(customElement, "class", null);
            attribute(customElement, "classname", "sample");
            attribute(customElement, "hidden", "true");
            attribute(customElement, "selected", "true");
            attribute(customElement, "oncustom", "value");
            attribute(customElement, "onclick", null);

            ReactDOM.render(host("example-host", JsPropertyMap.of("hidden", Browser.bool(false))), root);
            attribute(customElement, "classname", null);
            attribute(customElement, "hidden", "false");
            attribute(customElement, "selected", null);
            attribute(customElement, "oncustom", null);
            ReactDOM.render(host("example-host", JsPropertyMap.of()), root);
            attribute(customElement, "hidden", null);

            ReactDOM.render(
                    host(
                            "svg",
                            JsPropertyMap.of(
                                    "children",
                                    host(
                                            "font-face",
                                            JsPropertyMap.of(
                                                    "fontFamily", "sans-serif", "hidden", Browser.bool(false))))),
                    root);
            final var fontFace = Browser.query(root, "font-face");
            attribute(fontFace, "font-family", "sans-serif");
            attribute(fontFace, "fontFamily", null);
            attribute(fontFace, "hidden", null);

            ReactDOM.render(
                    ReactNode.of(
                            host("TEXTAREA", JsPropertyMap.of("defaultValue", "unused")),
                            host("INPUT", JsPropertyMap.of("type", "checkbox", "checked", Browser.bool(true)))),
                    root);
            check("".equals(Browser.query(root, "textarea").getValue()), "Mixed-case textarea bypasses defaultValue");
            final var uppercaseCheckbox = Browser.query(root, "input");
            check(Boolean.TRUE.equals(Browser.property(uppercaseCheckbox, "checked")), "Mixed-case input sets checked");
            attribute(uppercaseCheckbox, "checked", null);

            final var observed = new StringBuilder();
            final ReactEventHandler upperChange =
                    event -> observed.append(Browser.property(event.getTarget(), "value"));
            ReactDOM.render(host("INPUT", JsPropertyMap.of("value", "first", "onChange", upperChange)), root);
            final var uppercaseInput = Browser.query(root, "input");
            writeValue(uppercaseInput, "typed");
            check("typed".contentEquals(observed), "Mixed-case input still dispatches native changes");
            ReactDOM.render(host("INPUT", JsPropertyMap.of("value", "next", "onChange", upperChange)), root);
            attribute(uppercaseInput, "value", "next");
            check("typed".equals(uppercaseInput.getValue()), "Mixed-case input leaves its live value uncontrolled");

            ReactDOM.render(
                    host(
                            "svg",
                            JsPropertyMap.of(
                                    "children",
                                    host(
                                            "foreignobject",
                                            JsPropertyMap.of(
                                                    "children",
                                                    host("span", JsPropertyMap.of("id", "svg-descendant")))))),
                    root);
            check(
                    "http://www.w3.org/2000/svg"
                            .equals(Browser.property(Browser.query(root, "#svg-descendant"), "namespaceURI")),
                    "Only exact foreignObject casing changes the SVG child namespace");

            beginMountProbe();
            ReactDOM.render(
                    host(
                            "react4j-mount-parent",
                            JsPropertyMap.of(
                                    "data-probe",
                                    "parent",
                                    "children",
                                    host(
                                            "react4j-mount-child",
                                            JsPropertyMap.of("data-probe", "child", "children", "text")))),
                    root);
            check(
                    ("child:construct:false child:attribute:0:false parent:construct:false "
                                    + "parent:attribute:1:false parent:connected:true child:connected:true")
                            .equals(mountProbeTrace()),
                    "New custom elements initialize after their detached descendants and connect during insertion");

            hostUpdateOrder(root);
            registerCustomizedButton();
            final var clickTrace = new StringBuilder();
            final ReactEventHandler customClick = event -> clickTrace.append("click");
            ReactDOM.render(
                    host(
                            "button",
                            JsPropertyMap.of(
                                    "is",
                                    "react4j-test-button",
                                    "disabled",
                                    Browser.bool(false),
                                    "onClick",
                                    customClick)),
                    root);
            final var customizedButton = Browser.query(root, "button");
            attribute(customizedButton, "data-upgraded", "yes");
            attribute(customizedButton, "disabled", "false");
            check(
                    Boolean.TRUE.equals(Browser.property(customizedButton, "disabled")),
                    "The disabled attribute is present");
            mouseButton(customizedButton, 0);
            check("click".contentEquals(clickTrace), "Synthetic handlers use the rendered disabled value");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void invalidHostProps(final Browser.Element root) {
        final var rawHtml = JsPropertyMap.of("__html", "<em>markup</em>");
        checkRejected(() -> ReactDOM.render(host("input", JsPropertyMap.of("children", "text")), root));
        checkRejected(
                () -> ReactDOM.render(host("menuitem", JsPropertyMap.of("dangerouslySetInnerHTML", rawHtml)), root));
        checkRejected(() -> ReactDOM.render(host("div", JsPropertyMap.of("dangerouslySetInnerHTML", "markup")), root));
        ReactDOM.render(host("div", JsPropertyMap.of("children", "before")), root);
        checkRejected(() -> ReactDOM.render(
                host("div", JsPropertyMap.of("children", "text", "dangerouslySetInnerHTML", rawHtml)), root));
        ReactDOM.render(host("div", JsPropertyMap.of("children", "again")), root);
        checkRejected(() -> ReactDOM.render(host("div", JsPropertyMap.of("style", "display:none")), root));
        ReactDOM.render(
                host("div", JsPropertyMap.of("dangerouslySetInnerHTML", JsPropertyMap.of("__html", null))), root);
        check("".equals(root.getTextContent()), "An explicit null HTML value is valid");
    }

    private static void checkRejected(final Probe render) {
        boolean rejected = false;
        try {
            render.run();
        } catch (final Throwable error) {
            rejected = true;
        }
        check(rejected, "Invalid host properties are rejected");
    }

    private static void hostUpdateOrder(Browser.Element root) {
        final var differences = new StringBuilder();
        ReactDOM.render(probedParent("p0", probedChild("one", "c0", "kept")), root);
        final var parent = Browser.query(root, "react4j-mount-parent");
        final var child = Browser.query(root, "react4j-mount-child");

        beginUpdateProbe();
        ReactDOM.render(probedParent("p1", probedChild("one", "c1", "kept")), root);
        checkUpdateTrace(
                differences,
                "Child attributes update before the parent's attributes",
                "child:attribute:c1:p0:kept parent:attribute:p1:c1:kept");
        check(Browser.same(parent, Browser.query(root, "react4j-mount-parent")), "Parent host was retained");
        check(Browser.same(child, Browser.query(root, "react4j-mount-child")), "Child host was retained");

        beginUpdateProbe();
        ReactDOM.render(probedParent("p2", probedChild("two", "c2", "next")), root);
        checkUpdateTrace(
                differences,
                "Child replacement precedes the parent's attributes",
                "child:attribute:c2:-: child:disconnected:c1 child:connected:c2:p1:next parent:attribute:p2:c2:next");
        check(!Browser.same(child, Browser.query(root, "react4j-mount-child")), "A new child key replaces its host");

        ReactDOM.render(probedParent("p3", ReactNode.of("stale text")), root);
        beginUpdateProbe();
        ReactDOM.render(probedParent("p4", probedChild("three", "c3", "content")), root);
        checkUpdateTrace(
                differences,
                "Direct text is cleared before child placement",
                "child:attribute:c3:-: child:connected:c3:p3:content parent:attribute:p4:c3:content");

        ReactDOM.render(
                host(
                        "react4j-mount-parent",
                        JsPropertyMap.of(
                                "data-probe",
                                "p5",
                                "dangerouslySetInnerHTML",
                                JsPropertyMap.of("__html", "<em>stale HTML</em>"))),
                root);
        beginUpdateProbe();
        ReactDOM.render(probedParent("p6", probedChild("four", "c4", "html-next")), root);
        checkUpdateTrace(
                differences,
                "Raw HTML is cleared before child placement",
                "child:attribute:c4:-: child:connected:c4:p5:html-next parent:attribute:p6:c4:html-next");
        check(!Browser.exists(root, "em"), "Old raw HTML is gone");
        check("html-next".equals(root.getTextContent()), "The new child remains after updating parent properties");

        final var retainedDescription = probedChild("anchor", "anchor", "T");
        ReactDOM.render(probedParent("p7", ReactNode.of(retainedDescription)), root);
        final var retained = Browser.query(root, "react4j-mount-child");
        beginUpdateProbe();
        ReactDOM.render(
                probedParent(
                        "p7",
                        ReactNode.of(retainedDescription, probedChild("a", "a", "A"), probedChild("b", "b", "B"))),
                root);
        checkUpdateTrace(
                differences,
                "New sibling hosts connect in forward placement order",
                "child:attribute:a:-: child:attribute:b:-: child:connected:a:p7:TA child:connected:b:p7:TAB");
        check(
                Browser.same(retained, Browser.query(root, "react4j-mount-child")),
                "The existing prefix host was retained");
        check("TAB".equals(root.getTextContent()), "New siblings are in the requested DOM order");

        final var a = probedChild("a", "a", "A");
        final var b = probedChild("b", "b", "B");
        final RefConsumer oldRef = node -> recordUpdate(null == node ? "ref:old:detach" : "ref:old:attach");
        final var c = ReactElement.createHostElement(
                "react4j-mount-child", "c", oldRef, JsPropertyMap.of("data-probe", "c", "children", "C"));
        ReactDOM.render(probedParent("p8", ReactNode.of(a, b, c)), root);
        final var previous = Browser.queryAll(root, "react4j-mount-child");
        beginUpdateProbe();
        ReactDOM.render(probedParent("p8", ReactNode.of(c, a, b)), root);
        checkUpdateTrace(
                differences,
                "Keyed siblings move in forward placement order",
                "child:disconnected:a child:connected:a:p8:BCA child:disconnected:b child:connected:b:p8:CAB");
        final var next = Browser.queryAll(root, "react4j-mount-child");
        check(
                Browser.same(previous[2], next[0])
                        && Browser.same(previous[0], next[1])
                        && Browser.same(previous[1], next[2]),
                "Moving keyed siblings retains every DOM node");

        final RefConsumer newRef = node -> recordUpdate(null == node ? "ref:new:detach" : "ref:new:attach");
        final var movedC = ReactElement.createHostElement(
                "react4j-mount-child", "c", newRef, JsPropertyMap.of("children", "X", "data-probe", "c1"));
        final var movedA = ReactElement.createHostElement(
                "react4j-mount-child", "a", null, JsPropertyMap.of("children", "Y", "data-probe", "a1"));
        beginUpdateProbe();
        ReactDOM.render(probedParent("p9", ReactNode.of(b, movedC, movedA)), root);
        checkUpdateTrace(
                differences,
                "Each keyed host moves before its own updates",
                "ref:old:detach child:disconnected:c child:connected:c:p8:ABC child:attribute:c1:p8:X "
                        + "child:disconnected:a child:connected:a:p8:BXA child:attribute:a1:p8:Y "
                        + "parent:attribute:p9:b:BXY ref:new:attach");

        ReactDOM.render(
                host(
                        "react4j-mount-parent",
                        JsPropertyMap.of(
                                "data-probe",
                                "p10",
                                "dangerouslySetInnerHTML",
                                JsPropertyMap.of("__html", "raw text"))),
                root);
        final var text = Browser.property(parent, "firstChild");
        beginUpdateProbe();
        ReactDOM.render(
                host("react4j-mount-parent", JsPropertyMap.of("children", "direct", "data-probe", "p11")), root);
        checkUpdateTrace(
                differences,
                "Direct text updates occur at the children property position",
                "parent:attribute:p11:-:direct");
        check(
                null != text && Browser.same(text, Browser.property(parent, "firstChild")),
                "Replacing raw text with direct text preserves the lone text node");
        check(0 == differences.length(), differences.toString());
    }

    private static ReactElement probedParent(String value, ReactNode children) {
        return host("react4j-mount-parent", JsPropertyMap.of("data-probe", value, "children", children));
    }

    private static ReactElement probedChild(String key, String value, String text) {
        return ReactElement.createHostElement(
                "react4j-mount-child", key, null, JsPropertyMap.of("data-probe", value, "children", text));
    }

    private static void checkUpdateTrace(StringBuilder differences, String description, String expected) {
        final var actual = endUpdateProbe();
        if (!expected.equals(actual)) {
            differences
                    .append(description)
                    .append(", observed [")
                    .append(actual)
                    .append("]\n");
        }
    }

    @JsMethod
    private static native void beginUpdateProbe();

    @JsMethod
    private static native void recordUpdate(String value);

    @JsMethod
    private static native String endUpdateProbe();

    @JsMethod
    private static native void beginMountProbe();

    @JsMethod
    private static native String mountProbeTrace();

    @JsMethod
    private static native void registerCustomizedButton();

    @JsMethod
    private static native JsPropertyMap<@Nullable Object> inheritedProps(JsPropertyMap<@Nullable Object> prototype);

    private static ReactElement host(final String tag, final JsPropertyMap<@Nullable Object> inputs) {
        return ReactElement.createHostElement(tag, null, null, inputs);
    }

    private static void attribute(final Browser.Element element, final String name, @Nullable final String expected) {
        check(Objects.equals(expected, element.getAttribute(name)), "Attribute " + name + " should be " + expected);
    }

    @JsMethod
    private static native @Nullable String attributeIn(Browser.Element element, String namespace, String name);

    @JsMethod
    private static native String styleProperty(Browser.Element element, String name);

    private static ReactElement select(
            @Nullable final String value,
            @Nullable final Object defaultValue,
            final boolean multiple,
            final ReactEventHandler change) {
        final var props = JsPropertyMap.<@Nullable Object>of("onChange", change, "multiple", Browser.bool(multiple));
        if (null != value) {
            props.set("value", value);
        }
        if (null != defaultValue) {
            props.set("defaultValue", defaultValue);
        }
        props.set("children", ReactNode.of(new ReactNode[] {option("a"), option("b")}));
        return ReactElement.createHostElement("select", null, null, props);
    }

    private static ReactElement option(final String label) {
        return ReactElement.createHostElement(
                "option", label, null, JsPropertyMap.of("value", label, "children", ReactNode.of(label)));
    }

    @JsMethod
    private static native void writeValue(Browser.Element element, String value);

    @JsMethod
    private static native void nativeInputEvent(Browser.Element element, String type);

    @JsMethod
    private static native void stopNativePropagation(Browser.Element element, String type);

    @JsMethod
    private static native Object[] nonTextValues();

    @JsMethod
    static native void withoutReportedErrors(Probe probe);

    @JsMethod
    private static native String observeDocumentListeners(Browser.Element root, Probe probe);

    @JsMethod
    private static native void setSelection(Browser.Element input, int start, int end);

    @JsMethod
    private static native void focus(Browser.Element input);

    @JsMethod
    private static native void blur(Browser.Element input);

    @JsMethod
    private static native boolean isFocused(Browser.Element element);

    private static ReactElement radio(final String id, final String name, final boolean checked) {
        final var props = JsPropertyMap.<@Nullable Object>of("type", "radio", "name", name, "id", id);
        props.set("checked", Browser.bool(checked));
        return ReactElement.createHostElement("input", id, null, props);
    }

    @JsMethod
    private static native void setCheckedUntracked(Browser.Element element, boolean checked);

    @JsMethod
    private static native void appendExisting(Browser.Element parent, Browser.Element child);

    private static void getters(Object overlay, Object value) {
        int count = 0;
        for (var method : methods(overlay)) {
            if (!method.startsWith("m_") || !method.contains("__$devirt__")) {
                continue;
            }
            var name = method.split("__")[0].substring(2);
            String property;
            if ("isTrusted".equals(name)) {
                property = name;
            } else if (name.startsWith("get")) {
                property = name.substring(3);
            } else if (name.startsWith("is")) {
                property = name.substring(2);
            } else {
                throw new AssertionError("Unknown event accessor " + method);
            }
            property = property.substring(0, 1).toLowerCase() + property.substring(1);
            check(
                    Browser.same(invoke(overlay, method, value), Browser.property(value, property)),
                    "Native event property: " + method);
            count++;
        }
        check(0 < count, "No event accessors");
    }

    @JsFunction
    private interface Inspector {
        void inspect(Object overlay, Object value);
    }

    @JsMethod
    private static native Object dispatch(Browser.Element target, String name);

    @JsMethod
    private static native void callSaved(Object method, SyntheticEvent<?> event);

    @JsMethod
    private static native Object[] eventOverlays(String name);

    @JsMethod
    private static native void inspectWindow(Inspector inspector);

    @JsMethod
    private static native String[] methods(Object overlay);

    @JsMethod
    private static native Object invoke(Object overlay, String method, Object value);
}
