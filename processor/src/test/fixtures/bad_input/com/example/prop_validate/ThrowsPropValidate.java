package com.example.prop_validate;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsPropValidate
{
  @InputValidate
  void validateMyProp( String prop )
    throws IOException
  {
  }

  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
