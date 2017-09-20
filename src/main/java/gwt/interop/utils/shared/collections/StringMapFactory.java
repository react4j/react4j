package gwt.interop.utils.shared.collections;


public class StringMapFactory {

    public interface MapConstructor {
        <T> StringMap<T> create();
    }

    private static MapConstructor mapConstructor;

    public static void setConstructor(MapConstructor mapConstructor) {
        StringMapFactory.mapConstructor = mapConstructor;
    }

    public static <T> StringMap<T> create() {
        assert mapConstructor != null : "You need to call StringMapFactory.setConstructor first";
        return mapConstructor.create();
    }
}
