package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonnullChildPropComponent
  extends Component
{
  @Nonnull
  @Prop
  protected abstract ReactNode getChild();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
