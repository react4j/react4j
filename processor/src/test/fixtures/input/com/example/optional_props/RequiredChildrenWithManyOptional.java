package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RequiredChildrenWithManyOptional
{
  @InputDefault
  static final String DEFAULT_MY_PROP_A = "Foo";
  @InputDefault
  static final String DEFAULT_MY_PROP_B = "Foo";
  @InputDefault
  static final String DEFAULT_MY_PROP_C = "Foo";
  @InputDefault
  static final String DEFAULT_MY_PROP_D = "Foo";

  @Input
  abstract ReactNode[] getChildren();

  @Input
  abstract String getMyPropA();

  @Input
  abstract String getMyPropB();

  @Input
  abstract String getMyPropC();

  @Input
  abstract String getMyPropD();

  @Render
  ReactNode render()
  {
    return null;
  }
}
