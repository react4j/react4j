package com.example.arez.computed;

import arez.annotations.Computed;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ParameterizedReturnComputedComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Computed
  <T> Consumer<T> isActive()
  {
    return null;
  }
}
