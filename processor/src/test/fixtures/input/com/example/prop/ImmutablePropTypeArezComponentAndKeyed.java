package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeArezComponentAndKeyed
  extends Component
{
  @ArezComponent( requireId = Feature.ENABLE )
  static abstract class MyComponent
    implements Keyed
  {
    @Action
    void myAction()
    {
    }

    // Keyed should win over ArezId
    @Nonnull
    @Override
    public String getKey()
    {
      return "";
    }
  }

  @Prop( immutable = true )
  abstract MyComponent getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
