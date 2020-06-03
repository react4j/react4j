package com.example.prop;

import java.io.IOException;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class PropThrowsExceptionComponent
{
  @Prop
  protected abstract String getMyProp()
    throws IOException;

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
