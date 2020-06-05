package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class NullablePropAndNonnullChildComponent
{
  @Input
  @Nonnull
  abstract String getMyProp();

  @Input
  @Nullable
  abstract String getMyProp2();

  @Input
  abstract ReactNode getChild();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
