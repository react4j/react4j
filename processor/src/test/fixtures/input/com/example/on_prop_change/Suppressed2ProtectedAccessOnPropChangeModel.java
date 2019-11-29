package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class Suppressed2ProtectedAccessOnPropChangeModel
  extends Component
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedLifecycleMethod" )
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
