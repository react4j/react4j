package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class BadType1OnPropChange
{
  @OnInputChange
  void onMyPropChange( int myProp )
  {
  }

  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
