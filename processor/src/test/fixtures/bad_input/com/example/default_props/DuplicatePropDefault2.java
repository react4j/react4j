package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicatePropDefault2
{
  @InputDefault( name = "myProp" )
  public static String getMyPropDefault1()
  {
    return "Foo";
  }

  @InputDefault( name = "myProp" )
  public static String getMyPropDefault2()
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
