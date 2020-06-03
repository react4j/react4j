package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class MissingPropOnPropChange
{
  @OnPropChange
  void onMyPropChange( String myProp )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
