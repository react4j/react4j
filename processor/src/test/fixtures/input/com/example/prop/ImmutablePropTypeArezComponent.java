package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
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

  @Prop( immutable = true )
  abstract MyComponent getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
