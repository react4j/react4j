package react4j.examples.input_semantics;

import arez.ObjectsDeepEqualsComparator;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View(type = View.Type.STATEFUL)
abstract class MutableIcons {
    @Input(equalityComparator = ObjectsDeepEqualsComparator.class)
    abstract String[] icons();

    @Render
    ReactNode render() {
        return ReactNode.of(String.join(",", icons()));
    }
}
