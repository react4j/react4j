package isolated;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.jspecify.annotations.NullMarked;
import react4j.Keyed;
import react4j.React;
import react4j.dom.DOM;

@NullMarked
public final class Consumer {
    private Consumer() {}

    public static void main(String[] args) {
        Keyed keyed = () -> "archive-key";
        if (!"archive-key".equals(Keyed.getKey(keyed)) || Keyed.getKey(new Object()) != null) {
            throw new AssertionError("Packaged runtime key API");
        }
        if (!React.shouldCheckInvariants() || !React.shouldValidateInputValues() || React.shouldMinimizeInputKeys()) {
            throw new AssertionError("Packaged development configuration");
        }
        var factory = Sting_React4j_SampleViewFactory.create("service");
        if (!factory.getClass().getSimpleName().equals("React4j_SampleViewFactory")) {
            throw new AssertionError("Packaged processor integration");
        }
        var methods = Arrays.stream(DOM.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .count();
        if (methods != 529 || SampleBuilder.class.getDeclaredMethods().length == 0) {
            throw new AssertionError("Packaged generated DOM/view APIs");
        }
    }
}
