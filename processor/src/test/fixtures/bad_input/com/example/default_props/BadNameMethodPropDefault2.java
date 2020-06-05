package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BadNameMethodPropDefault2
{
  @InputDefault( name = "assert" )
  public static String zoooooooooooooom()
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
