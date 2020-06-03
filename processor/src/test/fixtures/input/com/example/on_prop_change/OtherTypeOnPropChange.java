package com.example.on_prop_change;

import java.util.BitSet;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class OtherTypeOnPropChange
{
  @OnPropChange
  void onMyPropChange( BitSet myProp )
  {
  }

  @Prop
  abstract BitSet getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
