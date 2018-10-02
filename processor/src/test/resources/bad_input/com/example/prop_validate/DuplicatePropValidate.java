package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicatePropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( String prop )
  {
  }

  @PropValidate( name = "myProp" )
  void validateMyProp2( String prop )
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
