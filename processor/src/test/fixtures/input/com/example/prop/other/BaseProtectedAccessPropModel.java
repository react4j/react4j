package com.example.prop.other;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPropModel
{
  @Prop
  protected abstract String getMyProp();

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
