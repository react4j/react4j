package com.example.input;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BasicInputView
{
  @Input
  abstract boolean isSomeField();

  @Render
  ReactNode render()
  {
    return null;
  }
}
