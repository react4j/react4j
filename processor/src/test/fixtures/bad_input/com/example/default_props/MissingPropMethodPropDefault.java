package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
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
