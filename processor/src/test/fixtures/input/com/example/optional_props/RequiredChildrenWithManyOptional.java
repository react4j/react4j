package com.example.optional_props;

import javax.annotation.Nullable;
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

  @Nullable
  @Input
  abstract ReactNode[] getChildren();

  @Nullable
  @Input
  abstract String getMyPropA();

  @Nullable
  @Input
  abstract String getMyPropB();

  @Nullable
  @Input
  abstract String getMyPropC();

  @Nullable
  @Input
  abstract String getMyPropD();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
