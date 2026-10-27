package react4j;

/** A renderable that builds a node when supplied to a composition API. */
@FunctionalInterface
public interface Buildable extends Renderable {
    ReactNode build();
}
