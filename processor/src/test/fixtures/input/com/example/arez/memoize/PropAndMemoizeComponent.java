package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
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
