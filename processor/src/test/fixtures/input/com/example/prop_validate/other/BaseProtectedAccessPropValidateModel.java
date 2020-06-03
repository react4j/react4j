package com.example.prop_validate.other;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPropValidateModel
{
  @PropValidate
  protected void validateMyProp( String prop )
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
