package com.example.on_prop_change.other;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;

@org.jspecify.annotations.NullMarked
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
