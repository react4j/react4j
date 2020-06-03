package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class DoubleOnPropChange
{
  @OnPropChange
  void onMyPropChange( double myProp )
  {
  }

  @Prop
  abstract double getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
