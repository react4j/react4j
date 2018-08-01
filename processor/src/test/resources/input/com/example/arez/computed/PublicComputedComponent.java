package com.example.arez.computed;

import arez.annotations.Computed;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class PublicComputedComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Computed
  public boolean isActive()
  {
    return true;
  }
}
