package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ReturnOnPropChanged
  extends Component
{
  @OnPropChanged
  int onMyPropChanged( String oldValue )
  {
    return 0;
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
