package gwt.interop.utils.shared.collections;

public class ArrayFactory {

    public interface ArrayConstructor {
        <T> Array<T> create();
    }

    private static ArrayConstructor arrayConstructor;

    public static void setConstructor(ArrayConstructor arrayConstructor) {
        ArrayFactory.arrayConstructor = arrayConstructor;
    }
    public static <T> Array<T> create() {
        assert arrayConstructor != null : "You need to call ArrayFactory.setConstructor first";
        return arrayConstructor.create();
    }
}
