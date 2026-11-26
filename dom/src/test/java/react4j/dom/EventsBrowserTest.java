package react4j.dom;

import static react4j.webtesting.Checks.check;

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
            Browser.query(portal, "button").click();
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

            ReactDOM.render(ReactElement.createFragment(null, radio("first", true), radio("second", false)), root);
            final var first = Browser.query(root, "#first");
            final var second = Browser.query(root, "#second");
            second.click();
            check(
                    Boolean.TRUE.equals(Browser.property(first, "checked")),
                    "Controlled radio previously selected is restored");
            check(Boolean.FALSE.equals(Browser.property(second, "checked")), "Controlled radio click is reverted");

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
                };
                ReactDOM.render(
                        host("button", JsPropertyMap.of("onClickCapture", innerCapture, "onClick", innerBubble)),
                        nestedRoot);
                trace.setLength(0);
                Browser.query(nestedRoot, "button").click();
                check(
                        "inner capture inner bubble outer capture outer bubble".contentEquals(trace),
                        "A nested root dispatches before its enclosing root");
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
    public static void runEnterLeave() {
        final var root = Browser.append("div");
        final var portal = Browser.append("div");
        try {
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
    private static native void movement(Browser.Element element, String type, Browser.@Nullable Element related);

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

            ReactDOM.render(select("b", null, false, change), root);
            final var select = Browser.query(root, "select");
            check("b".equals(select.getValue()), "Select applies the controlled value after creating options");
            trace.setLength(0);
            Browser.setValue(select, "a");
            check("a".contentEquals(trace), "Select change reads the selected option");
            check("b".equals(select.getValue()), "Controlled select restores the selected option");

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

        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runDomEventVariants() {
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

    @JsMethod
    private static native void mouseButton(Browser.Element element, int button);

    @JsMethod
    private static native void textClick(Browser.Element element);

    @JsMethod
    private static native void editableSelection(Browser.Element element);

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
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsFunction
    private interface Probe {
        void run();
    }

    @JsFunction
    private interface FlagProbe {
        boolean read();
    }

    @JsMethod
    private static native int releasedWarnings(Probe probe);

    @JsMethod
    private static native void keyboard(Browser.Element element, String type, String key, int keyCode, int charCode);

    @JsMethod
    private static native void legacyKey(Browser.Element element, String type, int keyCode);

    @JsMethod
    public static void runUncontrolled() {
        final var root = Browser.append("div");
        try {
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

            ReactDOM.render(
                    ReactNode.of(
                            host("input", JsPropertyMap.of("type", "submit", "defaultValue", "unused")),
                            host("input", JsPropertyMap.of("type", "reset", "value", "Reset project"))),
                    root);
            attribute(Browser.query(root, "[type=submit]"), "value", null);
            attribute(Browser.query(root, "[type=reset]"), "value", "Reset project");

            ReactDOM.render(select(null, "b", false, changed), root);
            final var select = Browser.query(root, "select");
            check("b".equals(select.getValue()), "Select uses the mount default");
            ReactDOM.render(select(null, null, true, changed), root);
            check("".equals(select.getValue()), "Enabling multiple clears an uncontrolled select without a default");
            ReactDOM.render(select(null, null, false, changed), root);
            check("a".equals(select.getValue()), "Disabling multiple selects the first available option");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void runHostProperties() {
        final var root = Browser.append("div");
        try {
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
                                                    host("mi", JsPropertyMap.of("children", "x")),
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
    private static native void setSelection(Browser.Element input, int start, int end);

    @JsMethod
    private static native void focus(Browser.Element input);

    @JsMethod
    private static native void blur(Browser.Element input);

    @JsMethod
    private static native boolean isFocused(Browser.Element element);

    private static ReactElement radio(final String id, final boolean checked) {
        final var props = JsPropertyMap.<@Nullable Object>of("type", "radio", "name", "group", "id", id);
        props.set("checked", Browser.bool(checked));
        return ReactElement.createHostElement("input", null, null, props);
    }

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
