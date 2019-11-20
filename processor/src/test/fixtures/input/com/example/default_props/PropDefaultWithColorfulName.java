package com.example.default_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PropDefaultWithColorfulName
  extends Component
{
  @PropDefault
  static final String DEFAULT_MY_PROP12$23 = "Foo";

  @Prop
  abstract String getMyProp12$23();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
