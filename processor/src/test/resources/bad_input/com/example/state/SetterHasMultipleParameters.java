package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class SetterHasMultipleParameters
  extends Component
{
  @State
  protected abstract String getMyKey();

  @State
  protected abstract void setMyKey( int i, String v );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
