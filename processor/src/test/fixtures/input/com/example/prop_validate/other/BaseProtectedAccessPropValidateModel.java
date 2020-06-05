package com.example.prop_validate.other;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPropValidateModel
{
  @InputValidate
  protected void validateMyProp( String prop )
  {
  }

  @Input
  protected abstract String getMyProp();

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
