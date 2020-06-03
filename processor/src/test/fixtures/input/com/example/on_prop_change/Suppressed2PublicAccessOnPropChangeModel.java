package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@View
abstract class Suppressed2PublicAccessOnPropChangeModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @OnPropChange
  public void onMyPropChange( String myProp )
  {
  }

  @Prop
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
