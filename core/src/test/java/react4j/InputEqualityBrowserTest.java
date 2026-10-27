package react4j;

import static react4j.webtesting.Checks.check;

import arez.EqualityComparator;
import arez.ObjectsDeepEqualsComparator;
import arez.ObjectsEqualsComparator;
import arez.annotations.Action;
import arez.annotations.DefaultEqualityComparator;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
final class InputEqualityBrowserTest {
    private static int _constructed;
    private static int _renders;
    private static int _computations;
    private static int _pre;
    private static int _post;
    private static int _multi;
    private static @Nullable Content _current;

    private InputEqualityBrowserTest() {}

    static void run(final Browser.Element root) {
        _constructed = 0;
        _renders = 0;
        _computations = 0;
        _pre = 0;
        _post = 0;
        _multi = 0;
        RuntimeBrowserTest.render(
                InputEqualityBrowserTest_ContentBuilder.values("one").tick(0), root);
        // Debug-state storage can force another mount render.
        final var initialRenders = _renders;
        final String[] replacement = {"one"};
        RuntimeBrowserTest.render(
                InputEqualityBrowserTest_ContentBuilder.values(replacement).tick(0), root);
        check(
                1 == _constructed && initialRenders == _renders && 1 == _computations,
                "Equal content skips rendering and invalidation");
        check(0 == _pre && 0 == _post, "Equal content skips change callbacks");
        check(replacement == Objects.requireNonNull(_current).values(), "Equal replacement remains the current input");
        RuntimeBrowserTest.render(
                InputEqualityBrowserTest_ContentBuilder.values("one").tick(1), root);
        check(
                initialRenders < _renders && 1 == _computations && 0 == _pre && 0 == _post,
                "Other input changes render without invalidating equal content");
        final var afterTick = _renders;
        RuntimeBrowserTest.render(
                InputEqualityBrowserTest_ContentBuilder.values("two").tick(1), root);
        check(
                1 == _constructed && afterTick < _renders && 2 == _computations && 1 == _pre && 1 == _post,
                "Changed content updates the existing view and all change consequences");
        check(0 == _multi, "Multi-input callbacks require all listed inputs to change");
        RuntimeBrowserTest.render(
                InputEqualityBrowserTest_ContentBuilder.values("three").tick(2), root);
        check(1 == _multi, "Multi-input callbacks use each resolved equality policy");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_ByDefaultBuilder.value(new Value("one")), root);
        final var defaults = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_ByDefaultBuilder.value(new Value("one")), root);
        check(defaults == _renders, "Exact-type default comparator suppresses equal replacement");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_ByDefaultBuilder.value(new Value("two")), root);
        check(defaults + 1 == _renders, "Exact-type default propagates changed values");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_ExplicitBuilder.value(new Value("one")), root);
        final var overrides = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_ExplicitBuilder.value(new Value("one")), root);
        check(overrides + 1 == _renders, "Explicit comparator overrides the type default");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_CustomBuilder.value(1), root);
        final var custom = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_CustomBuilder.value(3), root);
        check(custom == _renders, "Custom comparator receives Integer values through its interface");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_CustomBuilder.value(4), root);
        check(custom + 1 == _renders, "Custom comparator propagates changes");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_NullableValueBuilder.value(null), root);
        final var nullable = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_NullableValueBuilder.value(null), root);
        check(nullable == _renders, "Nullable comparator values preserve null");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_NullableValueBuilder.value(2), root);
        check(nullable + 1 == _renders, "Nullable wrapper is boxed as its declared Java type");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_CharacterValueBuilder.value('a'), root);
        final var characters = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_CharacterValueBuilder.value('a'), root);
        RuntimeBrowserTest.render(InputEqualityBrowserTest_CharacterValueBuilder.value('b'), root);
        check(characters + 1 == _renders, "Character comparator values are boxed correctly");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_LongValueBuilder.value(9007199254740992L), root);
        final var longs = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_LongValueBuilder.value(9007199254740992L), root);
        RuntimeBrowserTest.render(InputEqualityBrowserTest_LongValueBuilder.value(9007199254740993L), root);
        check(longs + 1 == _renders, "Long comparator values retain all bits");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_FloatingBuilder.value(Double.NaN), root);
        final var floating = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_FloatingBuilder.value(Double.NaN), root);
        check(floating == _renders, "Selected Objects.equals preserves NaN equality");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_FloatingBuilder.value(0.0), root);
        RuntimeBrowserTest.render(InputEqualityBrowserTest_FloatingBuilder.value(-0.0), root);
        check(floating + 2 == _renders, "Selected Objects.equals distinguishes signed zeros");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_StrictFloatingBuilder.value(Double.NaN), root);
        final var strict = _renders;
        RuntimeBrowserTest.render(InputEqualityBrowserTest_StrictFloatingBuilder.value(Double.NaN), root);
        check(strict + 1 == _renders, "Unselected comparator retains strict NaN comparison");
        RuntimeBrowserTest.render(InputEqualityBrowserTest_StrictFloatingBuilder.value(0.0), root);
        RuntimeBrowserTest.render(InputEqualityBrowserTest_StrictFloatingBuilder.value(-0.0), root);
        check(strict + 2 == _renders, "Strict fallback equates signed zeros");
        RuntimeBrowserTest.render(null, root);
    }

    static BrowserPromise reactiveScenario(final Browser.Element root) {
        RuntimeBrowserTest.render(
                InputEqualityBrowserTest_ContentBuilder.values("reactive").tick(0), root);
        return Browser.delay(0).then(value -> {
            final var before = _renders;
            final var computations = _computations;
            final var pre = _pre;
            final var post = _post;
            Objects.requireNonNull(_current).changeExtra();
            return Browser.delay(0)
                    .thenDo(() -> check(
                            before < _renders && computations == _computations && pre == _pre && post == _post,
                            "Arez dependency changes render without changing equal inputs"));
        });
    }

    @View
    abstract static class Content {
        Content() {
            _constructed++;
            _current = this;
        }

        @Input(observable = Feature.ENABLE, equalityComparator = ObjectsDeepEqualsComparator.class)
        abstract String[] values();

        @Input
        abstract int tick();

        @Memoize
        String summary() {
            _computations++;
            return String.join(",", values());
        }

        @Observable
        abstract int getExtra();

        abstract void setExtra(int value);

        @Action
        void changeExtra() {
            setExtra(getExtra() + 1);
        }

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(summary() + tick() + getExtra());
        }

        @OnInputChange
        void before(String[] values) {
            _pre++;
        }

        @OnInputChange(phase = OnInputChange.Phase.POST)
        void after(String[] values) {
            _post++;
        }

        @OnInputChange
        void both(String[] values, int tick) {
            _multi++;
        }
    }

    @DefaultEqualityComparator(ObjectsEqualsComparator.class)
    record Value(String text) {}

    @View(type = View.Type.STATEFUL)
    abstract static class ByDefault {
        @Input
        abstract Value value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(value().text());
        }
    }

    public static final class AlwaysDifferent implements EqualityComparator {
        @Override
        public boolean areEqual(@Nullable Object oldValue, @Nullable Object newValue) {
            return false;
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Explicit {
        @Input(equalityComparator = AlwaysDifferent.class)
        abstract Value value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(value().text());
        }
    }

    public static final class Parity implements EqualityComparator {
        @Override
        public boolean areEqual(@Nullable Object oldValue, @Nullable Object newValue) {
            if (null == oldValue || null == newValue) {
                return oldValue == newValue;
            }
            return ((Integer) oldValue).intValue() % 2 == ((Integer) newValue).intValue() % 2;
        }

        public boolean areEqual(Integer oldValue, Integer newValue) {
            return false;
        }
    }

    public static final class Characters implements EqualityComparator {
        @Override
        public boolean areEqual(@Nullable Object oldValue, @Nullable Object newValue) {
            return ((Character) Objects.requireNonNull(oldValue)).charValue()
                    == ((Character) Objects.requireNonNull(newValue)).charValue();
        }
    }

    public static final class Longs implements EqualityComparator {
        @Override
        public boolean areEqual(@Nullable Object oldValue, @Nullable Object newValue) {
            return ((Long) Objects.requireNonNull(oldValue)).longValue()
                    == ((Long) Objects.requireNonNull(newValue)).longValue();
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Custom {
        @Input(equalityComparator = Parity.class)
        abstract int value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(value());
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class NullableValue {
        @Input(equalityComparator = Parity.class)
        abstract @Nullable Integer value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(String.valueOf(value()));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class CharacterValue {
        @Input(equalityComparator = Characters.class)
        abstract char value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(String.valueOf(value()));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class LongValue {
        @Input(equalityComparator = Longs.class)
        abstract long value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(String.valueOf(value()));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class Floating {
        @Input(equalityComparator = ObjectsEqualsComparator.class)
        abstract double value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(String.valueOf(value()));
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class StrictFloating {
        @Input
        abstract double value();

        @Render
        ReactNode render() {
            _renders++;
            return ReactNode.of(String.valueOf(value()));
        }
    }
}
