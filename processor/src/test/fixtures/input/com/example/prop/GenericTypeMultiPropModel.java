package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class GenericTypeMultiPropModel<T>
{
  @Nullable
  @Input
  abstract T getValue();

  @Nullable
  @Input
  abstract String getValue2();

  @Input
  @Nullable
  abstract String getValue3();

  @Input
  @Nullable
  abstract String getValue4();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
