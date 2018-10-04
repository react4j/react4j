package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BoxedPrimitiveBuild
  extends Component
{
  @Prop
  @Nonnull
  protected abstract Integer myValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
