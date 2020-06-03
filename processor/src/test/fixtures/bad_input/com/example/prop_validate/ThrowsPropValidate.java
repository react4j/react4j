package com.example.prop_validate;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsPropValidate
{
  @PropValidate
  void validateMyProp( String prop )
    throws IOException
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
