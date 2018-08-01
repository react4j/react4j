package com.example.arez.memoize;

import arez.annotations.Memoize;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ParameterizedReturnMemoizeComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize
  <T> Consumer<T> getIcon( String key )
  {
    return null;
  }
}
