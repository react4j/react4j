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

    @JsMethod
    public static void verifyRefs(Action mount, Action update, Check initialRef, Check foreignRef, Check foreignMissing) {
        mount.run();
        check(initialRef.get(), "The owner's first ref is its local host");
        check(foreignMissing.get(), "The second ref is absent before rendering it");
        update.run();
        check(initialRef.get(), "The owner's update preserves the local ref");
        check(foreignRef.get(), "The foreign renderer attaches the second ref to the same owner");
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
