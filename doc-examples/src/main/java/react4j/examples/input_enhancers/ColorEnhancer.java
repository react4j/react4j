package react4j.examples.input_enhancers;

public interface ColorEnhancer<R> {
    R color(String value);

    default R red() {
        return color("red");
    }
}
