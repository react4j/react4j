package react4j.processor;

import java.util.ArrayList;
import java.util.List;

final class BuilderDescriptor {

    private final List<Step> _steps = new ArrayList<>();

    BuilderDescriptor() {}

    Step addStep() {
        final var step = new Step(_steps.size() + 1);
        getSteps().add(step);
        return step;
    }

    List<Step> getSteps() {
        return _steps;
    }
}
