package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.stream.Stream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.PostRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.webtesting.Browser;

@NullMarked
final class GeneratedBuildableBrowserTest {
    private static int _builds;
    private static int _postRenders;

    private GeneratedBuildableBrowserTest() {}

    static void run(final Browser.Element root) {
        final Buildable ready = GeneratedBuildableBrowserTest_LeafBuilder.label("ready");
        RuntimeBrowserTest.render(ReactNode.of(ready), root);
        equal("ready", root.getTextContent());
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_ScalarBuilder.node(ready), root);
        equal("ready", root.getTextContent());
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_ArrayBuilder.twice(ready), root);
        equal("readyready", root.getTextContent());
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_ChildBuilder.child(ready), root);
        equal("ready", root.getTextContent());
        RuntimeBrowserTest.render(
                GeneratedBuildableBrowserTest_ChildrenBuilder.children(Stream.of(ready, ready)), root);
        equal("readyready", root.getTextContent());
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_RenderResultBuilder.build(), root);
        equal("rendered", root.getTextContent());
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_NullableResultBuilder.build(), root);
        equal("", root.getTextContent());
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_NullableResultBuilder.visible(true), root);
        equal("visible", root.getTextContent());
        RuntimeBrowserTest.render(ReactNode.of(GeneratedBuildableBrowserTest_GenericBuilder.id(7)), root);
        equal("7", root.getTextContent());
        RuntimeBrowserTest.render(
                ReactNode.of(GeneratedBuildableBrowserTest_NullableInputsBuilder.node(null)
                        .nodes(null, ready)),
                root);
        equal("ready", root.getTextContent());
        RuntimeBrowserTest.render(ReactNode.of(GeneratedBuildableBrowserTest_NullableInputsBuilder.newBuilder()), root);
        equal("", root.getTextContent());
        _builds = 0;
        _postRenders = 0;
        RuntimeBrowserTest.render(GeneratedBuildableBrowserTest_HookedResultBuilder.build(), root);
        equal("hooked", root.getTextContent());
        check(0 < _builds && _builds == _postRenders, "Render result builds before each post-render hook");
        _builds = 0;
        final Buildable counted = () -> {
            _builds++;
            return ReactNode.of("counted");
        };
        final var array = GeneratedBuildableBrowserTest_ArrayBuilder.twice(counted);
        check(2 == _builds, "Enhancer setter eagerly builds each array occurrence");
        RuntimeBrowserTest.render(array, root);
        equal("countedcounted", root.getTextContent());
        check(2 == _builds, "View receives materialized nodes");
        final Buildable defaulted = GeneratedBuildableBrowserTest_DefaultBuilder.newBuilder();
        RuntimeBrowserTest.render(ReactNode.of(defaulted), root);
        equal("defaultarray", root.getTextContent());
        Contexts.register(Renderable.class, counted);
        try {
            RuntimeBrowserTest.render(
                    ReactNode.of(GeneratedBuildableBrowserTest_ContextNodeBuilder.newBuilder()), root);
            equal("counted", root.getTextContent());
            check(2 < _builds, "Context input materializes its supplied builder");
        } finally {
            RuntimeBrowserTest.render(null, root);
            Contexts.clear();
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Leaf {
        @Input
        abstract @Nullable String label();

        @Input
        abstract @Nullable String suffix();

        @Render
        ReactNode render() {
            return ReactNode.of((null == label() ? "" : label()) + (null == suffix() ? "" : suffix()));
        }
    }

    interface Node<R> {
        R node(Renderable value);
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Scalar {
        private final ReactNode _node;

        Scalar(@Input(enhancer = Node.class) final ReactNode node) {
            _node = node;
        }

        @Render
        ReactNode render() {
            return _node;
        }
    }

    interface Nodes<R> {
        R nodes(Renderable... values);

        default R twice(Buildable value) {
            return nodes(value, value);
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Array {
        @Input(enhancer = Nodes.class)
        abstract ReactNode[] nodes();

        @Render
        ReactNode render() {
            return ReactNode.of(nodes());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Child {
        @Input
        abstract ReactNode child();

        @Render
        ReactNode render() {
            return child();
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Children {
        @Input
        abstract Renderable[] children();

        @Render
        ReactNode render() {
            return ReactNode.of(children());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class RenderResult {
        @Render
        Renderable render() {
            return GeneratedBuildableBrowserTest_LeafBuilder.label("rendered");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class NullableResult {
        @Input
        abstract @Nullable Boolean visible();

        @Render
        @Nullable
        Buildable render() {
            return Boolean.TRUE.equals(visible()) ? GeneratedBuildableBrowserTest_LeafBuilder.label("visible") : null;
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Generic<T extends Number> {
        private final T _id;

        Generic(@Input final T id) {
            _id = id;
        }

        @Input
        abstract @Nullable String suffix();

        @Render
        ReactNode render() {
            return ReactNode.of(_id.toString());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class NullableInputs {
        @Input
        abstract @Nullable ReactNode node();

        @Input
        abstract @Nullable ReactNode @Nullable [] nodes();

        @Render
        ReactNode render() {
            final var values = nodes();
            return ReactNode.of(node(), null == values ? null : ReactNode.of(values));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class HookedResult {
        @Render
        Buildable render() {
            return () -> {
                _builds++;
                return ReactNode.of("hooked");
            };
        }

        @PostRender
        void afterRender() {
            _postRenders++;
            check(_builds == _postRenders, "Post-render observes the completed build");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Default {
        @InputDefault
        static final Renderable DEFAULT_NODE = (Buildable) () -> ReactNode.of("default");

        @InputDefault
        static Renderable[] getNodesDefault() {
            return new Renderable[] {(Buildable) () -> ReactNode.of("array")};
        }

        @Input
        abstract Renderable node();

        @Input
        abstract Renderable[] nodes();

        @Render
        ReactNode render() {
            return ReactNode.of(node(), ReactNode.of(nodes()));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class ContextNode {
        @Input(fromTreeContext = true)
        abstract Renderable node();

        @Render
        Renderable render() {
            return node();
        }
    }
}
