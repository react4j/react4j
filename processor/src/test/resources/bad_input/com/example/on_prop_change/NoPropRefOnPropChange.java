package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NoPropRefOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
