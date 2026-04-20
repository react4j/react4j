package com.example.input;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class InputOrderingView
{
  InputOrderingView( @Input boolean paramZ, @Input boolean paramP )
  {
  }

  @Input
  abstract boolean paramB();

  @Input
  abstract boolean paramC();

  @Input
  abstract boolean paramA();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
