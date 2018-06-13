package com.example.arez;

import arez.annotations.Computed;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class KeepAliveComputedArezReactComponent
  extends ReactArezComponent
{
  @Computed( keepAlive = true )
  int myAutorun()
  {
    return 0;
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
