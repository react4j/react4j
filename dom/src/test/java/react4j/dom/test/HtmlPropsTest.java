package react4j.dom.test;

import static org.testng.Assert.*;

import org.jspecify.annotations.Nullable;
import org.testng.annotations.Test;
import react4j.dom.proptypes.html.HtmlGlobalFields;

public final class HtmlPropsTest {
    @Test
    public void combineClassNamesWithNullElements() {
        final var props = new TestProps();
        assertSame(props.className("one", null, "two"), props);
        assertEquals(props.className, "one two");
    }

    @Test
    public void absentClassNamesLeaveExistingProperty() {
        final var props = new TestProps();
        props.className("existing");
        props.className(null, null);
        assertEquals(props.className, "existing");
    }

    @Test
    public void rejectBlankClassNames() {
        final var props = new TestProps();
        expectThrows(IllegalStateException.class, () -> props.className(" "));
    }

    private static final class TestProps extends HtmlGlobalFields<TestProps> {
        private @Nullable String className;

        @Override
        protected void setClassName(@Nullable String value) {
            className = value;
        }
    }
}
