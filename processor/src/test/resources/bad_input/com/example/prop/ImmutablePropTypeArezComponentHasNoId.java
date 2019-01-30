package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeArezComponentHasNoId
  extends Component
{
  @ArezComponent
  static abstract class MyComponent
  {
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
