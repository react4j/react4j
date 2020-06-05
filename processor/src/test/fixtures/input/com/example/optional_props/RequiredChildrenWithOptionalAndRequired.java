package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RequiredChildrenWithOptionalAndRequired
{
  @InputDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Input
  abstract ReactNode[] getChildren();

  @Input
  abstract String getMyProp();

  @Input
  abstract String getMyRequiredProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
