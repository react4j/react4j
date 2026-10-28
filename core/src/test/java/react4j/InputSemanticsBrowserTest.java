package react4j;

import static react4j.webtesting.Checks.check;

import arez.annotations.PreDispose;
import arez.component.Identifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.webtesting.Browser;

@NullMarked
final class InputSemanticsBrowserTest {
    private static int _constructed;
    private static int _rendered;
    private static int _disposed;
    private static String @Nullable [] _captured;

    private InputSemanticsBrowserTest() {}

    static void run(final Browser.Element root) {
        _constructed = 0;
        _rendered = 0;
        _disposed = 0;
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_StringsBuilder.values("one", "two"), root);
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_StringsBuilder.values("one", "two"), root);
        check(1 == _constructed && 1 == _rendered && 0 == _disposed, "Equal arrays reuse the view");
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_StringsBuilder.values("two", "one"), root);
        check(2 == _constructed && 2 == _rendered && 1 == _disposed, "Changed order remounts");
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_CombinedBuilder.values("a-b").label("c"), root);
        final var count = _constructed;
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_CombinedBuilder.values("a").label("b-c"), root);
        check(count + 1 == _constructed, "Combined key parts are framed");
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_DefaultedBuilder.newBuilder().build(), root);
        final var defaults = _constructed;
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_DefaultedBuilder.values("default"), root);
        check(defaults == _constructed, "Default and explicit equal arrays share identity");
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_DefaultedBuilder.values("changed"), root);
        check(defaults + 1 == _constructed, "Default override changes identity");
        Contexts.register(String[].class, "input-semantics", new String[] {"context"});
        final var context = Contexts.get(String[].class, "input-semantics");
        RuntimeBrowserTest.render(
                context.provider()
                        .value(new String[] {"one"})
                        .children(InputSemanticsBrowserTest_ContextualBuilder.build()),
                root);
        final var contexts = _constructed;
        RuntimeBrowserTest.render(
                context.provider()
                        .value(new String[] {"one"})
                        .children(InputSemanticsBrowserTest_ContextualBuilder.build()),
                root);
        check(contexts == _constructed, "Equal context arrays reuse identity");
        RuntimeBrowserTest.render(
                context.provider()
                        .value(new String[] {"two"})
                        .children(InputSemanticsBrowserTest_ContextualBuilder.build()),
                root);
        check(contexts + 1 == _constructed, "Changed context arrays remount");
        final String[] first = {"same", "first"};
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_CustomBuilder.values(first), root);
        final var custom = _constructed;
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_CustomBuilder.values("same", "second"), root);
        check(custom == _constructed && first == _captured, "Custom key retains original immutable input");
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_CustomBuilder.values("changed", "third"), root);
        check(custom + 1 == _constructed, "Custom key changes remount");
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_PrimitiveKeyBuilder.value(17), root);
        final var primitives = _constructed;
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_PrimitiveKeyBuilder.value(17), root);
        check(primitives == _constructed, "Primitive key provider receives a Java Integer");
        RuntimeBrowserTest.render(InputSemanticsBrowserTest_PrimitiveKeyBuilder.value(18), root);
        check(primitives + 1 == _constructed, "Primitive custom keys propagate changes");
        mixedInputs(root);
        RuntimeBrowserTest.render(null, root);
        Contexts.clear();
    }

    private static void mixedInputs(final Browser.Element root) {
        final @Nullable Object[] dynamic = {new Identified(17), new NamedKey("key"), new TextValue("text"), null};
        final @Nullable Object[] equal = {new Identified(17), new NamedKey("key"), new TextValue("text"), null};
        for (var i = 0; i < dynamic.length; i++) {
            RuntimeBrowserTest.render(
                    InputSemanticsBrowserTest_MixedArrayBuilder.values("same").other(dynamic[i]), root);
            final var count = _constructed;
            RuntimeBrowserTest.render(
                    InputSemanticsBrowserTest_MixedArrayBuilder.values("same").other(equal[i]), root);
            check(count == _constructed, "Array keys compose with each dynamic identity strategy");
            RuntimeBrowserTest.render(
                    InputSemanticsBrowserTest_MixedArrayBuilder.values("changed")
                            .other(equal[i]),
                    root);
            check(count + 1 == _constructed, "Array changes propagate alongside dynamic inputs");
        }
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_MixedArrayBuilder.values("same").other(new Identified(17)), root);
        final var arrays = _constructed;
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_MixedArrayBuilder.values("same").other(new Identified(18)), root);
        check(arrays + 1 == _constructed, "Dynamic identifier changes propagate alongside arrays");
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_MixedCustomBuilder.values("same", "first")
                        .other(new Identified(17)),
                root);
        final var custom = _constructed;
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_MixedCustomBuilder.values("same", "second")
                        .other(new Identified(17)),
                root);
        check(custom == _constructed, "Custom keys compose with equal dynamic identifiers");
        RuntimeBrowserTest.render(
                InputSemanticsBrowserTest_MixedCustomBuilder.values("same", "second")
                        .other(new Identified(18)),
                root);
        check(custom + 1 == _constructed, "Dynamic identifier changes propagate alongside custom keys");
    }

    private record Identified(Integer id) implements Identifiable<Integer> {
        @Override
        public Integer getArezId() {
            return id;
        }
    }

    private record NamedKey(String key) implements Keyed {
        @Override
        public String getKey() {
            return key;
        }
    }

    private record TextValue(String text) {
        @Override
        public String toString() {
            return text;
        }
    }

    public static final class PrefixKeys implements InputKeyGenerator<String[]> {
        @Override
        public String getKey(final String[] value) {
            return value[0];
        }
    }

    public static final class IntegerKeys implements InputKeyGenerator<Integer> {
        @Override
        public String getKey(final Integer value) {
            return value.toString();
        }

        public String getKey(final int value) {
            return "wrong-overload";
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Custom {
        Custom(@Input(keyGenerator = PrefixKeys.class) final String[] values) {
            _constructed++;
            _captured = values;
        }

        @Render
        ReactNode render() {
            _rendered++;
            return ReactNode.of("custom");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class PrimitiveKey {
        PrimitiveKey(@Input(keyGenerator = IntegerKeys.class) final int value) {
            _constructed++;
        }

        @Render
        ReactNode render() {
            _rendered++;
            return ReactNode.of("primitive");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Strings {
        Strings(@Input final String[] values) {
            _constructed++;
        }

        @Render
        ReactNode render() {
            _rendered++;
            return ReactNode.of("strings");
        }

        @PreDispose
        void beforeDispose() {
            _disposed++;
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Combined {
        Combined(@Input final String[] values, @Input final String label) {
            _constructed++;
        }

        @Render
        ReactNode render() {
            _rendered++;
            return ReactNode.of("combined");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class MixedArray {
        MixedArray(@Input final String[] values, @Input(require = Feature.ENABLE) @Nullable final Object other) {
            _constructed++;
        }

        @Render
        ReactNode render() {
            return ReactNode.of("mixed-array");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class MixedCustom {
        MixedCustom(@Input(keyGenerator = PrefixKeys.class) final String[] values, @Input final Object other) {
            _constructed++;
        }

        @Render
        ReactNode render() {
            return ReactNode.of("mixed-custom");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Defaulted {
        Defaulted(@Input final String[] values) {
            _constructed++;
        }

        @InputDefault
        static String[] getValuesDefault() {
            return new String[] {"default"};
        }

        @Render
        ReactNode render() {
            _rendered++;
            return ReactNode.of("default");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Contextual {
        Contextual(@Input(fromTreeContext = true, qualifier = "input-semantics") final String[] values) {
            _constructed++;
        }

        @Render
        ReactNode render() {
            _rendered++;
            return ReactNode.of("context");
        }
    }
}
