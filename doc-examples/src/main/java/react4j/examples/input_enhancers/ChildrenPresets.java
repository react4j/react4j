package react4j.examples.input_enhancers;

import java.util.stream.Stream;
import react4j.ReactNode;

public final class ChildrenPresets {
    private ChildrenPresets() {}

    public static ReactNode titledPanel() {
        return ChildrenPanelBuilder.title("Items").children(Stream.of(ReactNode.of("first"), ReactNode.of("second")));
    }

    public static ReactNode untitledPanel() {
        return ChildrenPanelBuilder.children(Stream.of(ReactNode.of("only child")));
    }

    public static ReactNode emptyMenu() {
        return MenuPanelBuilder.empty();
    }
}
