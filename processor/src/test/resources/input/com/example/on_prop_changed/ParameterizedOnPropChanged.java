package com.example.on_prop_changed;

import java.util.ArrayList;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ParameterizedOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( ArrayList<String> oldValue )
  {
  }

  @Prop
  protected abstract ArrayList<String> getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
