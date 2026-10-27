package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Set;
import java.util.stream.Stream;
import jsinterop.base.Js;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.webtesting.Browser;

@NullMarked
public final class InputEnhancerBrowserTest {
    private static int _helperCalls;
    private static int _streamReads;

    private InputEnhancerBrowserTest() {}

    static void run(final Browser.Element root) throws ConversionException {
        _helperCalls = 0;
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_AdvancingBuilder.<Integer>newBuilder()
                        .one()
                        .titled("one"),
                root);
        equal("1:one", root.getTextContent());
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_AdvancingBuilder.<Integer, Double>number(3.0)
                        .titled("three", "units"),
                root);
        equal("3:three units", root.getTextContent());
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_OptionalBuilder.titled("optional")
                        .extra("tail")
                        .build(),
                root);
        equal("optional:tail", root.getTextContent());
        RuntimeBrowserTest.render(InputEnhancerBrowserTest_TerminalBuilder.one(), root);
        equal("1", root.getTextContent());
        RuntimeBrowserTest.render(InputEnhancerBrowserTest_PanelsBuilder.unitPanels(), root);
        equal("standard", root.getTextContent());
        var caught = false;
        try {
            InputEnhancerBrowserTest_TerminalBuilder.number(-1);
        } catch (final ConversionException error) {
            caught = "negative".equals(error.getMessage());
        }
        check(caught, "Static helper preserves checked exceptions");
        check(4 == _helperCalls, "Helpers preserve user side effects");
        final var child = ReactNode.of("value");
        final var scalarNode = InputEnhancerBrowserTest_OptionalChildBuilder.label("scalar")
                .extra("tail")
                .child(child);
        final ReactElement scalarElement = Js.uncheckedCast(scalarNode);
        final ReactNode[] childValues = Js.uncheckedCast(scalarElement.inputs().get("children"));
        check(1 == childValues.length && child == childValues[0], "Scalar child retains its React children array");
        RuntimeBrowserTest.render(scalarNode, root);
        equal("scalar:tail:value", root.getTextContent());
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_OptionalChildrenBuilder.label("array")
                        .extra("tail")
                        .children(ReactNode.of("first"), ReactNode.of("second")),
                root);
        equal("array:tail:firstsecond", root.getTextContent());
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_OptionalChildrenBuilder.label("omitted")
                        .extra("tail")
                        .build(),
                root);
        equal("omitted:tail:", root.getTextContent());
        _streamReads = 0;
        final var streamNode = InputEnhancerBrowserTest_OptionalChildrenBuilder.label("stream")
                .extra("tail")
                .children(
                        Stream.of(ReactNode.of("first"), ReactNode.of("second")).peek(value -> _streamReads++));
        check(2 == _streamReads, "Stream children are materialized exactly once before rendering");
        RuntimeBrowserTest.render(streamNode, root);
        equal("stream:tail:firstsecond", root.getTextContent());
        RuntimeBrowserTest.render(streamNode, root);
        check(2 == _streamReads, "Rendering does not traverse the children stream again");
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_OptionalChildrenBuilder.children(Stream.of(ReactNode.of("shortcut"))), root);
        equal("null:null:shortcut", root.getTextContent());
        RuntimeBrowserTest.render(
                InputEnhancerBrowserTest_CustomChildrenBuilder.children(
                        Stream.of(ReactNode.of("first"), ReactNode.of("ignored"))),
                root);
        equal("first", root.getTextContent());
        RuntimeBrowserTest.render(null, root);
    }

    interface Count<R> {
        R count(int value);

        default R one() {
            _helperCalls++;
            return count(1);
        }

        default <T extends Number> R number(T value) throws ConversionException {
            _helperCalls++;
            if (0 > value.intValue()) {
                throw new ConversionException("negative");
            }
            return count(value.intValue());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class OptionalChild {
        private final @Nullable ReactNode _child;

        OptionalChild(@Input final @Nullable ReactNode child) {
            _child = child;
        }

        @Input
        abstract @Nullable String label();

        @Input
        abstract @Nullable String extra();

        @Render
        ReactNode render() {
            return ReactNode.of(ReactNode.of(label() + ":" + extra() + ":"), _child);
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class OptionalChildren {
        @Input
        abstract ReactNode @Nullable [] children();

        @Input
        abstract @Nullable String label();

        @Input
        abstract @Nullable String extra();

        @Render
        ReactNode render() {
            final var values = children();
            return ReactNode.of(
                    ReactNode.of(label() + ":" + extra() + ":"), null == values ? null : ReactNode.of(values));
        }
    }

    interface Label<R> {
        R label(String value);

        default R titled(String... words) {
            return label(String.join(" ", words));
        }
    }

    interface FirstChild<R> extends ChildrenEnhancer<R> {
        @Override
        default R children(Stream<? extends ReactNode> values) {
            return ChildrenEnhancer.super.children(values.limit(1));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class CustomChildren {
        @Input(enhancer = FirstChild.class)
        abstract ReactNode[] children();

        @Render
        ReactNode render() {
            return ReactNode.of(children());
        }
    }

    interface PanelTypes<R> {
        R types(@Nullable Set<Class<?>> value);

        default R unitPanels() {
            return types(Set.of(String.class, Integer.class));
        }
    }

    static final class ConversionException extends Exception {
        private static final long serialVersionUID = 1L;

        ConversionException(final String message) {
            super(message);
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Advancing<T extends Number> {
        private final int _count;

        Advancing(@Input(name = "count", enhancer = Count.class) final int initialCount) {
            _count = initialCount;
        }

        @Input(enhancer = Label.class)
        abstract String label();

        @Render
        ReactNode render() {
            return ReactNode.of(_count + ":" + label());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Optional {
        @Input(enhancer = Label.class)
        abstract @Nullable String label();

        @Input
        abstract @Nullable String extra();

        @Render
        ReactNode render() {
            return ReactNode.of(label() + ":" + extra());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Terminal {
        private final int _count;

        Terminal(@Input(enhancer = Count.class) final int count) {
            _count = count;
        }

        @Render
        ReactNode render() {
            return ReactNode.of(Integer.toString(_count));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Panels {
        @Input(enhancer = PanelTypes.class)
        abstract @Nullable Set<Class<?>> types();

        @Render
        ReactNode render() {
            final var types = types();
            return ReactNode.of(
                    null != types && types.contains(String.class) && types.contains(Integer.class)
                            ? "standard"
                            : "empty");
        }
    }
}
