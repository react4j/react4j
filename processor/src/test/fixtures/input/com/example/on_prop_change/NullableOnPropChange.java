package com.example.on_prop_change;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NullableOnPropChange
{
  @OnPropChange
  void onMyPropChange( @Nullable String myProp )
  {
  }

  @Prop
  @Nullable
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
