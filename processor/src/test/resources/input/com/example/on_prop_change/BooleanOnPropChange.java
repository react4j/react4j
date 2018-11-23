package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BooleanOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( boolean myProp )
  {
  }

  @Prop
  protected abstract boolean getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
