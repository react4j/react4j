package com.example.arez;

import arez.annotations.Memoize;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class InputAndMemoizeComponent
{
  @Nullable
  @Input
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
