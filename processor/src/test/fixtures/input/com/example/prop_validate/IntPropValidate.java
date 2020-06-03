package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class IntPropValidate
{
  @PropValidate
  void validateMyProp( int prop )
  {
  }

  @Prop
  abstract int getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
