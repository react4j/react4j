package com.example.on_prop_change.other;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessOnPropChangeModel
{
  @OnInputChange
  protected void onMyPropChange( String myProp )
  {
  }

  @Input
  protected abstract String getMyProp();

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
