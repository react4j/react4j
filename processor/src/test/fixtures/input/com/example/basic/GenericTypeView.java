package com.example.basic;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class GenericTypeView<T>
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
