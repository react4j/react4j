package com.example.default_props;

import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class PrivateMethodPropDefault
  extends Component<BaseState>
{
  @PropDefault
  private static String getMyPropDefault()
  {
    return "Foo";
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
