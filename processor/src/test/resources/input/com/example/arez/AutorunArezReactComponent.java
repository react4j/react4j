package com.example.arez;

import arez.annotations.Observed;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class AutorunArezReactComponent
  extends ReactArezComponent
{
  @Observed
  void myAutorun()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
