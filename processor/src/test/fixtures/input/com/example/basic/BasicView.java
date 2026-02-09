package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class BasicView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
