package react4j.examples.input_enhancers;

import java.util.stream.Stream;
import react4j.ReactNode;

public final class ChildrenPresets {
    private ChildrenPresets() {}

    public static ReactNode titledPanel() {
        return ChildrenPanelBuilder.title("Items")
                .children(Stream.of(NoticeBuilder.message("Saved"), NoticeBuilder.message("Synchronized")));
    }

    public static ReactNode untitledPanel() {
        return ChildrenPanelBuilder.children(Stream.of(NoticeBuilder.message("Saved")));
    }

    public static ReactNode emptyMenu() {
        return MenuPanelBuilder.empty();
    }
}
