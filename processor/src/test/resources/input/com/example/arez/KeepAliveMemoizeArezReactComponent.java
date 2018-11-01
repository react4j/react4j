package com.example.arez;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class KeepAliveMemoizeArezReactComponent
  extends ReactArezComponent
{
  @Memoize( keepAlive = true )
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
