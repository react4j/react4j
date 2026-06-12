package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class PropHasParameterComponent
{
  @Input
  protected abstract String getMyProp( int i );

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
