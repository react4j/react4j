package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import org.jspecify.annotations.NullMarked;
import react4j.webtesting.Browser;

@NullMarked
final class ContextsTest {
    private ContextsTest() {}

    static void run(Browser.Element root) {
        Contexts.clear();
        try {
            Contexts.register(String.class, "qualified", "qualified default");
            var qualified = Contexts.get(String.class, "qualified");
            defaultValue(qualified, "qualified default", root);
            Contexts.register(String.class, "other", "other default");
            check(qualified != Contexts.get(String.class, "other"), "qualifiers select distinct contexts");
            Contexts.register(Runnable.class, "null default");
            var empty = Contexts.get(Runnable.class, "null default");
            defaultValue(empty, "<null>", root);

            Contexts.register(Integer.class, 42);
            var unqualified = Contexts.get(Integer.class);
            check(unqualified == Contexts.get(Integer.class, ""), "default qualifier");
            defaultValue(unqualified, "42", root);
            Contexts.register(Boolean.class);
            defaultValue(Contexts.get(Boolean.class), "<null>", root);

            if (React.shouldCheckInvariants()) {
                fails(
                        () -> Contexts.register(String.class, "qualified", "replacement"),
                        "Attempting to register React context with type " + String.class
                                + " and qualifier 'qualified' but a context already exists with that type and name");
                check(qualified == Contexts.get(String.class, "qualified"), "duplicate registration preserves context");
                defaultValue(qualified, "qualified default", root);
                fails(
                        () -> Contexts.register(int.class, "port", 2),
                        "Attempting to register primitive type int in React context. Use the boxed type instead");
                fails(
                        () -> Contexts.get(int.class, "port"),
                        "Attempting to access primitive type int from the React context. Access using the boxed type"
                                + " instead");
                fails(
                        () -> Contexts.get(Double.class, "missing"),
                        "Attempting to retrieve React context with type " + Double.class
                                + " and qualifier 'missing' but no such context exists with that type and name");
                fails(
                        () -> Contexts.get(Runnable.class),
                        "Attempting to retrieve React context with type " + Runnable.class
                                + " and qualifier '' but no such context exists with that type and name");
                fails(
                        () -> Contexts.get(String.class, "missing"),
                        "Attempting to retrieve React context with type " + String.class
                                + " and qualifier 'missing' but no such context exists with that type and name");
                ReactTestUtil.noCheckInvariants();
                try {
                    Contexts.register(Float.class);
                    defaultValue(Contexts.get(Float.class), "<null>", root);
                } finally {
                    ReactTestUtil.checkInvariants();
                }
            }
        } finally {
            Contexts.clear();
        }
    }

    private static <T> void defaultValue(Context<T> context, String expected, Browser.Element root) {
        RuntimeBrowserTest.render(
                context.consumer().render(value -> ReactNode.of(null == value ? "<null>" : value.toString())), root);
        equal(expected, root.getTextContent());
    }

    private static void fails(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalStateException expected) {
            equal(message, expected.getMessage());
            return;
        }
        throw new AssertionError("Expected: " + message);
    }
}
