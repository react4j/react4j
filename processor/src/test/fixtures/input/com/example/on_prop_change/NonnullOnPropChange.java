package com.example.on_prop_change;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NonnullOnPropChange
{
  @OnInputChange
  void onMyPropChange( @Nonnull String myProp )
  {
  }

  @Input
  @Nonnull
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
