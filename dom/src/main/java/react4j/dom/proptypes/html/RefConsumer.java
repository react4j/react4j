package react4j.dom.proptypes.html;

import jsinterop.annotations.JsFunction;
import org.jspecify.annotations.Nullable;

/**
 * Method to pass an element or component instance back from the renderer.
 */
@JsFunction
@FunctionalInterface
public interface RefConsumer {
    /**
     * Passes the reference to the component instance or element.
     * The reference is nonnull when the element has been attached to the DOM and
     * null when the reference has been detached from the DOM.
     *
     * @param reference the reference.
     */
    void accept(@Nullable Object reference);
}
