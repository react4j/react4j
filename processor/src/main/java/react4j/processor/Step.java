package react4j.processor;

import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class Step
{
  @Nonnull
  private final BuilderDescriptor _builder;
  private final int _index;
  private final ArrayList<StepMethod> _methods = new ArrayList<>();

  Step( @Nonnull final BuilderDescriptor builder, final int index )
  {
    _builder = Objects.requireNonNull( builder );
    _index = index;
  }

  @Nonnull
  BuilderDescriptor getBuilder()
  {
    return _builder;
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
