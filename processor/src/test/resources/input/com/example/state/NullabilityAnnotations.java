package com.example.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;

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
