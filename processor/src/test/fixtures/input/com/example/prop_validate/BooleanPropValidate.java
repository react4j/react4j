package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BooleanPropValidate
{
  @PropValidate
  void validateMyProp( boolean prop )
  {
  }

  @Prop
  abstract boolean getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
