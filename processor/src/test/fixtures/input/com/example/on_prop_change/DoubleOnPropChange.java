package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class DoubleOnPropChange
{
  @OnInputChange
  void onMyPropChange( double myProp )
  {
  }

  @Input
  abstract double getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
