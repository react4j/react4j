package com.example.render;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class MethodParameterComponent
{
  @Render
  ReactNode render(int i)
  {
    return null;
  }
}
