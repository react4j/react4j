package com.example.optional_props;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class OptionalChildrenWithOptionalAndRequired
{
  @InputDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Nullable
  @Input( require = Feature.DISABLE )
  abstract ReactNode[] getChildren();

  @Nullable
  @Input
  abstract String getMyProp();

  @Nullable
  @Input
  abstract String getMyRequiredProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
