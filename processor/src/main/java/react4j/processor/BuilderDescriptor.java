package react4j.processor;

import java.util.ArrayList;
import javax.annotation.Nonnull;

final class BuilderDescriptor
{
  private final ArrayList<Step> _steps = new ArrayList<>();

  BuilderDescriptor()
  {
  }

  @Nonnull
  Step addStep()
  {
    final Step step = new Step( this, _steps.size() + 1 );
    getSteps().add( step );
    return step;
  }

  ArrayList<Step> getSteps()
  {
    return _steps;
  }
}
