package com.example.state;

import java.text.ParseException;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;

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
