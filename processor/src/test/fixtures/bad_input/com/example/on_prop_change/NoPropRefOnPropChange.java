package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
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
