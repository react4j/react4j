package com.example.arez;

import arez.Disposable;
import arez.annotations.CascadeDispose;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NonArezHasArezFieldAnnotation
{
  @CascadeDispose
  Disposable _field;

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
