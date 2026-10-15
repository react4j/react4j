package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

/**
 * This prop has multiple optional props
 */
@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class MultiPropComponent4
{
  @Nullable
  @Input
  abstract String getMyProp();

  @Input
  @Nullable
  abstract String getMyProp2();

  @Input
  @Nullable
  abstract String getMyProp3();

  @Input
  @Nullable
  abstract String getMyProp4();

  @Input
  @Nullable
  abstract ReactNode getChild();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
