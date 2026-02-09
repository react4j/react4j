package com.example.on_prop_change.other;

import javax.annotation.Nullable;
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

  @Nullable
  @Input
  protected abstract String getMyProp();

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
