package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.PropRef;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ExplicitNameOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( @PropRef( "myProp" ) double zzzz )
  {
  }

  @Prop
  protected abstract double getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
