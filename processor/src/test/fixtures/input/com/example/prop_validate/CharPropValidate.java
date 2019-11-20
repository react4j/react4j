package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CharPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( char prop )
  {
  }

  @Prop
  abstract char getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
