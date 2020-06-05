package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BadTypeMethodPropDefault
{
  @InputDefault
  static int getMyPropDefault()
  {
    return 0;
  }

  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
