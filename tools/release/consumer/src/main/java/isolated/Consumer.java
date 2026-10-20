package isolated;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.jspecify.annotations.NullMarked;
import react4j.dom.DOM;

@NullMarked
public final class Consumer {
    private Consumer() {}

    public static void main(String[] args) {
        var factory = Sting_React4j_SampleViewFactory.create("service");
        if (!"React4j_SampleViewFactory".equals(factory.getClass().getSimpleName())) {
            throw new AssertionError("Packaged processor integration");
        }
        var methods = Arrays.stream(DOM.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .count();
        if (529 != methods || 0 == SampleBuilder.class.getDeclaredMethods().length) {
            throw new AssertionError("Packaged generated DOM/view APIs");
        }
    }
}
