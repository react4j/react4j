package react4j.dom;

import static react4j.webtesting.Checks.check;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import react4j.dom.events.Event;
import react4j.dom.events.SyntheticEvent;
import react4j.webtesting.Browser;

@NullMarked
public final class EventsBrowserTest {
    private EventsBrowserTest() {}

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
                dispatch(root, name, (event, overlays) -> {
                    received[0]++;
                    for (var overlay : overlays) {
                        getters(overlay, event);
                    }
                    check(!event.isDefaultPrevented(), "Initial default state");
                    event.preventDefault();
                    check(event.isDefaultPrevented(), "Default prevention");
                    event.stopPropagation();
                    check(event.isPropagationStopped(), "Propagation stop");
                    event.persist();
                });
                check(1 == received[0], name + " was not dispatched");
            }
            inspectWindow(EventsBrowserTest::getters);
        } finally {
            unmount(root);
            root.remove();
        }
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
    private interface Handler {
        void handle(SyntheticEvent<Event> event, Object[] overlays);
    }

    @JsFunction
    private interface Inspector {
        void inspect(Object overlay, Object value);
    }

    @JsMethod
    private static native void dispatch(Browser.Element root, String name, Handler handler);

    @JsMethod
    private static native void inspectWindow(Inspector inspector);

    @JsMethod
    private static native String[] methods(Object overlay);

    @JsMethod
    private static native Object invoke(Object overlay, String method, Object value);

    @JsMethod
    private static native void unmount(Browser.Element root);
}
