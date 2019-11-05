package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MissingPropPropValidate
  extends Component
{
  @PropValidate
  public void validateMyProp( String prop )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
