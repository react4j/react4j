package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class NullablePropAndNonnullChildComponent
{
  @Input

  abstract String getMyProp();

  @Input
  @Nullable
  abstract String getMyProp2();

  @Nullable
  @Input
  abstract ReactNode getChild();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
