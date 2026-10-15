package com.example.prop;

import java.io.IOException;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class PropThrowsExceptionComponent
{
  @Input
  protected abstract String getMyProp()
    throws IOException;

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
