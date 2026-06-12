package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class NoPropRefOnPropChange
{
  @OnInputChange
  void onMyPropChange()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
