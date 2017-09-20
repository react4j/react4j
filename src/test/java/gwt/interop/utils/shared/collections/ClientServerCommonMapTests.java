package gwt.interop.utils.shared.collections;

public class ClientServerCommonMapTests {
    public static void run() {

        StringMap<String> m1 = StringMapFactory.create();

        /* put, get, hasKey */
        m1.put("V1", "Value1");
        assert(m1.get("V1").equals("Value1"));
        assert(m1.get("V2") == null);
        assert(m1.hasKey("V1"));
        assert(!m1.hasKey("V12"));

        m1 = StringMapFactory.create();
        m1.put("V1", "Value1");
        m1.put("V1", "Value1.1");
        m1.put("V2", "Value2");

        /* keys */
        Array<String> mapKeys = m1.keys();
        assert(toString(mapKeys).equals("V1,V2"));

        /* values */
        Array<String> mapValues = m1.values();
        assert(toString(mapValues).equals("Value1.1,Value2"));

        /* remove */
        m1 = StringMapFactory.create();
        m1.put("V1", "Value1");
        m1.put("V2", "Value2");

        assert(m1.get("V1").equals("Value1"));
        m1.remove("V1");
        assert(m1.get("V1") == null);
        //Should be no-op
        m1.remove("V1");
        assert(m1.get("V1") == null);
    }

    private static String toString(Array<String> a) {
        return a.join(",");
    }
}
