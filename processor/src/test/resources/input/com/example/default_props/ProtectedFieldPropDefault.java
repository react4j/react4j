package com.example.default_props;

import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class ProtectedFieldPropDefault
  extends Component<BaseState>
{
  @PropDefault
  protected static final String DEFAULT_MY_PROP = "Foo";

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
