package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MultiPropComponent3
{
  @Prop
  abstract ReactNode getChild();

  @Prop
  abstract String getMyProp();

  @Prop
  abstract String getMyProp2();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
