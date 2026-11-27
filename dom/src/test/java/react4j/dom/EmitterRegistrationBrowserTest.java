package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.Objects;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.dom.events.ReactEventHandler;
import react4j.webtesting.Browser;

@NullMarked
public final class EmitterRegistrationBrowserTest {
    private EmitterRegistrationBrowserTest() {}

    @JsMethod
    public static void runEmitterRegistration() {
        final var root = Browser.append("div");
        try {
            final int[] calls = {0};
            final ReactEventHandler handler = event -> calls[0]++;
            render(root, null);
            final var child = Browser.query(root, "#emitter-child");
            render(root, handler);
            check(child == Browser.query(root, "#emitter-child"), "Registration keeps the existing target");
            check(emitterListener(child, "onClick") == handler, "Registration stores the supplied listener");
            check(emitterListener(child, "onClick") == handler, "Lookup retrieves the same listener");
            child.click();
            check(1 == calls[0], "One native click invokes its registered handler once");

            render(root, null);
            check(
                    Js.isTripleEqual(Js.undefined(), emitterListener(child, "onClick")),
                    "Removing handlers returns undefined from lookup");
            child.click();
            check(1 == calls[0], "A removed handler receives no later clicks");

            render(root, handler);
            calls[0] = 0;
            DomEvents.pause();
            try {
                child.click();
                check(0 == calls[0], "A native click is ignored while the emitter is paused");
            } finally {
                DomEvents.resume();
            }
            child.click();
            check(1 == calls[0], "The next click is delivered once after the emitter resumes");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void render(Browser.Element root, @Nullable ReactEventHandler handler) {
        final var props = JsPropertyMap.<@Nullable Object>of("id", "emitter-child");
        if (null != handler) {
            props.set("onClick", handler);
        }
        ReactDOM.render(ReactElement.createHostElement("div", null, null, props), root);
    }

    private static @Nullable Object emitterListener(Browser.Element element, String name) {
        return Objects.requireNonNull(DomEvents.state(Js.uncheckedCast(element)))
                .inputs
                .get(name);
    }
}
