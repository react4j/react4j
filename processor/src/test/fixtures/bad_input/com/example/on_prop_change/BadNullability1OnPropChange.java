package com.example.on_prop_change;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BadNullability1OnPropChange
{
  @OnPropChange
  void onMyPropChange( @Nonnull String myProp )
  {
  }

  @Nullable
  @Prop
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
