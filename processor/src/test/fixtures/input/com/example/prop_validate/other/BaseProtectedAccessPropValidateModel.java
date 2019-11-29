package com.example.prop_validate.other;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;

public abstract class BaseProtectedAccessPropValidateModel
  extends Component
{
  @PropValidate
  protected void validateMyProp( String prop )
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
