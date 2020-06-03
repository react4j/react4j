package com.example.default_props;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DefaultFieldContextPropModel
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Prop( source = Prop.Source.CONTEXT )
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
