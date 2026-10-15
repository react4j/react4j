package com.example.on_prop_change;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class Suppressed2ProtectedAccessOnPropChangeModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
  @OnInputChange
  protected void onMyPropChange( @Nullable String myProp )
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
