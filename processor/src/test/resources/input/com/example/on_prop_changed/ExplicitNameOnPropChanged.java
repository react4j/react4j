package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ExplicitNameOnPropChanged
  extends Component
{
  @OnPropChanged( name = "myProp" )
  void zzzz()
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
