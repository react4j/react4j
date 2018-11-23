package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DoubleOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( double myProp )
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
