package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class Suppressed2ProtectedAccessOnPropChangeModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
  @OnPropChange
  protected void onMyPropChange( String myProp )
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
