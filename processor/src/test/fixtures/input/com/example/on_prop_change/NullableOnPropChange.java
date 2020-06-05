package com.example.on_prop_change;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NullableOnPropChange
{
  @OnInputChange
  void onMyPropChange( @Nullable String myProp )
  {
  }

  @Input
  @Nullable
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
