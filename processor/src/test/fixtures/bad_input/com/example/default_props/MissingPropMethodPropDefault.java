package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MissingPropMethodPropDefault
{
  @InputDefault
  static String getMyPropDefault()
  {
    return "Foo";
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
