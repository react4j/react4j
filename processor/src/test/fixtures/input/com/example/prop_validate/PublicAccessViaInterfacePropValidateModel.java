package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PublicAccessViaInterfacePropValidateModel
  extends Component
  implements PropValidateInterface
{
  @Override
  @PropValidate
  public void validateMyProp( String prop )
  {
  }

  @Prop
  abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}