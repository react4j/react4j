package react4j.examples.input_enhancers;

import react4j.ChildrenEnhancer;

public interface MenuChildren<R> extends ChildrenEnhancer<R> {
    default R empty() {
        return children();
    }
}
