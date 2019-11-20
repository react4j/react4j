package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ShortPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( short prop )
  {
  }

  @Prop
  abstract short getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
