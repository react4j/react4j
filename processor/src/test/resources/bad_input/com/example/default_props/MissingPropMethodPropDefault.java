package com.example.default_props;

import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class MissingPropMethodPropDefault
  extends Component
{
  @PropDefault
  static String getMyPropDefault()
  {
    return "Foo";
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
