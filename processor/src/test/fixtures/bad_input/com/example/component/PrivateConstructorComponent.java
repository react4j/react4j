package com.example.component;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
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
