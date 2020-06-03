package com.example.basic;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class GenericTypeComponent<T>
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
