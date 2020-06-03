package com.example.render;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class StaticComponent
{
  @Render
  static ReactNode render()
  {
    return null;
  }
}
