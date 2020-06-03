package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicatePropDefault
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @PropDefault
  public static String getMyPropDefault()
  {
    return "Foo";
  }

  @Prop
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
