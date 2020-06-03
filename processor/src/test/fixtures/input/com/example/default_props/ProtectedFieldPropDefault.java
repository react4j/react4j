package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ProtectedFieldPropDefault
{
  @Nonnull
  @PropDefault
  protected static final String DEFAULT_MY_PROP = "Foo";

  @Prop
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
