package com.example.default_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

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
