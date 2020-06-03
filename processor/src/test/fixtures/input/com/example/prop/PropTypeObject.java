package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class PropTypeObject
{
  public static class MyObject
  {
  }

  @Prop
  abstract MyObject getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
