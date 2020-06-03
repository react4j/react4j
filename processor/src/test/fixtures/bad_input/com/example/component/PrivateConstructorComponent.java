package com.example.component;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class PrivateConstructorComponent
{
  private PrivateConstructorComponent()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
