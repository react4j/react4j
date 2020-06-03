package com.example.default_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class PackageAccessFieldPropDefault
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Prop
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
