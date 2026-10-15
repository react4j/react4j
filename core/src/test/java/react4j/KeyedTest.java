package react4j;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public final class KeyedTest {
    @Test
    public void getKey() {
        final String key = "abcdef";
        final Keyed keyed = new TestKeyed(key);
        assertEquals(keyed.getKey(), key);
        assertEquals(Keyed.getKey(keyed), key);
    }

    @Test
    public void getKey_onNonKeyed() {
        assertNull(Keyed.getKey("XXXX"));
    }

    private static class TestKeyed implements Keyed {
        private final String _key;

        TestKeyed(final String key) {
            _key = key;
        }

        @Override
        public String getKey() {
            return _key;
        }
    }
}
