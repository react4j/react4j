package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MissingPropFieldPropDefault
{
  @InputDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Render
  ReactNode render()
  {
    return null;
  }
}
