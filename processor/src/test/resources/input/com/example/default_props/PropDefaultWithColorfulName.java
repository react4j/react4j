package com.example.default_props;

import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class PropDefaultWithColorfulName
  extends Component
{
  @PropDefault
  static final String DEFAULT_MY_PROP12$23 = "Foo";

  @Prop
  protected abstract String getMyProp12$23();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
