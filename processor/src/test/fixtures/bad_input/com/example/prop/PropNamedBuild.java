package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PropNamedBuild
{
  @Input
  protected abstract String getBuild();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
