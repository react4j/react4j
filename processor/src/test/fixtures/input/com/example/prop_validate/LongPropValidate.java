package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class LongPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( long prop )
  {
  }

  @Prop
  abstract long getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
