package com.example.default_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MissingPropFieldPropDefault
  extends Component
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
