package com.example.on_prop_change;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsOnPropChange
{
  @OnInputChange
  void onMyPropChange( String myProp )
    throws IOException
  {
  }

  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
