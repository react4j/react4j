package com.example.arez;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class BasicArezReactComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
