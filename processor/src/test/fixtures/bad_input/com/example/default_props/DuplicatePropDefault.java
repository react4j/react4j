package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicatePropDefault
{
  @InputDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @InputDefault
  public static String getMyPropDefault()
  {
    return "Foo";
  }

  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
