package com.example.on_prop_change;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class BadNullability1OnPropChange
{
  @OnInputChange
  void onMyPropChange( String myProp )
  {
  }

  @Nullable
  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
