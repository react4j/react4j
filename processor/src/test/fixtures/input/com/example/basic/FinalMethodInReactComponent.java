package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
public abstract class FinalMethodInReactComponent
{
  final void someMethod()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
