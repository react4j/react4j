package com.example.default_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadTypeMethodPropDefault
  extends Component
{
  @PropDefault
  static int getMyPropDefault()
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
