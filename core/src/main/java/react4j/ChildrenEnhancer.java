package react4j;

import java.util.stream.Stream;

/**
 * Fluent conversions for a children array input.
 *
 * @param <R> the result of setting the children input.
 */
public interface ChildrenEnhancer<R> {
    R children(Renderable... children);

    default R children(Stream<? extends Renderable> children) {
        return children(children.toArray(Renderable[]::new));
    }
}
