package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class IntOnPropChange
{
  @OnPropChange
  void onMyPropChange( int myProp )
  {
  }

  @Prop
  abstract int getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
