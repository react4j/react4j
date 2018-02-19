package com.example.state;

import java.io.IOException;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class SetterThrows
  extends Component
{
  @State
  protected abstract String getMyKey();

  @State
  protected abstract void setMyKey( String value )
    throws IOException;

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
