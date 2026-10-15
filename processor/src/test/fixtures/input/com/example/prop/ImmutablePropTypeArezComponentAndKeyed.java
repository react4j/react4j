package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ImmutablePropTypeArezComponentAndKeyed
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

    public String getKey()
    {
      return "";
    }
  }

  ImmutablePropTypeArezComponentAndKeyed( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
