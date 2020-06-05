package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PostUpdateOnPropChange
{
  @OnInputChange( phase = OnInputChange.Phase.POST )
  void onMyPropChange( String myProp )
  {
  }

  @Input
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
