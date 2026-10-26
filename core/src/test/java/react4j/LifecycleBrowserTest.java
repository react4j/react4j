package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import arez.Arez;
import arez.Disposable;
import arez.annotations.Action;
import java.util.Objects;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class LifecycleBrowserTest {
    private static final StringBuilder EVENTS = new StringBuilder();
    private static final IllegalStateException CALLBACK_FAILURE = new IllegalStateException("callback failure");
    private static @Nullable GuardedView _guarded;

    private LifecycleBrowserTest() {}

    @JsMethod
    public static void run() {
        var root = Browser.append("div");
        try {
            EVENTS.setLength(0);
            RuntimeBrowserTest.render(LifecycleBrowserTest_OrderedBuilder.value(1), root);
            equal("render:1,first,second,phaseAdded,restored,middle,root,left,right,extra,", EVENTS.toString());
            equal("1", root.getTextContent());
            EVENTS.setLength(0);
            RuntimeBrowserTest.render(LifecycleBrowserTest_OrderedBuilder.value(2), root);
            equal(
                    "render:2,input-pre:2,first,second,restored,middle,root,left,right,extra,phaseAdded,"
                            + "input-post:2,first,second,restored,middle,root,left,right,extra,phaseAdded,",
                    EVENTS.toString());
            equal("2", root.getTextContent());
            RuntimeBrowserTest.render(null, root);
            disposedGuards(root);
            failurePropagation();
        } finally {
            RuntimeBrowserTest.render(null, root);
            _guarded = null;
            EVENTS.setLength(0);
            root.remove();
        }
    }

    private static void disposedGuards(Browser.Element root) {
        var dependency = new Dependency();
        var element = LifecycleBrowserTest_GuardedBuilder.dependency(dependency).value(1);
        dependency.dispose();
        EVENTS.setLength(0);
        RuntimeBrowserTest.render(element, root);
        check(0 == EVENTS.length(), "Disposed input suppresses all mount callbacks");
        equal("", root.getTextContent());
        RuntimeBrowserTest.render(null, root);

        var live = new Dependency();
        RuntimeBrowserTest.render(
                LifecycleBrowserTest_GuardedBuilder.dependency(live).value(1), root);
        equal("guarded-render,guarded-first,guarded-second,", EVENTS.toString());
        check(null != _guarded, "Mounted view captured");
        Disposable.dispose(_guarded);
        EVENTS.setLength(0);
        RuntimeBrowserTest.render(
                LifecycleBrowserTest_GuardedBuilder.dependency(live).value(2), root);
        check(0 == EVENTS.length(), "Disposed view suppresses pre-update and post-update callbacks");
        equal("1", root.getTextContent());
    }

    private static void record(String event) {
        EVENTS.append(event).append(',');
    }

    private static void failurePropagation() {
        var element = Js.<ReactElement>uncheckedCast(LifecycleBrowserTest_ThrowingBuilder.build());
        var properties = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(element);
        var constructor = Js.<ViewConstructorFunction>uncheckedCast(Objects.requireNonNull(properties.get("type")));
        var view = constructor.construct(element.inputs());
        try {
            for (var invocation : new Runnable[] {
                () -> ((OnComponentDidMount) view).componentDidMount(),
                () -> ((OnGetSnapshotBeforeUpdate) view).getSnapshotBeforeUpdate(element.inputs(), JsPropertyMap.of()),
                () -> ((OnComponentDidUpdate) view).componentDidUpdate(element.inputs())
            }) {
                EVENTS.setLength(0);
                try {
                    invocation.run();
                    throw new AssertionError("Expected callback failure");
                } catch (IllegalStateException error) {
                    check(CALLBACK_FAILURE == error, "Original callback failure propagates");
                }
                equal("throwing,", EVENTS.toString());
            }
        } finally {
            ((OnComponentWillUnmount) view).componentWillUnmount();
        }
    }

    abstract static class Parent {
        @PostMount
        @PostUpdate
        @PreUpdate
        void first() {
            record("parent-first");
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void second() {
            record("parent-second");
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void disabled() {
            record("disabled");
        }

        @PostMount
        void phaseAdded() {
            record("parent-phase");
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void restored() {
            record("parent-restored");
        }
    }

    abstract static class Middle extends Parent {
        @Override
        void restored() {
            record("disabled-restored");
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void middle() {
            record("middle");
        }
    }

    interface Root {
        @PostMount
        @PostUpdate
        @PreUpdate
        default void root() {
            record("root");
        }
    }

    interface Left extends Root {
        @PostMount
        @PostUpdate
        @PreUpdate
        default void left() {
            record("left");
        }
    }

    interface Right extends Root {
        @PostMount
        @PostUpdate
        @PreUpdate
        default void right() {
            record("right");
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class OrderedView extends Middle implements Left, Right {
        @Input(observable = Feature.DISABLE)
        abstract int value();

        @Override
        @PostMount
        @PostUpdate
        @PreUpdate
        void second() {
            record("second");
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void extra() {
            record("extra");
        }

        @Override
        @PostMount
        @PostUpdate
        @PreUpdate
        @Action(verifyRequired = false)
        void first() {
            check(Arez.context().isTransactionActive(), "Lifecycle override intercepted by Arez action");
            record("first");
        }

        @Override
        void disabled() {
            record("unexpected-disabled");
        }

        @Override
        @PostMount
        @PostUpdate
        @PreUpdate
        void restored() {
            record("restored");
        }

        @Override
        @PostMount
        @PostUpdate
        @PreUpdate
        void phaseAdded() {
            record("phaseAdded");
        }

        @OnInputChange
        void beforeInput(int value) {
            record("input-pre:" + value);
        }

        @OnInputChange(phase = OnInputChange.Phase.POST)
        void afterInput(int value) {
            record("input-post:" + value);
        }

        @Render
        ReactNode render() {
            record("render:" + value());
            return ReactNode.of(Integer.toString(value()));
        }
    }

    private static final class Dependency implements Disposable {
        private boolean _disposed;

        @Override
        public void dispose() {
            _disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return _disposed;
        }
    }

    @View(type = View.Type.STATEFUL)
    abstract static class GuardedView {
        GuardedView(@Input Disposable dependency) {
            check(!dependency.isDisposed(), "Disposed input suppresses view construction");
        }

        @Input(observable = Feature.DISABLE)
        abstract int value();

        @Render
        ReactNode render() {
            record("guarded-render");
            return ReactNode.of(Integer.toString(value()));
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void first() {
            _guarded = this;
            record("guarded-first");
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void second() {
            record("guarded-second");
        }
    }

    @View(type = View.Type.NO_RENDER)
    abstract static class ThrowingView {
        @PostMount
        @PostUpdate
        @PreUpdate
        void first() {
            record("throwing");
            throw CALLBACK_FAILURE;
        }

        @PostMount
        @PostUpdate
        @PreUpdate
        void second() {
            record("unexpected-after-failure");
        }
    }
}
