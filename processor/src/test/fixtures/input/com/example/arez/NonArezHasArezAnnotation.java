package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class NonArezHasArezAnnotation
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Action
  void handleFoo()
  {
  }
}
