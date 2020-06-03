package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class RequiredChildrenWithOptionalAndRequired
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Prop
  abstract ReactNode[] getChildren();

  @Prop
  abstract String getMyProp();

  @Prop
  abstract String getMyRequiredProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
