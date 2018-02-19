package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BadName
  extends Component
{
  @State( name = "-key" )
  protected abstract String getMyKey();

  @State( name = "-key" )
  protected abstract void setMyKey( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
