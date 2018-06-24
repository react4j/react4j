package com.example.state;

import java.io.IOException;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;

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
