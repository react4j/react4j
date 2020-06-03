package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ProtectedMethodPropDefault
{
  @Nonnull
  @PropDefault
  protected static String getMyPropDefault()
  {
    return "Foo";
  }

  @Prop
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
