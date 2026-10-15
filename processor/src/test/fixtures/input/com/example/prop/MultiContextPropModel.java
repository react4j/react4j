package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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
