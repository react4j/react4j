package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DoubleOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( double oldValue )
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
