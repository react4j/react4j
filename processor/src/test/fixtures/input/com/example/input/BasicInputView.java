package com.example.input;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class BasicInputView
{
  @Input
  abstract boolean isSomeField();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
