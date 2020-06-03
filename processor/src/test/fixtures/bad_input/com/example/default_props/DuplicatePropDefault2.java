package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicatePropDefault2
{
  @PropDefault( name = "myProp" )
  public static String getMyPropDefault1()
  {
    return "Foo";
  }

  @PropDefault( name = "myProp" )
  public static String getMyPropDefault2()
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
