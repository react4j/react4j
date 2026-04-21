package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MultiContextPropModel
{
  @Nullable
  @Input( fromTreeContext = true )
  abstract String getStringContextValue();

  @Input( fromTreeContext = true )
  abstract int getIntContextValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
