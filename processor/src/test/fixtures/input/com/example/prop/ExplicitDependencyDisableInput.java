package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ExplicitDependencyDisableInput
{
  @ArezComponent( allowEmpty = true )
  static abstract class MyComponent
  {
  }

  @Nullable
  @Input( dependency = Feature.DISABLE )
  abstract MyComponent getMyComponent();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
