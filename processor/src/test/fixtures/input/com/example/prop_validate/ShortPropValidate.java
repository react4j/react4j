package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ShortPropValidate
{
  @InputValidate
  void validateMyProp( short prop )
  {
  }

  @Input
  abstract short getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
