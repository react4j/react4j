package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ChildrenPropBadTypeComponent
{
  @Prop
  protected abstract int getChildren();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
