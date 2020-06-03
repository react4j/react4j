package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ImmutableOnPropChange
{
  @OnPropChange
  void onMyPropChange( String myProp )
  {
  }

  @Prop( immutable = true )
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
