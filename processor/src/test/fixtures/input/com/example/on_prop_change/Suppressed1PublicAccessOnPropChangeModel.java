package com.example.on_prop_change;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1PublicAccessOnPropChangeModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @OnInputChange
  public void onMyPropChange( String myProp )
  {
  }

  @Nullable
  @Input
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
