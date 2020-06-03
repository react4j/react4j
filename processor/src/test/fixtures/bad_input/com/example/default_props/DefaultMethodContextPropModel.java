package com.example.default_props;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class DefaultMethodContextPropModel
{
  @Prop( source = Prop.Source.CONTEXT )
  abstract String getMyProp();

  @PropDefault
  public static String getMyPropDefault()
  {
    return "Foo";
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
