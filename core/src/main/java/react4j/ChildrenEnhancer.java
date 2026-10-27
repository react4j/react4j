package react4j;

import java.util.stream.Stream;

/**
 * Fluent conversions for a children array input.
 *
 * @param <R> the result of setting the children input.
 */
public interface ChildrenEnhancer<R> {
    R children(ReactNode... children);

    default R children(Stream<? extends ReactNode> children) {
        return children(children.toArray(ReactNode[]::new));
    }
}
