package com.example.state;

import java.text.ParseException;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class GetterThrows
  extends Component
{
  @State
  protected abstract String getMyKey()
    throws ParseException;

  @State
  protected abstract void setMyKey( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
