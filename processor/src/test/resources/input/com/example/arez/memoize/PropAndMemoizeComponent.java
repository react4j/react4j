package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class PropAndMemoizeComponent
  extends ReactArezComponent
{
  @Prop
  protected abstract String getValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize
  public boolean isActive()
  {
    return true;
  }
}
