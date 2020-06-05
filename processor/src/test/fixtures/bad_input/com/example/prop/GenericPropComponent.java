package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class GenericPropComponent
{
  @Input
  protected abstract <T> T getValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
