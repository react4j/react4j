package com.example.on_prop_change.other;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessOnPropChangeModel
{
  @OnPropChange
  protected void onMyPropChange( String myProp )
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
