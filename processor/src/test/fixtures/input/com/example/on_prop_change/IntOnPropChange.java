package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class IntOnPropChange
{
  @OnInputChange
  void onMyPropChange( int myProp )
  {
  }

  @Input
  abstract int getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
