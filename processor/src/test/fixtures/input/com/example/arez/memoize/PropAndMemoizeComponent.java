package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class PropAndMemoizeComponent
{
  @Prop
  abstract String getValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Memoize
  boolean isActive()
  {
    return true;
  }
}
