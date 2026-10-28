package react4j;

import static react4j.webtesting.Checks.check;

import arez.annotations.ArezComponentLike;
import arez.annotations.PreDispose;
import arez.component.Identifiable;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.webtesting.Browser;

@NullMarked
final class ArrayKeyBrowserTest {
    private static int _constructed;
    private static int _disposed;

    private ArrayKeyBrowserTest() {}

    static void run(final Browser.Element root) {
        scalars();
        references(root);
        nested(root);
        RuntimeBrowserTest.render(null, root);
        Contexts.clear();
    }

    private static void scalars() {
        final Object[] first = {
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
        final Object[] changed = {
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
        for (var i = 0; i < first.length; i++) {
            same(scalarKey(first[i]), scalarKey(equal[i]));
            different(scalarKey(first[i]), scalarKey(changed[i]));
            different(scalarKey(first[i]), scalarKey(empty[i]));
            different(scalarKey(null), scalarKey(empty[i]));
        }
        same(scalarKey(new float[] {Float.NaN, 0}), scalarKey(new float[] {Float.NaN, -0.0f}));
        same(scalarKey(new double[] {Double.NaN, 0}), scalarKey(new double[] {Double.NaN, -0.0}));
        different(scalarKey(new String[] {"a, b"}), scalarKey(new String[] {"a", "b"}));
        different(scalarKey(new @Nullable String[] {null}), scalarKey(new String[] {"null"}));
        different(scalarKey(new String[0]), scalarKey(new String[] {""}));
        different(scalarKey(new String[] {"Aa"}), scalarKey(new String[] {"BB"}));
        different(scalarKey(new long[] {9007199254740992L}), scalarKey(new long[] {9007199254740993L}));
        different(scalarKey(new long[] {Long.MIN_VALUE}), scalarKey(new long[] {Long.MAX_VALUE}));
        different(scalarKey(new char[] {'\0', ':', '\ud800'}), scalarKey(new char[] {'\0', ':', '\ud801'}));
        different(
                scalarKey(new double[] {Double.NEGATIVE_INFINITY}), scalarKey(new double[] {Double.POSITIVE_INFINITY}));
        final float base = Float.parseFloat("16777216");
        different(scalarKey(new float[] {base}), scalarKey(new float[] {base + 1}));

        final var boxes = ArrayKeyBrowserTest_ReferencesBuilder.newBuilder();
        different(key(boxes.build()), key(ArrayKeyBrowserTest_ReferencesBuilder.booleans(true)));
        same(
                key(ArrayKeyBrowserTest_ReferencesBuilder.booleans(true)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.booleans(true)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.bytes((byte) 1)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.bytes((byte) 2)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.shorts((short) 1)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.shorts((short) 2)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.ints(1)), key(ArrayKeyBrowserTest_ReferencesBuilder.ints(2)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.longs(9007199254740992L)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.longs(9007199254740993L)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.chars('\0')),
                key(ArrayKeyBrowserTest_ReferencesBuilder.chars(':')));
        same(
                key(ArrayKeyBrowserTest_ReferencesBuilder.floats(Float.NaN, 0.0f)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.floats(Float.NaN, -0.0f)));
        same(
                key(ArrayKeyBrowserTest_ReferencesBuilder.doubles(Double.NaN, 0.0)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.doubles(Double.NaN, -0.0)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.floats(base)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.floats(base + 1)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.doubles(new @Nullable Double[] {null})),
                key(ArrayKeyBrowserTest_ReferencesBuilder.doubles(0.0)));
    }

    private static String scalarKey(@Nullable final Object value) {
        final var builder = ArrayKeyBrowserTest_ScalarsBuilder.newBuilder();
        if (value instanceof String[] array) {
            builder.strings(array);
        } else if (value instanceof boolean[] array) {
            builder.booleans(array);
        } else if (value instanceof byte[] array) {
            builder.bytes(array);
        } else if (value instanceof short[] array) {
            builder.shorts(array);
        } else if (value instanceof int[] array) {
            builder.ints(array);
        } else if (value instanceof long[] array) {
            builder.longs(array);
        } else if (value instanceof char[] array) {
            builder.chars(array);
        } else if (value instanceof float[] array) {
            builder.floats(array);
        } else if (value instanceof double[] array) {
            builder.doubles(array);
        }
        return key(builder.build());
    }

    private static void references(final Browser.Element root) {
        lifecycle(
                root,
                ArrayKeyBrowserTest_ReferencesBuilder.keyed(new Both("one", 17)),
                ArrayKeyBrowserTest_ReferencesBuilder.keyed(new Both("one", 18)),
                ArrayKeyBrowserTest_ReferencesBuilder.keyed(new Both("two", 17)));
        lifecycle(
                root,
                ArrayKeyBrowserTest_ReferencesBuilder.identified(new Both[] {new Both("one", 17)}),
                ArrayKeyBrowserTest_ReferencesBuilder.identified(new Both[] {new Both("two", 17)}),
                ArrayKeyBrowserTest_ReferencesBuilder.identified(new Both[] {new Both("one", 18)}));
        lifecycle(
                root,
                ArrayKeyBrowserTest_ReferencesBuilder.identifiedMatrix(new Both[][] {{new Both("one", 17)}}),
                ArrayKeyBrowserTest_ReferencesBuilder.identifiedMatrix(new Both[][] {{new Both("two", 17)}}),
                ArrayKeyBrowserTest_ReferencesBuilder.identifiedMatrix(new Both[][] {{new Both("one", 18)}}));
        lifecycle(
                root,
                ArrayKeyBrowserTest_ReferencesBuilder.dynamic(new Both("one", 17), new Id(23), new Text("a")),
                ArrayKeyBrowserTest_ReferencesBuilder.dynamic(new Both("one", 18), new Id(23), new Text("a")),
                ArrayKeyBrowserTest_ReferencesBuilder.dynamic(new Both("one", 18), new Id(24), new Text("a")));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.choices(Choice.ONE)),
                key(ArrayKeyBrowserTest_ReferencesBuilder.choices(Choice.TWO)));
        different(
                key(ArrayKeyBrowserTest_ReferencesBuilder.dynamic(new @Nullable Object[] {null})),
                key(ArrayKeyBrowserTest_ReferencesBuilder.dynamic("null")));
        same(
                key(ArrayKeyBrowserTest_GenericBuilder.<Identifiable<Integer>>values(new Both[] {new Both("one", 17)})),
                key(ArrayKeyBrowserTest_GenericBuilder.<Identifiable<Integer>>values(
                        new Both[] {new Both("one", 18)})));
        different(
                key(ArrayKeyBrowserTest_GenericBuilder.<Identifiable<Integer>>values(new Both[] {new Both("one", 17)})),
                key(ArrayKeyBrowserTest_GenericBuilder.<Identifiable<Integer>>values(
                        new Both[] {new Both("two", 17)})));
        final Object array = new String[] {"same"};
        same(
                key(ArrayKeyBrowserTest_ScalarObjectBuilder.value(array)),
                key(ArrayKeyBrowserTest_ScalarObjectBuilder.value(array)));
        same(
                key(ArrayKeyBrowserTest_ScalarObjectBuilder.value(array)),
                key(ArrayKeyBrowserTest_ScalarObjectBuilder.value(String.valueOf(array))));
        same(
                key(ArrayKeyBrowserTest_ReferencesBuilder.dynamic(new Object[] {array})),
                key(ArrayKeyBrowserTest_ReferencesBuilder.dynamic(String.valueOf(array))));
    }

