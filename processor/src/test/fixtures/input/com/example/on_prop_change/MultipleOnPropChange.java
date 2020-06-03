package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MultipleOnPropChange
{
  @OnPropChange
  void onPropChange( boolean myProp1, String myProp2, int myProp3 )
  {
  }

  @Prop
  abstract boolean getMyProp1();

  @Prop
  abstract String getMyProp2();

  @Prop
  abstract int getMyProp3();

  @Render
  ReactNode render()
  {
    return null;
  }
}
