package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentId;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeArezComponentWithExplicitRequireId
{
  @ArezComponent
  static abstract class MyComponent
  {
    @ComponentId
    int componentId()
    {
      return 0;
    }

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
