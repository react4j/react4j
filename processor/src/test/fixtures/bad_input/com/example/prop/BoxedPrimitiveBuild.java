package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class BoxedPrimitiveBuild
{
  @Prop
  @Nonnull
  protected abstract Integer myValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
