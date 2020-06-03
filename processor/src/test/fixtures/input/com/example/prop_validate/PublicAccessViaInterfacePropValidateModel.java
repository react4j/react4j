package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PublicAccessViaInterfacePropValidateModel
  implements PropValidateInterface
{
  @PropValidate
  public void validateMyProp( String prop )
  {
  }

  @Prop
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
