package com.example.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class NullabilityAnnotations
  extends Component
{
  @Nonnull
  @State
  protected abstract String getMyValue();

  @State
  protected abstract void setMyValue( @Nonnull String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
