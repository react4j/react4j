package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class IntPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( int prop )
  {
  }

  @Prop
  abstract int getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
