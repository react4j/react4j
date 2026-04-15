package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeArezComponent
{
  @ArezComponent( requireId = Feature.ENABLE )
  static abstract class MyComponent
  {
    @Action
    void myAction()
    {
    }
  }

  ImmutablePropTypeArezComponent( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
