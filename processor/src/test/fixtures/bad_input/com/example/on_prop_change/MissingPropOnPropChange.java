package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MissingPropOnPropChange
{
  @OnInputChange
  void onMyPropChange( String myProp )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
