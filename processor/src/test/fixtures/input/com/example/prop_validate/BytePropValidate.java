package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BytePropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( byte prop )
  {
  }

  @Prop
  abstract byte getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
