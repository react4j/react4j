package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class FloatPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( float prop )
  {
  }

  @Prop
  abstract float getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
