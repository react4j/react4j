package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.stream.Stream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Renderable;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.webtesting.Browser;

@NullMarked
final class BuildableBrowserTest {
    private BuildableBrowserTest() {}

    static void run(final Browser.Element container, final Browser.Element target) {
        final var calls = new int[1];
        final Buildable child = () -> {
            calls[0]++;
            return DOM.span("built");
        };
        final var children = new @Nullable Renderable[] {DOM.text("raw"), child, null, child};
        final var node = DOM.div(children);
        check(2 == calls[0], "DOM eagerly builds each occurrence");
        check(child == children[1] && child == children[3], "DOM preserves caller arrays");
        ReactDOM.render(node, container);
        equal("rawbuiltbuilt", container.getTextContent());
        check(2 == calls[0], "Rendering materialized DOM does not rebuild children");
        final var reads = new int[1];
        ReactDOM.render(
                DOM.div(Stream.<@Nullable Renderable>of(child, null, DOM.text("tail"))
                        .peek(value -> reads[0]++)),
                container);
        check(3 == reads[0] && 3 == calls[0], "DOM stream traverses once");
        equal("builttail", container.getTextContent());
        final var props = new HtmlProps();
        props.children = child;
        ReactDOM.render(DOM.div(props), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.div(props, new Renderable[0]), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.div(props, (Renderable[]) null), container);
        equal("built", container.getTextContent());
        final var beforeOverride = calls[0];
        ReactDOM.render(DOM.div(props, DOM.text("override")), container);
        check(beforeOverride == calls[0], "Overridden props child is not built");
        check(child == props.children, "DOM preserves caller props");
        equal("override", container.getTextContent());
        ReactDOM.render(DOM.div(props, (Renderable) null), container);
        equal("", container.getTextContent());
        ReactDOM.render(DOM.fragment("key", Stream.of(child)), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.fragment(child), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.fragment(Stream.of(child)), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.suspense(child, Stream.of(child)), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.suspense(child, 0, child), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.suspense("key", child, Stream.of(child)), container);
        equal("built", container.getTextContent());
        ReactDOM.render(DOM.suspense("key", child, 0, Stream.of(child)), container);
        equal("built", container.getTextContent());
        ReactDOM.render(child, container, () -> equal("built", container.getTextContent()));
        ReactDOM.render(child, container);
        equal("built", container.getTextContent());
        ReactDOM.render(ReactDOM.createPortal(child, target), container);
        equal("built", target.getTextContent());
        ReactDOM.unmountComponentAtNode(container);
        equal("", target.getTextContent());
    }
}
