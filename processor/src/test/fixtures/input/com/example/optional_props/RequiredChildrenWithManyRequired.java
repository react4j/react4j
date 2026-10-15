package com.example.optional_props;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class RequiredChildrenWithManyRequired
{
  @Input
  abstract ReactNode @Nullable [] getChildren();

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
