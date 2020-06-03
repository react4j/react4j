package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
public abstract class FinalMethodInView
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
