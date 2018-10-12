package com.example.on_prop_changed;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ByteOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( byte oldValue )
  {
  }

  @Prop
  protected abstract byte getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
