package com.example.component;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
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
