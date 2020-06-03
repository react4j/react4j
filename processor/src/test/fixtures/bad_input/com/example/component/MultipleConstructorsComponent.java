package com.example.component;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MultipleConstructorsComponent
{
  MultipleConstructorsComponent( final String ignored )
  {
  }

  MultipleConstructorsComponent()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
