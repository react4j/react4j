package com.example.default_props;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsMethodPropDefault
{
  @InputDefault
  static String getMyPropDefault()
    throws IOException
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
