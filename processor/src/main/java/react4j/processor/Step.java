package react4j.processor;

import com.palantir.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;

final class Step {
    private final int _index;

    private final List<StepMethod> _methods = new ArrayList<>();

    Step(final int index) {
        _index = index;
    }

    int getIndex() {
        return _index;
    }

    void addMethod(final InputDescriptor input, final StepMethodType stepMethodType) {
        getMethods().add(new StepMethod(input, stepMethodType));
    }

    void addTerminalMethod(final String name, final TypeName type) {
        getMethods().add(new StepMethod(name, type, null, StepMethodType.TERMINATE));
    }

    List<StepMethod> getMethods() {
        return _methods;
    }
}
