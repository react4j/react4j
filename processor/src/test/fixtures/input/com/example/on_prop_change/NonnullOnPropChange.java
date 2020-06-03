package com.example.on_prop_change;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NonnullOnPropChange
{
  @OnPropChange
  void onMyPropChange( @Nonnull String myProp )
  {
  }

  @Prop
  @Nonnull
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
