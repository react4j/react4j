package react4j.processor;

import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class Step
{
  private final int _index;
  private final ArrayList<StepMethod> _methods = new ArrayList<>();

  Step( final int index )
  {
    _index = index;
  }

  int getIndex()
  {
    return _index;
  }

  void addStep( @Nonnull final String name,
                @Nonnull final String key,
                @Nonnull final TypeName type,
                @Nullable final ExecutableElement propMethod,
                @Nullable final ExecutableType propMethodType,
                @Nonnull final StepMethodType stepMethodType )
  {
    getMethods().add( new StepMethod( name, key, type, propMethod, propMethodType, stepMethodType ) );
  }

  ArrayList<StepMethod> getMethods()
  {
    return _methods;
  }
}
