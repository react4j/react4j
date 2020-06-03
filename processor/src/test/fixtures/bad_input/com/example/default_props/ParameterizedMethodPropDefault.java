package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ParameterizedMethodPropDefault
{
  @PropDefault
  static String getMyPropDefault( int i )
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
