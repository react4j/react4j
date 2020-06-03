package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class GenericTypeMultiPropModel<T>
{
  @Prop
  abstract T getValue();

  @Prop
  abstract String getValue2();

  @Prop
  @Nullable
  abstract String getValue3();

  @Prop
  @Nullable
  abstract String getValue4();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
