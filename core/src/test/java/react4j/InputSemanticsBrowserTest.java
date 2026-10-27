package react4j;

import static react4j.webtesting.Checks.check;

import arez.annotations.PreDispose;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.internal.InputKeys;
import react4j.webtesting.Browser;

@NullMarked
final class InputSemanticsBrowserTest {
    private static int _constructed;
    private static int _rendered;
    private static int _disposed;
    private static String @Nullable [] _captured;

    private InputSemanticsBrowserTest() {}

    static void run(final Browser.Element root) {
        encoding();
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
        RuntimeBrowserTest.render(null, root);
        Contexts.clear();
    }

    private static void encoding() {
        final Object[] values = {
            new String[] {"a"},
            new boolean[] {true},
            new byte[] {1},
            new short[] {1},
            new int[] {1},
            new long[] {1},
            new char[] {'a'},
            new float[] {1},
            new double[] {1}
        };
        final Object[] equal = {
            new String[] {"a"},
            new boolean[] {true},
            new byte[] {1},
            new short[] {1},
            new int[] {1},
            new long[] {1},
            new char[] {'a'},
            new float[] {1},
            new double[] {1}
        };
        final Object[] different = {
            new String[] {"b"},
            new boolean[] {false},
            new byte[] {2},
            new short[] {2},
            new int[] {2},
            new long[] {2},
            new char[] {'b'},
            new float[] {2},
            new double[] {2}
        };
        final Object[] empty = {
            new String[0],
            new boolean[0],
            new byte[0],
            new short[0],
            new int[0],
            new long[0],
            new char[0],
            new float[0],
            new double[0]
        };
        for (var i = 0; i < values.length; i++) {
            same(values[i], equal[i]);
            different(values[i], different[i]);
            different(values[i], empty[i]);
            different(null, empty[i]);
        }
        same(null, null);
        same(new float[] {Float.NaN, 0}, new float[] {Float.NaN, -0.0f});
        same(new double[] {Double.NaN, 0}, new double[] {Double.NaN, -0.0});
        different(new String[] {"a, b"}, new String[] {"a", "b"});
        different(new @Nullable String[] {null}, new String[] {"null"});
        different(new String[0], new String[] {""});
        different(new String[] {"Aa"}, new String[] {"BB"});
        different(new long[] {9007199254740992L}, new long[] {9007199254740993L});
        different(new long[] {Long.MIN_VALUE}, new long[] {Long.MAX_VALUE});
        different(new char[] {'\0', ':', '\ud800'}, new char[] {'\0', ':', '\ud801'});
        different(new double[] {Double.NEGATIVE_INFINITY}, new double[] {Double.POSITIVE_INFINITY});
        final float base = Float.parseFloat("16777216");
        different(new float[] {base}, new float[] {base + 1});
        var rejected = false;
        try {
            InputKeys.arrayKey(new Object[0]);
        } catch (final IllegalArgumentException error) {
            rejected = true;
        }
        check(rejected, "Unsupported arrays do not silently acquire content keys");
    }

    private static void same(@Nullable final Object a, @Nullable final Object b) {
        check(InputKeys.arrayKey(a).equals(InputKeys.arrayKey(b)), "Equal array content keys");
    }

    private static void different(@Nullable final Object a, @Nullable final Object b) {
        check(!InputKeys.arrayKey(a).equals(InputKeys.arrayKey(b)), "Distinct array content keys");
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
