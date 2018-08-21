package com.example.state;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezOnSetterName
  extends ReactArezComponent
{
  @State
  protected abstract String getMyKey();

  @Action
  protected abstract void setMyKey( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
