package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Suppressed1ProtectedAccessOnPropChangeModel
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedMethod" )
  @OnPropChange
  protected void onMyPropChange( String myProp )
  {
  }

  @Prop
  abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}