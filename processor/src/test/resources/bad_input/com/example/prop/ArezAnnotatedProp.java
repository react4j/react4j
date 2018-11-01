package com.example.prop;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezAnnotatedProp
  extends ReactArezComponent
{
  @Memoize
  @Prop
  protected abstract String getMyKey();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
