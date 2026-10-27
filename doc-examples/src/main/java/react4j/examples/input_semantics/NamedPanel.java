package react4j.examples.input_semantics;

import java.util.Locale;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View(type = View.Type.STATEFUL)
abstract class NamedPanel {
    private final String _name;

    NamedPanel(@Input(keyGenerator = LowerCaseKeys.class) final String name) {
        _name = name.toLowerCase(Locale.ROOT);
    }

    @Render
    ReactNode render() {
        return ReactNode.of(_name);
    }
}
