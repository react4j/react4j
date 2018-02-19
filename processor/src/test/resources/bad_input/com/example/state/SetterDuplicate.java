package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class SetterDuplicate
  extends Component
{
  @State
  protected abstract String getMyKey();

  @State( name = "myKey" )
  protected abstract void setMyKey2( String value );

  @State
  protected abstract void setMyKey( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
