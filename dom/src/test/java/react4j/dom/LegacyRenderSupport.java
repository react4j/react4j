package react4j.dom;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

@NullMarked
public final class LegacyRenderSupport {
    private LegacyRenderSupport() {}

    public static void render(@Nullable ReactNode node, Object container) {
        Renderer.render(node, Renderer.requireContainer(container), null);
    }
}
