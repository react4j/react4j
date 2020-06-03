package com.example.prop;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ArezAnnotatedProp
{
  @Memoize
  @Prop
  protected abstract String getMyKey();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
