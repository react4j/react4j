package com.example.arez.memoize;

import arez.annotations.Memoize;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ParameterizedReturnMemoizeComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Memoize
  <T> Consumer<T> getIcon( String key )
  {
    return null;
  }
}
