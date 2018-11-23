package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CharOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( char oldValue )
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
