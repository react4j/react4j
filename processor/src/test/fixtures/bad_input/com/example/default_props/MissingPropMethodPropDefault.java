package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MissingPropMethodPropDefault
{
  @PropDefault
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
