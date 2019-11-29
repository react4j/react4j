package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Suppressed1PublicAccessOnPropChangeModel
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicLifecycleMethod" )
  @OnPropChange
  public void onMyPropChange( String myProp )
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
