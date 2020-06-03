package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PropNotAbstractComponent
{
  @Prop
  protected String getMyProp()
  {
    return "";
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
