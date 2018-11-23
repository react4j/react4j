package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( String oldValue )
  {
  }

  @OnPropChange( name = "myProp" )
  void onMyPropChanged2( String oldValue )
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
