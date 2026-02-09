package com.example.optional_props;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RequiredChildrenWithManyRequired
{
  @Nullable
  @Input
  abstract ReactNode[] getChildren();

  @Nullable
  @Input
  abstract String getMyRequiredProp1();

  @Nullable
  @Input
  abstract String getMyRequiredProp2();

  @Nullable
  @Input
  abstract String getMyRequiredProp3();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
