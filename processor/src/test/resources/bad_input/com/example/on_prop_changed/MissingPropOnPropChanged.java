package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MissingPropOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( String oldValue )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
