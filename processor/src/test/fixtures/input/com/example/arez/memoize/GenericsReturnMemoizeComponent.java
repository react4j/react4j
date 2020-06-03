package com.example.arez.memoize;

import arez.annotations.Memoize;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class GenericsReturnMemoizeComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Memoize
  Consumer<String> getIcon( String key )
  {
    return null;
  }
}
