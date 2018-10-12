package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CharOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( char oldValue )
  {
  }

  @Prop
  protected abstract char getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
