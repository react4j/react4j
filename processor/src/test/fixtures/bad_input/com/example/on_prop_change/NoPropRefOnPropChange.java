package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class NoPropRefOnPropChange
{
  @OnPropChange
  void onMyPropChange()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
