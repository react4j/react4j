package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
public abstract class FinalMethodInView
{
  final void someMethod()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
