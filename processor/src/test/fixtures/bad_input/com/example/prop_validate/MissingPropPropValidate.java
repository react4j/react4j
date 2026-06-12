package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class MissingPropPropValidate
{
  @InputValidate
  public void validateMyProp( String prop )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
