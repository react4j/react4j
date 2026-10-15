package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class MultiPropComponent2
{
  @Nullable
  @Input
  abstract String getMyProp();

  @Input
  abstract ReactNode @Nullable [] getChildren();

  @Nullable
  @Input
  abstract String getMyProp2();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
