package com.example.on_prop_change;

import java.util.BitSet;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class OtherTypeOnPropChange
{
  @OnInputChange
  void onMyPropChange( BitSet myProp )
  {
  }

  @Input
  abstract BitSet getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
