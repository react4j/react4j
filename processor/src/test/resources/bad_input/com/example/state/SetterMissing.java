package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class SetterMissing
  extends Component
{
  @State
  protected abstract String getMyKey();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
