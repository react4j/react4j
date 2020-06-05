package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MultiContextPropModel
{
  @Input( source = Input.Source.CONTEXT )
  abstract String getStringContextValue();

  @Input( source = Input.Source.CONTEXT )
  abstract int getIntContextValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
