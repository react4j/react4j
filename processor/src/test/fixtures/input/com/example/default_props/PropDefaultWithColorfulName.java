package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PropDefaultWithColorfulName
{
  @PropDefault
  static final String DEFAULT_MY_PROP12$23 = "Foo";

  @Prop
  abstract String getMyProp12$23();

  @Render
  ReactNode render()
  {
    return null;
  }
}
