package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class SetterIsPrivate
  extends Component
{
  @State
  protected abstract String getMyKey();

  @State
  private void setMyKey( String value )
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
