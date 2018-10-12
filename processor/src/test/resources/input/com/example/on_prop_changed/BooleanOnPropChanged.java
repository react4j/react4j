package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BooleanOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( boolean oldValue )
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
