package com.example.on_prop_change;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsOnPropChange
{
  @OnPropChange
  void onMyPropChange( String myProp )
    throws IOException
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
