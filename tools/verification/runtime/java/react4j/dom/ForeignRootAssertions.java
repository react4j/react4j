package react4j.dom;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ForeignRootAssertions {
    private ForeignRootAssertions() {}

    @JsMethod
    public static void verify(
            Action mount,
            Check foreignUnmountWithWarning,
            Check foreignUnmount,
            Check ownerUnmount,
            Check empty,
            Count unmounts) {
        mount.run();
        check(foreignUnmountWithWarning.get(), "A caller can unmount a root owned by another runtime");
        check(empty.get(), "The owner removed the host subtree");
        check(1 == unmounts.get(), "The owner ran the unmount lifecycle once");
        check(!foreignUnmount.get(), "A second foreign unmount sees no root");
        check(!ownerUnmount.get(), "The owner has also released the root");
        mount.run();
        check(ownerUnmount.get(), "An ordinary unmount succeeds after remounting");
        check(empty.get(), "An ordinary unmount removes the host subtree");
        check(2 == unmounts.get(), "That unmount runs the lifecycle once");
        check(!foreignUnmount.get(), "An ordinary unmount also clears the ownership marker");
    }

    private static void check(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    @JsFunction
    public interface Action {
        void run();
    }

    @JsFunction
    public interface Check {
        boolean get();
    }

    @JsFunction
    public interface Count {
        int get();
    }
}
