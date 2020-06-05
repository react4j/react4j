package react4j.processor;

import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

final class Step
{
  private final int _index;
  @Nonnull
  private final List<StepMethod> _methods = new ArrayList<>();

  Step( final int index )
  {
    _index = index;
  }

  int getIndex()
  {
    return _index;
  }

  void addMethod( @Nonnull final InputDescriptor input, @Nonnull final StepMethodType stepMethodType )
  {
    getMethods().add( new StepMethod( input, stepMethodType ) );
  }

  void addTerminalMethod( @Nonnull final String name, @Nonnull final String key, @Nonnull final TypeName type )
  {
    getMethods().add( new StepMethod( name, key, type, null, StepMethodType.TERMINATE ) );
  }

  @Nonnull
  List<StepMethod> getMethods()
  {
    return _methods;
  }
}
