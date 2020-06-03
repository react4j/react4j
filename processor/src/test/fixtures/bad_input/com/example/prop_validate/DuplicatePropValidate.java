package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicatePropValidate
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

  @Render
  ReactNode render()
  {
    return null;
  }
}
