package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class MissingPropPropValidate
{
  @PropValidate
  public void validateMyProp( String prop )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
