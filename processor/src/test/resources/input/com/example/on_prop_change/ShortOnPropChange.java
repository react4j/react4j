package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ShortOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( short oldValue )
  {
  }

  @Prop
  protected abstract short getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
