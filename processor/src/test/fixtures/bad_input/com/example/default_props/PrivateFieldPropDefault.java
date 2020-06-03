package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PrivateFieldPropDefault
{
  @PropDefault
  private static final String DEFAULT_MY_PROP = "Foo";

  @Prop
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
