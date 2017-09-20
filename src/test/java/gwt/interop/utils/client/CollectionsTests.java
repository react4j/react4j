package gwt.interop.utils.client;

import com.google.gwt.junit.client.GWTTestCase;
import gwt.interop.utils.client.collections.JsArray;
import gwt.interop.utils.client.collections.JsStringMap;
import gwt.interop.utils.shared.collections.*;

public class CollectionsTests extends GWTTestCase {

    public String getModuleName() {
        return "gwt.interop.utils.InteropUtils";
    }

    public void testMap() {
        StringMapFactory.setConstructor(JsStringMap::create);

        ClientServerCommonMapTests.run();
    }

    public void testArray() {
        ArrayFactory.setConstructor(JsArray::create);
        ClientServerCommonArrayTests.run();
    }
}
