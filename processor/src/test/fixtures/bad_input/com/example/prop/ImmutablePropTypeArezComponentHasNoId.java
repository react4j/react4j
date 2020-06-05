package com.example.prop;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeArezComponentHasNoId
{
  @ArezComponent( allowEmpty = true, requireId = Feature.DISABLE )
  static abstract class MyComponent
  {
  }

  @Input( immutable = true )
  protected abstract MyComponent getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
