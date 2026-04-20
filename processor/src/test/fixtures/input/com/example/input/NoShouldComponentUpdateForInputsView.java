package com.example.input;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NoShouldComponentUpdateForInputsView
{
  NoShouldComponentUpdateForInputsView( @Input boolean paramZ, @Input boolean paramP )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
