package com.example.state;

import arez.annotations.Computed;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezOnGetterName
  extends ReactArezComponent
{
  @Computed
  protected abstract String getMyKey();

  @State
  protected abstract void setMyKey( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
