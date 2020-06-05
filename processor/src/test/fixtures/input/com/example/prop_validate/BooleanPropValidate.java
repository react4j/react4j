package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BooleanPropValidate
{
  @InputValidate
  void validateMyProp( boolean prop )
  {
  }

  @Input
  abstract boolean getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
