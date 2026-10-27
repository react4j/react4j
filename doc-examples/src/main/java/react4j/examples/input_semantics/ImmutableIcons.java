package react4j.examples.input_semantics;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View(type = View.Type.STATEFUL)
abstract class ImmutableIcons {
    private final String[] _icons;

    ImmutableIcons(@Input final String[] icons) {
        _icons = icons;
    }

    @Render
    ReactNode render() {
        return ReactNode.of(String.join(",", _icons));
    }
}
