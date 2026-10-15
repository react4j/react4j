package com.example.basic;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class GenericTypeView<T>
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
