package com.example.prop;

import arez.annotations.ArezComponent;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class ArezPropModel
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Nullable
  @Input
  abstract String getValue();

  @Nullable
  @Input
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
