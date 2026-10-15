package com.example.optional_props;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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
  abstract ReactNode @Nullable [] getChildren();

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
