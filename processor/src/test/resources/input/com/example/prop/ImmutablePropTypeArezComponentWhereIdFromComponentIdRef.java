package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentIdRef;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeArezComponentWhereIdFromComponentIdRef
  extends Component
{
  @ArezComponent
  static abstract class MyComponent
  {
    @ComponentIdRef
    abstract int componentId();

    @Action
    void myAction()
    {
    }
  }

  @Prop( immutable = true )
  protected abstract MyComponent getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
