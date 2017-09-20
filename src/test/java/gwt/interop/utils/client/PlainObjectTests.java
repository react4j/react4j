package gwt.interop.utils.client;

import com.google.gwt.junit.client.GWTTestCase;
import gwt.interop.utils.client.collections.JsArray;
import gwt.interop.utils.client.collections.JsStringMap;
import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.interop.utils.shared.collections.ArrayFactory;
import gwt.interop.utils.shared.collections.StringMapFactory;
import gwt.interop.utils.shared.plainobjects.CommonDataObject;

import static gwt.interop.utils.client.plainobjects.JsPlainObj.$;
import static gwt.interop.utils.client.plainobjects.JsPlainObj.$jsPlainObj;


public class PlainObjectTests extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "gwt.interop.utils.InteropUtils";
    }

    public void testInitPlainObject() {
        JsPlainObj o1 = $jsPlainObj("a", 1, "b", 10, "c", 20);
        assertEquals(o1.toJSONString(), "{\"a\":1,\"b\":10,\"c\":20}");
    }

    public void testInitPlainObjectAllPrimitiveTypes() {
        JsPlainObj o1 = $jsPlainObj(
                "a", 1,
                "a1", Integer.valueOf(1),
                "b", 10.1,
                "b1", Double.valueOf(10.1),
                "c", true,
                "c1", Boolean.valueOf(true),
                "d","stringvalue"
                );
        assertEquals(o1.toJSONString(), "{\"a\":1,\"a1\":1,\"b\":10.1,\"b1\":10.1,\"c\":true,\"c1\":true,\"d\":\"stringvalue\"}");
    }

    public void testInitPlainObjectWithSuppliedObject() {
        JsPlainObj o1 = $(new JsPlainObj(), "a", 2, "b", 20, "c", 30);

        assertEquals(o1.toJSONString(), "{\"a\":2,\"b\":20,\"c\":30}");
    }

    public void testConstructPlainObject() {
        JsPlainObj o2 = new JsPlainObj();
        o2.set("b", 2);
        o2.set("d", 3);

        assertEquals(o2.toJSONString(), "{\"b\":2,\"d\":3}");
    }

    public void testMerge() {
        JsPlainObj o1 = $jsPlainObj("a", 1, "b", 10, "c", 20);
        JsPlainObj o2 = $jsPlainObj("b", 2, "d", 3);
        JsPlainObj o3 = o1.merge(o2);

        assertEquals(o3.toJSONString(), "{\"a\":1,\"b\":2,\"c\":20,\"d\":3}");
    }

    public void testExcept() {
        JsPlainObj o1 = $jsPlainObj("a", 1, "b", 10, "c", 20);

        JsPlainObj o2 = o1.except("a","c");
        assertEquals(o2.toJSONString(), "{\"b\":10}");
    }

    public void testComplexPlainObjectToJson() {
        StringMapFactory.setConstructor(JsStringMap::create);
        ArrayFactory.setConstructor(JsArray::create);

        CommonDataObject commonObj = CommonDataObject.create();
        String json = JSON.stringify(commonObj);

        assertEquals(json, "{\"intVal\":10,\"doubleVal\":20.2,\"doubleObjVal\":20.2,\"booleanVal\":true,\"booleanObjVal\":true,\"stringVal\":\"A String Value\",\"anArray\":[\"ArrayValue1\",\"ArrayValue2\",\"ArrayValue3\"],\"aMap\":{\"v1\":\"A Map Value 1\",\"v2\":\"A Map Value 2\"},\"embeddedObj\":{\"field1\":\"An embbeded object\"}}");
    }

    public void testComplexPlainObjectFromJson() {
        StringMapFactory.setConstructor(JsStringMap::create);
        ArrayFactory.setConstructor(JsArray::create);

        CommonDataObject commonObj = JSON.parse("{\"intVal\":10,\"doubleVal\":20.2,\"doubleObjVal\":20.2,\"booleanVal\":true,\"booleanObjVal\":true,\"stringVal\":\"A String Value\",\"numberVal\":10,\"anArray\":[\"ArrayValue1\",\"ArrayValue2\",\"ArrayValue3\"],\"aMap\":{\"v1\":\"A Map Value 1\",\"v2\":\"A Map Value 2\"},\"embeddedObj\":{\"field1\":\"An embbeded object\"}}");
        assertEquals(commonObj.intVal, 10);
        assertEquals(commonObj.doubleVal,20.2);
        assertEquals(commonObj.doubleObjVal,20.2);
        assertEquals(commonObj.booleanVal,true);
        assertEquals(commonObj.booleanObjVal.booleanValue(),true);
        assertEquals(commonObj.stringVal,"A String Value");
        assertEquals(commonObj.aMap.get("v1"), "A Map Value 1");
        assertEquals(commonObj.anArray.get(1), "ArrayValue2");

        //Currently crashes, compiler issue (being fixed)
        //assertEquals(commonObj.convolutedSharedMethod("v2"), "ddd");
    }
}