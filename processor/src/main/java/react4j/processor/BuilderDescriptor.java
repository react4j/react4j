package react4j.processor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

final class BuilderDescriptor
{
  @Nonnull
  private final List<Step> _steps = new ArrayList<>();

  BuilderDescriptor()
  {
  }

  @Nonnull
  Step addStep()
  {
    final Step step = new Step( _steps.size() + 1 );
    getSteps().add( step );
    return step;
  }

  @Nonnull
  List<Step> getSteps()
  {
    return _steps;
  }
}