    private static void nested(final Browser.Element root) {
        lifecycle(
                root,
                ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{"one"}, null, {}}),
                ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{"one"}, null, {}}),
                ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{"two"}, null, {}}));
        different(
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{"a", "b"}})),
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{"a"}, {"b"}})));
        different(
                key(ArrayKeyBrowserTest_NestedBuilder.newBuilder().build()),
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[0][])));
        different(
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {null})),
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{}})));
        different(
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{null}})),
                key(ArrayKeyBrowserTest_NestedBuilder.values(new String[][] {{"null"}})));
        same(
                key(ArrayKeyBrowserTest_DeepBuilder.values(new String[][][] {{{"a"}}, null})),
                key(ArrayKeyBrowserTest_DeepBuilder.values(new String[][][] {{{"a"}}, null})));
        different(
                key(ArrayKeyBrowserTest_DeepBuilder.values(new String[][][] {{{"a", "b"}}})),
                key(ArrayKeyBrowserTest_DeepBuilder.values(new String[][][] {{{"a"}, {"b"}}})));
        different(
                key(ArrayKeyBrowserTest_MatrixBuilder.values(new long[][] {{9007199254740992L}, null})),
                key(ArrayKeyBrowserTest_MatrixBuilder.values(new long[][] {{9007199254740993L}, null})));
        same(
                key(ArrayKeyBrowserTest_NestedGenericBuilder.<String>values(new String[][] {{"a"}})),
                key(ArrayKeyBrowserTest_NestedGenericBuilder.<String>values(new String[][] {{"a"}})));
        final String[] row = {"a"};
        final Object scalarRow = row;
        same(
                key(ArrayKeyBrowserTest_GenericBuilder.<String[]>values(new String[][] {row})),
                key(ArrayKeyBrowserTest_GenericBuilder.<String>values(String.valueOf(scalarRow))));
        final Object[][] cycle = new Object[1][];
        cycle[0] = cycle;
        same(
                key(ArrayKeyBrowserTest_NestedGenericBuilder.<Object>values(cycle)),
                key(ArrayKeyBrowserTest_NestedGenericBuilder.<Object>values(cycle)));
        lifecycle(
                root,
                ArrayKeyBrowserTest_DefaultedBuilder.newBuilder().build(),
                ArrayKeyBrowserTest_DefaultedBuilder.values(new String[][] {{"default"}}),
                ArrayKeyBrowserTest_DefaultedBuilder.values(new String[][] {{"changed"}}));
        lifecycle(
                root,
                ArrayKeyBrowserTest_CustomBuilder.values(new String[][] {{"same", "first"}}),
                ArrayKeyBrowserTest_CustomBuilder.values(new String[][] {{"same"}, {"second"}}),
                ArrayKeyBrowserTest_CustomBuilder.values(new String[][] {{"changed"}}));
        Contexts.register(String[][].class, "array-keys", new String[][] {{"default"}});
        final var context = Contexts.get(String[][].class, "array-keys");
        lifecycle(
                root,
                context.provider()
                        .value(new String[][] {{"one"}})
                        .children(ArrayKeyBrowserTest_ContextualBuilder.build()),
                context.provider()
                        .value(new String[][] {{"one"}})
                        .children(ArrayKeyBrowserTest_ContextualBuilder.build()),
                context.provider()
                        .value(new String[][] {{"two"}})
                        .children(ArrayKeyBrowserTest_ContextualBuilder.build()));
    }

    private static void lifecycle(
            final Browser.Element root, final Renderable first, final Renderable equal, final Renderable changed) {
        RuntimeBrowserTest.render(Renderable.toReactNode(first), root);
        final var constructed = _constructed;
        final var disposed = _disposed;
        RuntimeBrowserTest.render(Renderable.toReactNode(equal), root);
        check(constructed == _constructed && disposed == _disposed, "Equal declared array keys retain the view");
        RuntimeBrowserTest.render(Renderable.toReactNode(changed), root);
        check(
                constructed + 1 == _constructed && disposed + 1 == _disposed,
                "Changed declared array keys remount the view");
    }

    private static String key(final Renderable node) {
        return Objects.requireNonNull(((ReactElement) Objects.requireNonNull(Renderable.toReactNode(node))).key());
    }

    private static void same(final String first, final String second) {
        check(first.equals(second), "Equal array keys");
    }

    private static void different(final String first, final String second) {
        check(!first.equals(second), "Distinct array keys");
    }

    private record Both(String key, Integer id) implements Keyed, Identifiable<Integer> {
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Integer getArezId() {
            return id;
        }
    }

    private record Id(Integer id) implements Identifiable<Integer> {
        @Override
        public Integer getArezId() {
            return id;
        }
    }

    private record Text(String text) {
        @Override
        public String toString() {
            return text;
        }
    }

    enum Choice implements Keyed {
        ONE,
        TWO;

        @Override
        public String getKey() {
            return "same";
        }
    }

    @ArezComponentLike
    abstract static class Counted {
        Counted() {
            _constructed++;
        }

        @Render
        ReactNode render() {
            return ReactNode.of("array");
        }

        @PreDispose
        void beforeDispose() {
            _disposed++;
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Scalars extends Counted {
        Scalars(
                @Input final @Nullable String @Nullable [] strings,
                @Input final boolean @Nullable [] booleans,
                @Input final byte @Nullable [] bytes,
                @Input final short @Nullable [] shorts,
                @Input final int @Nullable [] ints,
                @Input final long @Nullable [] longs,
                @Input final char @Nullable [] chars,
                @Input final float @Nullable [] floats,
                @Input final double @Nullable [] doubles) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class References extends Counted {
        References(
                @Input final Boolean @Nullable [] booleans,
                @Input final Byte @Nullable [] bytes,
                @Input final Short @Nullable [] shorts,
                @Input final Integer @Nullable [] ints,
                @Input final Long @Nullable [] longs,
                @Input final Character @Nullable [] chars,
                @Input final Float @Nullable [] floats,
                @Input final @Nullable Double @Nullable [] doubles,
                @Input final Choice @Nullable [] choices,
                @Input final Keyed @Nullable [] keyed,
                @Input final Identifiable<Integer> @Nullable [] identified,
                @Input final Identifiable<Integer> @Nullable [] @Nullable [] identifiedMatrix,
                @Input final @Nullable Object @Nullable [] dynamic) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Nested extends Counted {
        Nested(@Input final @Nullable String @Nullable [] @Nullable [] values) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Deep extends Counted {
        Deep(@Input final String @Nullable [] @Nullable [] @Nullable [] values) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Matrix extends Counted {
        Matrix(@Input final long @Nullable [] @Nullable [] values) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Generic<T> extends Counted {
        Generic(@Input final T[] values) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class NestedGeneric<T> extends Counted {
        NestedGeneric(@Input final T[][] values) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class ScalarObject extends Counted {
        ScalarObject(@Input final Object value) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Defaulted extends Counted {
        Defaulted(@Input final String[][] values) {}

        @InputDefault
        static String[][] getValuesDefault() {
            return new String[][] {{"default"}};
        }
    }

    public static final class NestedKeys implements InputKeyGenerator<String[][]> {
        @Override
        public String getKey(final String[][] value) {
            return value[0][0];
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Custom extends Counted {
        Custom(@Input(keyGenerator = NestedKeys.class) final String[][] values) {}
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Contextual extends Counted {
        Contextual(@Input(fromTreeContext = true, qualifier = "array-keys") final String[][] values) {}
    }
}
